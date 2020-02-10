package cn.milai.ib.compiler.frontend.lex;

import java.util.Set;

/**
 * 两个 NFA 节点的组合，用于表示 NFA 的中间状态
 * @author milai
 * @date 2020.02.04
 */
public class NFAPair {

	private NFAStatus first;

	private NFAStatus last;

	NFAPair(NFAStatus first, NFAStatus last) {
		this.first = first;
		this.last = last;
	}

	/**
	 * 构建一个以 ch 为边连接的两个节点的 NFA
	 * @param ch
	 */
	NFAPair(Set<Character> set) {
		this.first = new NFAStatus();
		this.last = new NFAStatus();
		first.addEdge(set, last);
	}

	public NFAStatus getFirst() {
		return first;
	}

	public NFAStatus getLast() {
		return last;
	}

	/**
	 * 连接两个 NFAPair
	 * @param nfa1
	 * @param nfa2
	 * @return
	 */
	public static NFAPair connect(NFAPair nfa1, NFAPair nfa2) {
		if (nfa1 == null) {
			return nfa2;
		}
		if (nfa2 == null) {
			return nfa1;
		}
		nfa1.last.addEdge(nfa2.first);
		return new NFAPair(nfa1.first, nfa2.last);
	}

	/**
	 * 并联两个 NFAPair
	 * @param nfa1
	 * @param nfa2
	 * @return
	 */
	public static NFAPair paralell(NFAPair nfa1, NFAPair nfa2) {
		if (nfa1 == null) {
			return nfa2;
		}
		if (nfa2 == null) {
			return nfa1;
		}
		NFAStatus first = new NFAStatus();
		NFAStatus last = new NFAStatus();
		first.addEdge(nfa2.first);
		first.addEdge(nfa1.first);
		nfa2.last.addEdge(last);
		nfa1.last.addEdge(last);
		return new NFAPair(first, last);
	}

	@Override
	public String toString() {
		return first.toString();
	}

}
