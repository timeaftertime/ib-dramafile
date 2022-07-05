package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.milai.common.collection.Mapping;

/**
 * DFA 构造器
 * @author milai
 * @date 2020.02.10
 */
public class DFABuilder {

	/**
	 * 使用子集构造法将 NFA 转换为 DFA 并返回头结点
	 * @param first
	 * @return
	 */
	public static Node newDFA(Node first) {
		Map<Set<Node>, Node> nfaToDFA = new HashMap<>();
		Queue<Set<Node>> q = new ConcurrentLinkedQueue<>();
		Set<Node> firstSet = NodeUtil.closure(first);
		nfaToDFA.put(firstSet, new DFANode());
		q.add(firstSet);
		while (!q.isEmpty()) {
			Set<Node> froms = q.poll();
			for (char ch : Mapping.reduceSet(froms, Node::accepts)) {
				Set<Node> tos = NodeUtil.nextsOf(froms, ch);
				if (tos.isEmpty()) {
					continue;
				}
				if (!nfaToDFA.containsKey(tos)) {
					q.add(tos);
					nfaToDFA.put(tos, new DFANode(tos));
				}
				nfaToDFA.get(froms).addNext(ch, nfaToDFA.get(tos));
			}
		}
		return NodeUtil.firstStatusOf(new ArrayList<>(nfaToDFA.values()));
	}

}
