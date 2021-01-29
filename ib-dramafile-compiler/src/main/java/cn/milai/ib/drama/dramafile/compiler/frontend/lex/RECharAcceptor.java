package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * 接受指定正则表达式所匹配字符的字符接收器
 * @author milai
 * @date 2020.03.20
 */
public class RECharAcceptor implements CharAcceptor {

	private Predicate<Character> acceptor;

	/**
	 * 
	 * @param re 正则表达式 [ ] 中间的字符串
	 */
	public RECharAcceptor(String re) {
		Predicate<String> tmp = Pattern.compile("^[" + re + "]$").asPredicate();
		acceptor = ch -> tmp.test("" + ch);
	}

	@Override
	public boolean accept(char ch) {
		return acceptor.test(ch);
	}

}
