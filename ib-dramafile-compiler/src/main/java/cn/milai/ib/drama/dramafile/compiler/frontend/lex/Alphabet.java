package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.Iterator;

/**
 * 字母表
 * @author milai
 * @date 2021.09.06
 */
public class Alphabet {

	/**
	 * 获取所有字符的可迭代对象
	 * @return
	 */
	public static Iterable<Character> all() {
		return () -> new Iterator<Character>() {
			private char ch = 1;

			@Override
			public boolean hasNext() {
				return ch > 0;
			}

			@Override
			public Character next() {
				return ch++;
			}
		};
	}
}
