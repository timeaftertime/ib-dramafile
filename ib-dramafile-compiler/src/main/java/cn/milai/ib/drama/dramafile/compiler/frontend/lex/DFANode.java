package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 词法分析过程中 DFA 的状态
 * @author milai
 * @date 2020.02.09
 */
public class DFANode implements Node {

	/**
	 * 边 -> 通往的状态
	 */
	private Map<Character, Node> edges = new HashMap<>();

	private Set<String> tokens = new HashSet<>();

	public DFANode() {
	}

	/**
	 * 合并指定 {@link Node} 集合的 token 构造一个 {@link DFANode}
	 * @param status
	 */
	public DFANode(Set<? extends Node> status) {
		for (Node s : status) {
			if (s.isAccept()) {
				tokens.addAll(s.tokens());
			}
		}
	}

	@Override
	public void addNext(char ch, Node s) {
		edges.put(ch, s);
	}

	@Override
	public Set<Character> accepts() {
		return edges.keySet();
	}

	@Override
	public Node next(char ch) {
		return edges.get(ch);
	}

	@Override
	public boolean isAccept() { return tokens.size() > 0; }

	@Override
	public Set<String> tokens() {
		if (!isAccept()) {
			throw new UnsupportedOperationException("当前状态不是接受状态");
		}
		return tokens;
	}

	@Override
	public void addToken(String token) {
		this.tokens.add(token);
	}

	@Override
	public String toString() {
		return String.format("DFA{ edges=%s }", edges.keySet());
	}

}
