package cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor;

import java.util.regex.Pattern;

/**
 * 接受指定 [ ] 正则表达式所匹配字符的字符接收器
 * @author milai
 * @date 2020.03.20
 */
public class RegexAcceptor implements CharAcceptor {

	private String re;
	private Pattern pattern;

	/**
	 * 构造匹配指定 [ ] 正则表达式的接收器
	 * @param re 正则表达式 [ ] 中间的字符串
	 */
	public RegexAcceptor(String re) {
		this.re = "^[" + re + "]$";
		pattern = Pattern.compile(this.re);
	}

	@Override
	public boolean accept(char ch) {
		return pattern.matcher("" + ch).matches();
	}

	@Override
	public String toString() {
		return String.format("Regex(%s)", re);
	}

}
