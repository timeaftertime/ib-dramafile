package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor.CharAcceptor;

/**
 * NFA 节点组合，用于保存 NFA 中间状态
 * @author milai
 * @date 2020.02.04
 */
public class NFAPair {

	private NFANode first;

	private NFANode last;

	NFAPair(NFANode first, NFANode last) {
		this.first = first;
		this.last = last;
	}

	/**
	 * 构建一个以指定 {@link CharAcceptor} 为边连接的两个节点的 NFA
	 * @param acceptor
	 */
	NFAPair(CharAcceptor acceptor) {
		this.first = new NFANode();
		this.last = new NFANode();
		first.addNext(acceptor, last);
	}

	public NFANode getFirst() { return first; }

	public NFANode getLast() { return last; }

	/**
	 * 串联两个 NFAPair
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
		nfa1.last.addEpsilonNext(nfa2.first);
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
		NFANode first = new NFANode();
		NFANode last = new NFANode();
		first.addEpsilonNext(nfa2.first);
		first.addEpsilonNext(nfa1.first);
		nfa2.last.addEpsilonNext(last);
		nfa1.last.addEpsilonNext(last);
		return new NFAPair(first, last);
	}

	@Override
	public String toString() {
		return first.toString();
	}

}
