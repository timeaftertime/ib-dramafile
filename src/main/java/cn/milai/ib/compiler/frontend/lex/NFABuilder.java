package cn.milai.ib.compiler.frontend.lex;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cn.milai.ib.compiler.ex.IBCompilerException;
import cn.milai.ib.compiler.frontend.LexToken;

/**
 * NFA 构造器
 * 使用 Thompson 构造法，将正则表达式转换为 NFA
 * @author milai
 * @date 2020.02.04
 */
public class NFABuilder {

	/**
	 * 将多个正则表达式转换为一个 NFA
	 * @param res
	 * @return
	 */
	public static NFAStatus newNFA(List<LexToken> tokens) {
		if (tokens.isEmpty()) {
			throw new IllegalArgumentException("tokens 必须大于 0");
		}
		List<NFAStatus> firsts = Lists.newArrayList();
		for (LexToken token : tokens) {
			NFAPair pair = transfer(new StringInput(token.getRE()), Char.EOF);
			pair.getLast().setToken(token.getCode());
			firsts.add(pair.getFirst());
		}
		return combine(firsts);
	}

	/**
	 * 连接多个 NFA 的头节点，返回组合成的 NFA 的头节点
	 * @param firsts
	 * @return
	 */
	public static NFAStatus combine(List<NFAStatus> firsts) {
		if (firsts.size() == 1) {
			return firsts.get(0);
		}
		NFAStatus status = new NFAStatus();
		for (NFAStatus s : firsts) {
			status.addEdge(s);
		}
		return status;
	}

	/**
	 * 将 input 在 endChar 之前的正则表达式转换为 NFA
	 * @param input
	 * @param endChar
	 * @return
	 */
	private static NFAPair transfer(StringInput input, char endChar) {
		NFAPair nfa = null;
		while (true) {
			if (input.getNext() == endChar) {
				if (input.hasNext()) {
					input.next();
				}
				if (nfa == null) {
					throw new IBCompilerException(String.format("正则表达式构建的 NFA 为空"));
				}
				return nfa;
			}
			nfa = NFAPair.connect(nfa, parseNextNFA(input, endChar));
		}
	}

	/**
	 * 解析并返回当前层次的下一个 NFA
	 * @param input
	 * @param endChar
	 * @return
	 */
	private static NFAPair parseNextNFA(StringInput input, char endChar) {
		NFAPair nfa = null;
		char ch = input.next();
		switch (ch) {
			case Char.EOF:
				throw new IBCompilerException(String.format("括号匹配之前遇到输入终止符"));
			case Char.SET_LEFT_CHAR: {
				nfa = dealRepeat(input, dealInputSet(input));
				break;
			}
			case Char.COMP_LEFT_CHAR: {
				nfa = dealRepeat(input, transfer(input, Char.COMP_RIGHT_CHAR));
				break;
			}
			case Char.SLASH: {
				nfa = dealRepeat(input, Char.slash(input.next()));
				break;
			}
			default: {
				nfa = dealRepeat(input, Sets.newHashSet(ch));
				break;
			}
		}
		if (input.getNext() == Char.OR) {
			input.next();
			nfa = NFAPair.paralell(nfa, parseNextNFA(input, endChar));
		}
		return nfa;
	}

	private static NFAPair dealRepeat(StringInput input, Set<Character> inputSet) {
		if (!input.hasNext()) {
			return new NFAPair(inputSet);
		}
		if (input.getNext() == Char.ONE_OR_MORE_CHAR) {
			input.next();
			return oneOrMoreNFA(inputSet);
		}
		if (input.getNext() == Char.NONE_OR_MORE_CHAR) {
			input.next();
			return noneOrMoreNFA(inputSet);
		}
		if (input.getNext() == Char.BLOCK_LEFT_CHAR) {
			throw new IBCompilerException("暂不支持 {x,x} 表示重复的形式");
		}
		return new NFAPair(inputSet);
	}

