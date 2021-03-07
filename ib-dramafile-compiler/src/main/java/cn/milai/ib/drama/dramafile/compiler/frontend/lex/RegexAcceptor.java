package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * 接受指定正则表达式所匹配字符的字符接收器
 * @author milai
 * @date 2020.03.20
 */
public class RegexAcceptor implements CharAcceptor {

	private Predicate<Character> acceptor;
	private String re;

	/**
	 * 
	 * @param re 正则表达式 [ ] 中间的字符串
	 */
	public RegexAcceptor(String re) {
		this.re = "^[" + re + "]$";
		Predicate<String> tmp = Pattern.compile(this.re).asPredicate();
		acceptor = ch -> tmp.test("" + ch);
	}

	@Override
	public boolean accept(char ch) {
		return acceptor.test(ch);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != RegexAcceptor.class) {
			return false;
		}
		RegexAcceptor acceptor = (RegexAcceptor) obj;
		return acceptor.re.equals(re);
	}

	@Override
	public String toString() {
		return re;
	}

}
