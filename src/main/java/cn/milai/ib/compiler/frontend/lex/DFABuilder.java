package cn.milai.ib.compiler.frontend.lex;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import cn.milai.ib.compiler.ex.IBCompilerException;

/**
 * DFA 构造器
 * 使用子集构造法将 NFA 转换为 DFA ,再使用 Hopcroft 算法最小化 DFA 
 * @author milai
 * @date 2020.02.10
 */
public class DFABuilder {

	public static DFA newDFA(NFA nfa) {
		Map<Set<NFAStatus>, DFAStatus> status = Maps.newHashMap();
		Queue<Set<NFAStatus>> q = Queues.newConcurrentLinkedQueue();
		Set<NFAStatus> firstSet = closure(nfa.getFirst());
		status.put(firstSet, new DFAStatus());
		q.add(firstSet);
		while (!q.isEmpty()) {
			Set<NFAStatus> fromSet = q.poll();
			for (Character ch : Char.all()) {
				Set<NFAStatus> toSet = closure(nextStatusOf(fromSet, ch));
				if (toSet.isEmpty()) {
					continue;
				}
				if (!status.containsKey(toSet)) {
					status.put(toSet, new DFAStatus());
					q.add(toSet);
				}
				status.get(fromSet).putEdge(ch, status.get(toSet));
			}
		}
		return new DFA(minimize(status.get(firstSet), Sets.newHashSet(status.values())));
	}

	/**
	 * 使用 Hopcroft 算法最小化 DFA
	 * @param s0 
	 * @param status
	 */
	private static DFAStatus minimize(DFAStatus s0, Set<DFAStatus> status) {
		status.remove(s0);
		Set<Set<DFAStatus>> nows = Sets.newHashSet();
		nows.add(Sets.newHashSet(s0));
		nows.add(status);
		Set<Set<DFAStatus>> pres = null;
		while (!nows.equals(pres)) {
			pres = nows;
			nows = Sets.newHashSet();
			for (Set<DFAStatus> p : pres) {
				nows.addAll(split(pres, p));
			}
		}
		return rebuildDFA(Lists.newArrayList(nows));
	}

	/**
	 * 尝试根据某个字符将 p 中状态分为多份
	 * @param pres
	 * @param p
	 * @return
	 */
	private static Set<Set<DFAStatus>> split(Set<Set<DFAStatus>> pres, Set<DFAStatus> p) {
		if (p.size() >= 2) {
			for (char ch : Char.all()) {
				Map<Set<DFAStatus>, Set<DFAStatus>> map = Maps.newHashMap();
				for (DFAStatus status : p) {
					Set<DFAStatus> key = setOf(pres, status.next(ch));
					if (!map.containsKey(key)) {
						map.put(key, Sets.newHashSet());
					}
					map.get(key).add(status);
				}
				if (map.size() >= 2) {
					return Sets.newHashSet(map.values());
				}
			}
		}
		Set<Set<DFAStatus>> result = Sets.newHashSet();
		result.add(p);
		return result;
	}

	/**
	 * 获取 s 所属的 set
	 * @param pres
	 * @param s
	 * @return
	 */
	private static Set<DFAStatus> setOf(Set<Set<DFAStatus>> pres, DFAStatus s) {
		if (s == null) {
			return null;
		}
		for (Set<DFAStatus> pre : pres) {
			if (pre.contains(s)) {
				return pre;
			}
		}
		throw new IBCompilerException(String.format("没有找到 %s 所属 set", s));
	}

	/**
	 * 将 List 中每个 Set 中的状态合并，形成一个新的 DFA，并返回头结点
	 * @param nows
	 * @return
	 */
	private static DFAStatus rebuildDFA(List<Set<DFAStatus>> nows) {
		List<DFAStatus> status = Lists.newArrayList();
		for (int i = 0; i < nows.size(); i++) {
			status.add(new DFAStatus());
		}
		for (int i = 0; i < nows.size(); i++) {
			DFAStatus from = status.get(i);
			for (DFAStatus s : nows.get(i)) {
				for (Character ch : s.accepts()) {
					// 同一个 Set 的其他结点已经设置过，不需要重复设置
					if (from.next(ch) != null) {
						continue;
					}
					DFAStatus to = s.next(ch);
					from.putEdge(ch, status.get(indexOf(nows, to)));
				}
			}
		}
		return firstStatusOf(status);
	}

	/**
	 * 使用拓扑排序找到 DFA 的头结点
	 * @param status
	 * @return
	 */
	private static DFAStatus firstStatusOf(List<DFAStatus> status) {
		int[] in = new int[status.size()];
		for (DFAStatus s : status) {
			for (Character ch : s.accepts()) {
				int index = status.indexOf(s.next(ch));
				in[index]++;
			}
		}
		for (int i = 0; i < in.length; i++) {
			if (in[i] == 0) {
				return status.get(i);
			}
		}
		throw new IBCompilerException("找不到 DFA 头结点");
	}

	/**
	 * 获取 status 所属 Set 在 nows 中的下标，若不存在，返回 -1
	 * @param nows
	 * @param status
	 * @return
	 */
	private static int indexOf(List<Set<DFAStatus>> nows, DFAStatus status) {
		for (int i = 0; i < nows.size(); i++) {
			if (nows.get(i).contains(status)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 返回状态 status 通过 ch 能到达的所有状态的集合
	 * @param status
	 * @param ch
	 * @return
	 */
	private static Set<NFAStatus> nextStatusOf(Set<NFAStatus> status, char ch) {
		Set<NFAStatus> result = Sets.newHashSet();
		for (NFAStatus s : status) {
			for (Edge e : s.getEdges()) {
				if (e.isEpsilon()) {
					continue;
				}
				if (e.accept(ch)) {
					result.add(e.getTargetStatus());
				}
			}
		}
		return result;
	}

	/**
	 * 返回 set 中所有状态的闭包的并集
	 * @param set
	 * @return
	 */
	private static Set<NFAStatus> closure(Set<NFAStatus> set) {
		Set<NFAStatus> result = Sets.newHashSet();
		for (NFAStatus s : set) {
			result.add(s);
			result.addAll(closure(s));
		}
		return result;
	}

	/**
	 * 返回 s 的闭包（s 及与 s 通过 epsilon 边相连的所有状态）
	 * @param s
	 * @return
	 */
	private static Set<NFAStatus> closure(NFAStatus s) {
		return closure(s, Sets.newHashSet(s));
	}

	/**
	 * 将 found 中不存在的、s 闭包中的状态添加到 found
	 * @param s
	 * @param found 已经添加的状态
	 * @return
	 */
	private static Set<NFAStatus> closure(NFAStatus s, Set<NFAStatus> found) {
		for (Edge e : s.getEdges()) {
			if (e.isEpsilon() && found.add(e.getTargetStatus())) {
				found.addAll(closure(e.getTargetStatus(), found));
			}
		}
		return found;
	}
}
