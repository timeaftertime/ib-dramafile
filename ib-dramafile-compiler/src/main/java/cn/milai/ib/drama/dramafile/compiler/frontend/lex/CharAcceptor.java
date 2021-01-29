package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

/**
 * 字符接收器
 * @author milai
 * @date 2020.03.19
 */
public interface CharAcceptor {

	/**
	 * 接受所有字符的 CharAcceptor
	 */
	CharAcceptor ANY = acceptor -> true;

	/**
	 * 是否能接受指定字符
	 * @param ch
	 * @return
	 */
	boolean accept(char ch);
}
