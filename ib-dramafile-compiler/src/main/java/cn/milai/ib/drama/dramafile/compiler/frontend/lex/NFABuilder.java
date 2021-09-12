package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.List;
import java.util.Set;

import org.springframework.util.Assert;

import cn.milai.beginning.collection.Mapping;
import cn.milai.common.base.Chars;
import cn.milai.ib.drama.dramafile.compiler.ex.IBCompilerException;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor.CharAcceptor;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor.ExcludeAcceptor;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor.IncludeAcceptor;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor.RegexAcceptor;

/**
 * NFA(Non-deterministic-Finite-Automaton) 非确定性有限自动机的构造器。
 * 使用 Thompson 构造法，将正则表达式转换为 NFA
 * @author milai
 * @date 2020.02.04
 */
public class NFABuilder {

	/**
	 * 将多个正则表达式转换为一个 NFA
	 * @param tokens
	 * @return
	 */
	public static Node newNFA(Set<TokenDefinition> tokens) {
		Assert.notEmpty(tokens, "token 定义不能为空");
		return combine(Mapping.list(tokens, t -> fromRegex(t.getRegex(), t.getCode())));
	}

	/**
	 * 连接多个 NFA 的头节点，返回组合成的 NFA 的头节点
	 * @param firsts
	 * @return
	 */
	public static Node combine(List<Node> firsts) {
		if (firsts.size() == 1) {
			return firsts.get(0);
		}
		Node head = new NFANode();
		for (Node s : firsts) {
			head.addEpsilonNext(s);
		}
		return head;
	}

	/**
	 * 将正则表达式转换为 NFA，返回头结点
	 * @param regex
	 * @param 匹配成功时的 token 名
	 * @return
	 */
	private static Node fromRegex(String regex, String token) {
		CharScanner scanner = new CharScanner(regex);
		NFAPair pair = null;
		while (scanner.hasMore()) {
			pair = NFAPair.connect(pair, nextFromScanner(scanner));
		}
		Assert.notNull(pair, String.format("由正则表达式构造 NFA 失败: %s", regex));
		pair.getLast().addToken(token);
		return pair.getFirst();
	}

	/**
	 * 解析小括号 () 里的 NFA
	 * @param scanner
	 * @param endChar
	 * @return
	 */
	private static NFAPair parseParenthesis(CharScanner scanner) {
		NFAPair nfa = null;
		while (scanner.hasMore()) {
			if (scanner.now() == CharSets.CLOSE_PARENTHESIS) {
				Assert.notNull(nfa, "小括号中 NFA 为空");
				scanner.next();
				return nfa;
			}
			nfa = NFAPair.connect(nfa, nextFromScanner(scanner));
		}
		throw new IBCompilerException("未匹配到右小括号");
	}

	/**
	 * 解析并返回当前层次的下一个 {@link NFAPair}
	 * @param scanner
	 * @return
	 */
	private static NFAPair nextFromScanner(CharScanner scanner) {
		NFAPair nfa = null;
		char ch = scanner.next();
		switch (ch) {
			case CharSets.OPEN_BRACKET : {
				nfa = applyRepeat(scanner, new NFAPair(parseBracket(scanner)));
				break;
			}
			case CharSets.OPEN_PARENTHESIS : {
				nfa = applyRepeat(scanner, parseParenthesis(scanner));
				break;
			}
			case CharSets.SLASH : {
				nfa = applyRepeat(scanner, new NFAPair(CharSets.slash(scanner.next())));
				break;
			}
			default: {
				CharAcceptor acceptor = ch == CharSets.NOT_CRLF ? new ExcludeAcceptor(Chars.C_LF, Chars.C_CR)
					: new IncludeAcceptor(ch);
				nfa = applyRepeat(scanner, new NFAPair(acceptor));
				break;
			}
		}
		if (scanner.hasMore() && scanner.now() == CharSets.OR) {
			scanner.next();
			nfa = NFAPair.paralell(nfa, nextFromScanner(scanner));
		}
		return nfa;
	}

	private static NFAPair applyRepeat(CharScanner scanner, NFAPair pair) {
		if (!scanner.hasMore()) {
			return pair;
		}
		if (scanner.now() == CharSets.NONE_OR_ONE) {
			scanner.next();
			return noneOrOne(pair);
		}
		if (scanner.now() == CharSets.ONE_OR_MORE) {
			scanner.next();
			return oneOrMore(pair);
		}
		if (scanner.now() == CharSets.NONE_OR_MORE) {
			scanner.next();
			return noneOrMore(pair);
		}
		if (scanner.now() == CharSets.OPEN_BRACE) {
			throw new IBCompilerException("暂不支持 {x,y} 表示重复的形式");
		}
		return pair;
	}

	/**
	 * 构造一个如下图示的 NFA
	 *                  ↓--ϵ--+
	 * S0--ϵ-->---NFA---ϵ-->S1
	 * @param pair
	 * @return
	 */
	private static NFAPair oneOrMore(NFAPair pair) {
		NFANode s0 = new NFANode();
		NFANode s1 = new NFANode();
		s0.addEpsilonNext(pair.getFirst());
		pair.getLast().addEpsilonNext(pair.getFirst());
		pair.getLast().addEpsilonNext(s1);
		return new NFAPair(s0, s1);
	}

	/**
	 * 构造一个如下图示的 NFA
	 *                  ↓--ϵ--+
	 * S0--ϵ-->---NFA---ϵ-->S1
	 *  +-----------------------↑
	 * @param pair
	 * @return
	 */
	private static NFAPair noneOrMore(NFAPair pair) {
		NFANode s0 = new NFANode();
		NFANode s1 = new NFANode();
		s0.addEpsilonNext(pair.getFirst());
		s0.addEpsilonNext(s1);
		pair.getLast().addEpsilonNext(pair.getFirst());
		pair.getLast().addEpsilonNext(s1);
		return new NFAPair(s0, s1);
	}

	/**
	 * 构造一个如下图示的 NFA
	 * S0--ϵ-->---NFA---ϵ-->S1
	 *  +-----------------------↑
	 * @param pair
	 * @return
	 */
	private static NFAPair noneOrOne(NFAPair pair) {
		NFANode s0 = new NFANode();
		NFANode s1 = new NFANode();
		s0.addEpsilonNext(pair.getFirst());
		pair.getLast().addEpsilonNext(s1);
		s0.addEpsilonNext(s1);
		return new NFAPair(s0, s1);
	}

	/**
	 * 转换使用中括号 [] 括起来的字符集合
	 * @param scanner
	 * @param endBracket
	 * @return
	 */
	private static CharAcceptor parseBracket(CharScanner scanner) {
		StringBuilder regexBuilder = new StringBuilder();
		boolean lastSlash = false;
		while (scanner.hasMore()) {
			char ch = scanner.next();
			if (ch == CharSets.CLOSE_BRAKET && !lastSlash) {
				String regex = regexBuilder.toString();
				Assert.hasLength(regex, "中括号中正则表达式为空");
				return new RegexAcceptor(regex);
			}
			if (lastSlash) {
				lastSlash = false;
			} else {
				lastSlash = (ch == CharSets.SLASH);
			}
			regexBuilder.append(ch);
		}
		throw new IBCompilerException("未匹配到右中括号：regex = " + regexBuilder);
	}

}
