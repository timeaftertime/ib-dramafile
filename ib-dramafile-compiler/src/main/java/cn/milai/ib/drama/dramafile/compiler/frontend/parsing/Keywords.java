package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

/**
 * 关键词
 * @author milai
 * @date 2021.09.20
 */
public class Keywords {

	private Keywords() {
	}

	/**
	 * 语法开始符号
	 */
	public static final String CFG = "CFG";

	/**
	 * 输入结束符号
	 */
	public static final String EOF = "$";

	/**
	 * 空字符
	 */
	public static final String EPSILON = "ϵ";

	/**
	 * 推导符
	 */
	public static final String PRODUCTION = "->";

	/**
	 * 重命名符
	 */
	public static final String ALIAS = "=";

}
