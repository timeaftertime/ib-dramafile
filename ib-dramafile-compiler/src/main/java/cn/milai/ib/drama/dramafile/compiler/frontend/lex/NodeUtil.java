package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import cn.milai.beginning.collection.Filter;
import cn.milai.beginning.collection.Mapping;

/**
 * DFA 工具类
 * @author milai
 * @date 2021.09.08
 */
public class NodeUtil {

	/**
	 * 使用 Hopcroft 算法最小化 DFA
	 * @param first
	 */
	public static Node minimize(Node first) {
		Set<Node> nodes = allStatusOf(first);
		Set<Node> acceptStatus = nodes.stream().filter(Node::isAccept).collect(Collectors.toSet());
		Set<Set<Node>> nows = new HashSet<>();
		nows.add(Filter.nset(nodes, s -> s.isAccept()));
		nows.add(acceptStatus);
		Set<Set<Node>> pres = null;
		while (!nows.equals(pres)) {
			pres = nows;
			nows = new HashSet<>();
			for (Set<Node> p : pres) {
				nows.addAll(split(pres, p));
			}
		}
		return rebuildDFA(new ArrayList<>(nows));
	}

	/**
	 * 遍历 {@link Node} 返回所有其连接的所有结点的集合
	 * @param first
	 * @return
	 */
	private static Set<Node> allStatusOf(Node first) {
		Queue<Node> q = new ConcurrentLinkedQueue<>();
		Set<Node> visited = new HashSet<>();
		q.add(first);
		while (!q.isEmpty()) {
			Node now = q.poll();
			visited.add(now);
			for (char ch : now.accepts()) {
				Node next = now.next(ch);
				if (!visited.contains(next)) {
					q.add(next);
				}
			}
		}
		return visited;
	}

	/**
	 * 尝试根据某个字符将结点集合分为多份
	 * @param pres
	 * @param toBeSplit
	 * @return
	 */
	private static Set<Set<Node>> split(Set<Set<Node>> pres, Set<Node> toBeSplit) {
		if (toBeSplit.size() >= 2) {
			Set<Character> accepted = Mapping.reduceSet(
				pres, set -> Mapping.reduceSet(set, status -> status.accepts())
			);
			for (char ch : accepted) {
				Map<Set<Node>, Set<Node>> fromAndTo = new HashMap<>();
				for (Node s : toBeSplit) {
					Node next = s.next(ch);
					Set<Node> key = next == null ? null : Filter.first(pres, set -> set.contains(next)).orElse(null);
					fromAndTo.computeIfAbsent(key, k -> new HashSet<>()).add(s);
				}
				if (fromAndTo.size() >= 2) {
					return new HashSet<>(fromAndTo.values());
				}
			}
		}
		Set<Set<Node>> result = new HashSet<>();
		result.add(toBeSplit);
		return result;
	}

	/**
	 * 将 {@link List} 中每个相等状态集合合并为一个结点，形成一个新的 DFA，并返回头结点
	 * @param nows
	 * @return
	 */
	private static Node rebuildDFA(List<Set<Node>> nows) {
		List<Node> status = new ArrayList<>();
		Map<Node, Integer> statusIndex = new HashMap<>();
		for (int i = 0; i < nows.size(); i++) {
			status.add(new DFANode(nows.get(i)));
			for (Node s : nows.get(i)) {
				statusIndex.put(s, i);
			}
		}

		for (int i = 0; i < nows.size(); i++) {
			Node from = status.get(i);
			for (Node s : nows.get(i)) {
				for (char ch : s.accepts()) {
					from.addNext(ch, status.get(statusIndex.get(s.next(ch))));
				}
			}
		}
		return firstStatusOf(status);
	}

	/**
	 * 使用拓扑排序找到有限状态机头结点
	 * @param status
	 * @return
	 */
	public static Node firstStatusOf(List<Node> status) {
		int[] in = new int[status.size()];
		for (Node s : status) {
			for (char ch : s.accepts()) {
				in[status.indexOf(s.next(ch))]++;
			}
			for (Node next : s.epsilonNexts()) {
				in[status.indexOf(next)]++;
			}
		}
		for (int i = 0; i < in.length; i++) {
			if (in[i] == 0) {
				return status.get(i);
			}
		}
		throw new IllegalArgumentException("找不到 DFA 头结点");
	}

	/**
	 * 返回 s 的闭包（s 及与 s 通过 epsilon 边相连的所有状态）
	 * @param s
	 * @return
	 */
	public static Set<Node> closure(Node s) {
		Queue<Node> q = new ConcurrentLinkedQueue<>();
		q.add(s);
		Set<Node> visited = new HashSet<>();
		visited.add(s);
		while (!q.isEmpty()) {
			Node now = q.poll();
			for (Node next : now.epsilonNexts()) {
				if (visited.add(next)) {
					q.add(next);
				}
			}
		}
		return visited;
	}

	/**
	 * 返回 {@link Collection} 中所有状态的 {@link #closure(Node)} 的并集
	 * @param nodes
	 * @return
	 */
	public static Set<Node> closure(Collection<Node> nodes) {
		return Mapping.reduceSet(new HashSet<>(nodes), NodeUtil::closure);
	}

	/**
	 * 返回 NFA {@link Node} 通过 {@code ch} 能到达的所有状态的集合。
	 * 若 {@link Node} 通过该字符能到达多个状态，只返回 {@link Node#next(char)} 达到的那个
	 * @param nodes
	 * @param ch
	 * @return
	 */
	public static Set<Node> nextsOf(Collection<Node> nodes, char ch) {
		return closure(
			new HashSet<>(nodes).stream().map(s -> s.next(ch)).filter(n -> n != null).collect(Collectors.toSet())
		);
	}

}
