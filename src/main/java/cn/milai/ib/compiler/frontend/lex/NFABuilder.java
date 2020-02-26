package cn.milai.ib.compiler.frontend.lex;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cn.milai.ib.compiler.ex.IBCompilerException;

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
	public static NFAStatus newNFA(Set<TokenDef> tokens) {
		if (tokens.isEmpty()) {
			throw new IllegalArgumentException("tokens 不能为空");
		}
		List<NFAStatus> firsts = Lists.newArrayList();
		for (TokenDef token : tokens) {
			NFAPair pair = null;
			try {
				pair = transfer(new CharInput(token.getRE()));
			} catch (Exception e) {
				throw new IBCompilerException(String.format("输入的 %s 不是合法的正则表达式", token.getRE()), e);
			}
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
	 * 将 input 的正则表达式转换为 NFA
	 * @param input
	 * @return
	 */
	private static NFAPair transfer(CharInput input) {
		NFAPair nfa = null;
		while (true) {
			if (!input.hasNext()) {
				if (nfa == null) {
					throw new IBCompilerException(String.format("正则表达式构建的 NFA 为空"));
				}
				return nfa;
			}
			nfa = NFAPair.connect(nfa, parseNextNFA(input));
		}
	}

	/**
	 * 将 input 在 endChar 之前的正则表达式转换为 NFA
	 * @param input
	 * @param endChar
	 * @return
	 */
	private static NFAPair transfer(CharInput input, char endChar) {
		NFAPair nfa = null;
		while (true) {
			if (!input.hasNext()) {
				throw new IBCompilerException("结束符 " + endChar + " 出现前遇到输入结束");
			}
			if (input.getNext() == endChar) {
				if (nfa == null) {
					throw new IBCompilerException(String.format("正则表达式构建的 NFA 为空"));
				}
				input.next();
				return nfa;
			}
			nfa = NFAPair.connect(nfa, parseNextNFA(input));
		}
	}

	/**
	 * 解析并返回当前层次的下一个 NFA
	 * @param endChar
	 * @return
	 */
	private static NFAPair parseNextNFA(CharInput input) {
		NFAPair nfa = null;
		switch (input.getNext()) {
			case Char.SET_LEFT_CHAR : {
				input.next();
				nfa = dealRepeat(input, dealInputSet(input));
				break;
			}
			case Char.COMP_LEFT_CHAR : {
				input.next();
				nfa = dealRepeat(input, transfer(input, Char.COMP_RIGHT_CHAR));
				break;
			}
			case Char.SLASH : {
				input.next();
				nfa = dealRepeat(input, Char.slash(input.next()));
				break;
			}
			default: {
				char ch = input.next();
				nfa = dealRepeat(input, ch == Char.INVERT_CRLF ? Char.invertCRLF() : Sets.newHashSet(ch));
				break;
			}
		}
		if (input.hasNext() && input.getNext() == Char.OR) {
			input.next();
			nfa = NFAPair.paralell(nfa, parseNextNFA(input));
		}
		return nfa;
	}

	private static NFAPair dealRepeat(CharInput input, Set<Character> inputSet) {
		if (!input.hasNext()) {
			return new NFAPair(inputSet);
		}
		if (input.getNext() == Char.NONE_OR_ONE) {
			input.next();
			return noneOrOneNFA(inputSet);
		}
		if (input.getNext() == Char.ONE_OR_MORE) {
			input.next();
			return oneOrMoreNFA(inputSet);
		}
		if (input.getNext() == Char.NONE_OR_MORE) {
			input.next();
			return noneOrMoreNFA(inputSet);
		}
		if (input.getNext() == Char.BLOCK_LEFT_CHAR) {
			throw new IBCompilerException("暂不支持 {x,x} 表示重复的形式");
		}
		return new NFAPair(inputSet);
	}

	private static NFAPair dealRepeat(CharInput input, NFAPair pair) {
		if (!input.hasNext()) {
			return pair;
		}
		if (input.getNext() == Char.NONE_OR_ONE) {
			input.next();
			return noneOrOneNFA(pair);
		}
		if (input.getNext() == Char.ONE_OR_MORE) {
			input.next();
			return oneOrMoreNFA(pair);
		}
		if (input.getNext() == Char.NONE_OR_MORE) {
			input.next();
			return noneOrMoreNFA(pair);
		}
		if (input.getNext() == Char.BLOCK_LEFT_CHAR) {
			throw new IBCompilerException("暂不支持 {x,x} 表示重复的形式");
		}
		return pair;
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
	 * @param pair
	 * @return
	 */
	private static NFAPair oneOrMoreNFA(NFAPair pair) {
		NFAStatus s0 = new NFAStatus();
		NFAStatus s1 = new NFAStatus();
		s0.addEdge(pair.getFirst());
		pair.getLast().addEdge(pair.getFirst());
		pair.getLast().addEdge(s1);
		return new NFAPair(s0, s1);
	}

	/**
	 * 构造一个如下图示的 NFA
	 *                 ↓--ϵ--+
	 * S0--ϵ-->S1-->S2--ϵ-->S3
	 *  +-----------------------↑
	 * 其中 S0 为开始状态，S3 为结束状态
	 * @param inputSet
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
	 * @param pair
	 * @return
	 */
	private static NFAPair noneOrMoreNFA(NFAPair pair) {
		NFAStatus s0 = new NFAStatus();
		NFAStatus s1 = new NFAStatus();
		s0.addEdge(pair.getFirst());
		s0.addEdge(s1);
		pair.getLast().addEdge(pair.getFirst());
		pair.getLast().addEdge(s1);
		return new NFAPair(s0, s1);
	}

	/**
	 * 构造一个如下图示的 NFA
	 * S0--ϵ-->S1-->S2--ϵ-->S3
	 *  +-----------------------↑
	 * 其中 S0 为开始状态，S3 为结束状态
	 * @param s
	 * @return
	 */
	private static NFAPair noneOrOneNFA(Set<Character> inputSet) {
		NFAStatus s0 = new NFAStatus();
		NFAStatus s1 = new NFAStatus();
		NFAStatus s2 = new NFAStatus();
		NFAStatus s3 = new NFAStatus();
		s0.addEdge(s1);
		s0.addEdge(s3);
		s1.addEdge(inputSet, s2);
		s2.addEdge(s3);
		return new NFAPair(s0, s3);
	}

	/**
	 * 构造一个如下图示的 NFA
	 * S0--ϵ-->---NFA---ϵ-->S1
	 *  +-----------------------↑
	 * @param pair
	 * @return
	 */
	private static NFAPair noneOrOneNFA(NFAPair pair) {
		NFAStatus s0 = new NFAStatus();
		NFAStatus s1 = new NFAStatus();
		s0.addEdge(pair.getFirst());
		pair.getLast().addEdge(s1);
		s0.addEdge(s1);
		return new NFAPair(s0, s1);
	}

	/**
	 * 转换使用 [] 括起来的字符集合
	 * @param input
	 * @param endBracket
	 * @return
	 */
	private static Set<Character> dealInputSet(CharInput input) {
		boolean invert = false;
		if (input.getNext() == Char.INVERT) {
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
			if (input.getNext() != Char.RANGE) {
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
