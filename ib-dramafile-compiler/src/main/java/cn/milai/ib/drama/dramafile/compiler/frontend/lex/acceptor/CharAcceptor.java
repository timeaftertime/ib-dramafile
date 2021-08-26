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

}
