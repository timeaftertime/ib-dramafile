package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.Collections;
import java.util.Set;

/**
 * 状态结点
 * @author milai
 * @date 2021.09.10
 */
public interface Node {

	/**
	 * 当前结点是否为接收结点
	 * @return
	 */
	boolean isAccept();

	/**
	 * {@link #isAccept()} 为 {@code true} 时获取 token 集合
	 * @return
	 * @throws UnsupportedOperationException 如果当前不是接收状态
	 */
	Set<String> tokens() throws UnsupportedOperationException;

	/**
	 * {@link #isAccept()} 为 {@code true} 时获取第一个 token
	 * @return
	 * @throws UnsupportedOperationException
	 */
	default String token() throws UnsupportedOperationException {
		return (String) tokens().toArray()[0];
	}

	/**
	 * 设置当前结点为接受状态并添加一个指定 token code
	 * @param token
	 */
	void addToken(String token);

	/**
	 * 获取当前节点通过字符 {@code ch} 连接的状态，若不存在，返回 null
	 * @param ch
	 * @return
	 */
	Node next(char ch);

	/**
	 * 获取可接受的字符集合
	 * @return
	 */
	Set<Character> accepts();

	/**
	 * 设置当前结点通过 ch 连接的结点为指定 {@link Node}
	 * @param ch
	 * @param s
	 */
	void addNext(char ch, Node s);

	/**
	 * 获取通过空字符连接的结点集合
	 * @return
	 */
	default Set<Node> epsilonNexts() {
		return Collections.emptySet();
	}

	/**
	 * 添加一个空字符串连接的结点
	 * @param s
	 */
	default void addEpsilonNext(Node s) {
		throw new UnsupportedOperationException();
	}

}
