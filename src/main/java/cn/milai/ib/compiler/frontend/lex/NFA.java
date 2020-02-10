package cn.milai.ib.compiler.frontend.lex;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

/**
 * 非确定有限自动机
 * @author milai
 * @date 2020.02.04
 */
public class NFA {

	private NFAStatus first;

	private NFAStatus last;

	public NFA(NFAStatus first, NFAStatus last) {
		this.first = first;
		this.last = last;
	}

	/**
	 * 构建一个以 ch 为边连接的两个节点的 NFA
	 * @param ch
	 */
	public NFA(Set<Character> set) {
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
	 * 连接两个 NFA
	 * @param nfa1
	 * @param nfa2
	 * @return
	 */
	public static NFA connect(NFA nfa1, NFA nfa2) {
		if (nfa1 == null) {
			return nfa2;
		}
		if (nfa2 == null) {
			return nfa1;
		}
		nfa1.last.addEdge(nfa2.first);
		return new NFA(nfa1.first, nfa2.last);
	}

	/**
	 * 并联两个 NFA
	 * @param nfa1
	 * @param nfa2
	 * @return
	 */
	public static NFA paralell(NFA nfa1, NFA nfa2) {
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
		return new NFA(first, last);
	}

	/**
	 * 深度复制一个 NFA
	 * @param nfa
	 * @return
	 */
	public static NFA copy(NFA nfa) {
		Map<NFAStatus, NFAStatus> copied = Maps.newHashMap();
		copyStatus(nfa.first, copied);
		return new NFA(copied.get(nfa.first), copied.get(nfa.last));
	}

	/**
	 * 从 status 开始递归复制一个 NFA 
	 * 若 status 在 copied 中已经存在将使用该 status ，否则新建一个 
	 * @param status
	 * @param copied
	 * @return
	 */
	private static NFAStatus copyStatus(NFAStatus status, Map<NFAStatus, NFAStatus> copied) {
		NFAStatus newStatus = copiedStatus(status, copied);
		for (Edge e : status.getEdges()) {
			if (e.isEpsilon()) {
				newStatus.addEdge(copyStatus(e.getTargetStatus(), copied));
			}
		}
		return newStatus;
	}

	/**
	 * 获取 status 对应的已经复制的状态（若存在）或新建一个（若不存在）
	 * @param status
	 * @param copied
	 * @return
	 */
	private static NFAStatus copiedStatus(NFAStatus status, Map<NFAStatus, NFAStatus> copied) {
		if (copied.containsKey(status)) {
			return copied.get(status);
		}
		NFAStatus s = new NFAStatus();
		copied.put(status, s);
		return s;
	}

	@Override
	public String toString() {
		return first.toString();
	}

}