	private static NFAPair dealRepeat(StringInput input, NFAPair nfa) {
		if (!input.hasNext()) {
			return nfa;
		}
		if (input.getNext() == Char.ONE_OR_MORE_CHAR) {
			input.next();
			return oneOrMoreNFA(nfa);
		}
		if (input.getNext() == Char.NONE_OR_MORE_CHAR) {
			input.next();
			return noneOrMoreNFA(nfa);
		}
		if (input.getNext() == Char.BLOCK_LEFT_CHAR) {
			throw new IBCompilerException("暂不支持 {x,x} 表示重复的形式");
		}
		return nfa;
	}

	/**
	 * 构造一个如下图示的 NFA
	 *                 ↓--ϵ--+
	 * S0--ϵ-->S1-->S2--ϵ-->S3
	 * @param s
	 * @return
	 */
	private static NFAPair oneOrMoreNFA(Set<Character> inputSet) {
		NFAStatus s0 = new NFAStatus();
		NFAStatus s1 = new NFAStatus();
		NFAStatus s2 = new NFAStatus();
		NFAStatus s3 = new NFAStatus();
		s0.addEdge(s1);
		s1.addEdge(inputSet, s2);
		s2.addEdge(s1);
		s2.addEdge(s3);
		return new NFAPair(s0, s3);
	}

	/**
	 * 构造一个如下图示的 NFA
	 *                  ↓--ϵ--+
	 * S0--ϵ-->---NFA---ϵ-->S1
	 * @param nfa
	 * @return
	 */
	private static NFAPair oneOrMoreNFA(NFAPair nfa) {
		NFAStatus s0 = new NFAStatus();
		NFAStatus s1 = new NFAStatus();
		s0.addEdge(nfa.getFirst());
		nfa.getLast().addEdge(nfa.getFirst());
		nfa.getLast().addEdge(s1);
		return new NFAPair(s0, s1);
	}

	/**
	 * 构造一个如下图示的 NFA
	 *                 ↓--ϵ--+
	 * S0--ϵ-->S1-->S2--ϵ-->S3
	 *  +-----------------------↑
	 * 其中 S0 为开始状态，S3 为结束状态
	 * @param s
	 * @return
	 */
	private static NFAPair noneOrMoreNFA(Set<Character> inputSet) {
		NFAStatus s0 = new NFAStatus();
		NFAStatus s1 = new NFAStatus();
		NFAStatus s2 = new NFAStatus();
		NFAStatus s3 = new NFAStatus();
		s0.addEdge(s1);
		s0.addEdge(s3);
		s1.addEdge(inputSet, s2);
		s2.addEdge(s1);
		s2.addEdge(s3);
		return new NFAPair(s0, s3);
	}

	/**
	 * 构造一个如下图示的 NFA
	 *                  ↓--ϵ--+
	 * S0--ϵ-->---NFA---ϵ-->S1
	 *  +-----------------------↑
	 * @param nfa
	 * @return
	 */
	private static NFAPair noneOrMoreNFA(NFAPair nfa) {
		NFAStatus s0 = new NFAStatus();
		NFAStatus s1 = new NFAStatus();
		s0.addEdge(nfa.getFirst());
		s0.addEdge(s1);
		nfa.getLast().addEdge(nfa.getFirst());
		nfa.getLast().addEdge(s1);
		return new NFAPair(s0, s1);
	}

	/**
	 * 转换使用 [] 括起来的字符集合
	 * @param input
	 * @param endBracket
	 * @return
	 */
	private static Set<Character> dealInputSet(StringInput input) {
		boolean invert = false;
		if (input.getNext() == Char.INVERT_CHAR) {
			input.next();
			invert = true;
		}
		Set<Character> set = Sets.newHashSet();
		while (input.hasNext()) {
			char ch = input.next();
			if (ch == Char.SET_RIGHT_CHAR) {
				return invert ? Char.invert(set) : set;
			}
			if (ch == Char.SLASH) {
				set.addAll(Char.slash(input.next()));
				continue;
			}
			if (input.getNext() != Char.RANGE_CHAR) {
				set.add(ch);
			} else {
				input.next();
				char nxt = input.next();
				if (nxt == Char.SET_RIGHT_CHAR) {
					throw new IBCompilerException(String.format("非法范围符号：%c-", nxt));
				}
				set.addAll(Char.range(ch, nxt));
			}
		}
		throw new IBCompilerException("未匹配到右括号：" + Char.SET_RIGHT_CHAR);
	}

}
