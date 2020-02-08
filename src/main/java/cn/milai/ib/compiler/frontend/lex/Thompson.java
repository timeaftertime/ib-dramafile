package cn.milai.ib.compiler.frontend.lex;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import cn.milai.ib.compiler.ex.IBCompilerException;

/**
 * Thompson 构造法，将正则表达式转换为 NFA
 * @author milai
 * @date 2020.02.04
 */
public class Thompson {

	/**
	 * 将多个正则表达式转换为一个 NFA
	 * @param res
	 * @return
	 */
	public static NFA transfer(List<String> res) {
		NFA nfa = null;
		for (String re : res) {
			nfa = NFA.paralell(nfa, transfer(new StringInput(re), Char.EOF));
		}
		return nfa;
	}

	/**
	 * 将 input 在 endChar 之前的正则表达式转换为 NFA
	 * @param input
	 * @param endChar
	 * @return
	 */
	private static NFA transfer(StringInput input, char endChar) {
		NFA nfa = null;
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
			nfa = NFA.connect(nfa, parseNextNFA(input, endChar));
		}
	}

	/**
	 * 解析并返回当前层次的下一个 NFA
	 * @param input
	 * @param endChar
	 * @return
	 */
	private static NFA parseNextNFA(StringInput input, char endChar) {
		NFA nfa = null;
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
			nfa = NFA.paralell(nfa, parseNextNFA(input, endChar));
		}
		return nfa;
	}

	private static NFA dealRepeat(StringInput input, Set<Character> inputSet) {
		if (!input.hasNext()) {
			return new NFA(inputSet);
		}
		if (input.getNext() == Char.ONE_OR_MORE_CHAR) {
			input.next();
			return oneOrMoreNFA(inputSet);
		}
		if (input.getNext() == Char.NONE_OR_MORE_CHAR) {
			input.next();
			return noneOrMoreNFA(inputSet);
		}
		if (input.getNext() == Char.TIMES_LEFT_CHAR) {
			throw new IBCompilerException("暂不支持 {x,x} 表示重复的形式");
		}
		return new NFA(inputSet);
	}

	private static NFA dealRepeat(StringInput input, NFA nfa) {
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
		if (input.getNext() == Char.TIMES_LEFT_CHAR) {
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
	private static NFA oneOrMoreNFA(Set<Character> inputSet) {
		Status s0 = new Status();
		Status s1 = new Status();
		Status s2 = new Status();
		Status s3 = new Status();
		s0.addEdge(s1);
		s1.addEdge(inputSet, s2);
		s2.addEdge(s1);
		s2.addEdge(s3);
		return new NFA(s0, s3);
	}

	/**
	 * 构造一个如下图示的 NFA
	 *                  ↓--ϵ--+
	 * S0--ϵ-->---NFA---ϵ-->S1
	 * @param nfa
	 * @return
	 */
	private static NFA oneOrMoreNFA(NFA nfa) {
		Status s0 = new Status();
		Status s1 = new Status();
		s0.addEdge(nfa.getFirst());
		nfa.getLast().addEdge(nfa.getFirst());
		nfa.getLast().addEdge(s1);
		return new NFA(s0, s1);
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
	private static NFA noneOrMoreNFA(Set<Character> inputSet) {
		Status s0 = new Status();
		Status s1 = new Status();
		Status s2 = new Status();
		Status s3 = new Status();
		s0.addEdge(s1);
		s0.addEdge(s3);
		s1.addEdge(inputSet, s2);
		s2.addEdge(s1);
		s2.addEdge(s3);
		return new NFA(s0, s3);
	}

	/**
	 * 构造一个如下图示的 NFA
	 *                  ↓--ϵ--+
	 * S0--ϵ-->---NFA---ϵ-->S1
	 *  +-----------------------↑
	 * @param nfa
	 * @return
	 */
	private static NFA noneOrMoreNFA(NFA nfa) {
		Status s0 = new Status();
		Status s1 = new Status();
		s0.addEdge(nfa.getFirst());
		s0.addEdge(s1);
		nfa.getLast().addEdge(nfa.getFirst());
		nfa.getLast().addEdge(s1);
		return new NFA(s0, s1);
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
