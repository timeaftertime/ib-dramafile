package cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor;

/**
 * 字符接收器
 * @author milai
 * @date 2020.03.19
 */
public interface CharAcceptor {

	/**
	 * 是否能接受指定字符
	 * @param c
	 * @return
	 */
	boolean accept(char c);

	/**
	 * 返回一个新 {@link CharAcceptor}，其 {@link #accept(char)} 实现为两个 {@link CharAcceptor} 的逻辑与
	 * @param a1
	 * @param a2
	 * @return
	 */
	static CharAcceptor and(CharAcceptor a1, CharAcceptor a2) {
		return c -> a1.accept(c) && a2.accept(c);
	}

	/**
	 * 返回一个新 {@link CharAcceptor}，其 {@link #accept(char)} 实现为两个 {@link CharAcceptor} 的逻辑或
	 * @param a1
	 * @param a2
	 * @return
	 */
	static CharAcceptor or(CharAcceptor a1, CharAcceptor a2) {
		return c -> a1.accept(c) || a2.accept(c);
	}

}
