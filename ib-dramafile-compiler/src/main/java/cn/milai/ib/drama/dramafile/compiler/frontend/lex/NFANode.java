package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.util.Assert;

import cn.milai.common.collection.Creator;
import cn.milai.common.collection.Mapping;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor.CharAcceptor;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor.IncludeAcceptor;

/**
 * {@link NFAPair} 节点
 * @author milai
 * @date 2020.02.04
 */
public class NFANode implements Node {

	/**
	 * 出边
	 */
	private Map<CharAcceptor, Node> edges = new HashMap<>();

	private Set<Node> epsilonNexts = new HashSet<>();

	private String token = null;

	private Set<Character> acceptSet;

	public void addNext(CharAcceptor acceptor, Node status) {
		edges.put(acceptor, status);
		acceptSet = null;
	}

	@Override
	public void addNext(char ch, Node s) {
		addNext(new IncludeAcceptor(ch), s);
	}

	@Override
	public void addEpsilonNext(Node node) {
		epsilonNexts.add(node);
	}

	@Override
	public Set<Node> epsilonNexts() {
		return Collections.unmodifiableSet(epsilonNexts);
	}

	@Override
	public Node next(char ch) {
		for (CharAcceptor acceptor : edges.keySet()) {
			if (acceptor.accept(ch)) {
				return edges.get(acceptor);
			}
		}
		return null;
	}

	@Override
	public boolean isAccept() { return token != null; }

	@Override
	public String token() {
		Assert.isTrue(isAccept(), "当前状态不是接收状态");
		return token;
	}

	@Override
	public Set<String> tokens() throws UnsupportedOperationException {
		return Creator.hashSet(token());
	}

	@Override
	public Set<Character> accepts() {
		if (acceptSet == null) {
			acceptSet = Mapping.reduceSet(edges.keySet(), a -> a.accepts(Alphabet.set()));
		}
		return acceptSet;
	}

	@Override
	public void addToken(String token) {
		if (this.token != null) {
			throw new IllegalStateException("NFA 结点不能设置多个 token code");
		}
		this.token = token;
	}

	@Override
	public String toString() {
		return String.format("NFA{ edges=%s, ε=%s }", edges.keySet(), epsilonNexts);
	}

}
