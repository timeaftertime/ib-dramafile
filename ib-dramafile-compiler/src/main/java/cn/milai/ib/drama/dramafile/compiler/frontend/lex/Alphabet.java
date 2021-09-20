package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.Iterator;
import java.util.Set;

import cn.milai.beginning.collection.Creator;

/**
 * 字母表
 * @author milai
 * @date 2021.09.06
 */
public class Alphabet {

	/**
	 * 获取从 {@code from} 到 {@code to} 的 {@link Character} 迭代器(左闭右开)
	 * @param from
	 * @param to
	 * @return
	 */
	public static Iterable<Character> range(int from, int to) {
		return () -> new Iterator<Character>() {
			private char ch = (char) from;

			@Override
			public boolean hasNext() {
				return ch != to;
			}

			@Override
			public Character next() {
				return ch++;
			}
		};
	}

	/**
	 * 获取从 {@code from} 到 {@code to} {@link Character} 的 {@link Set} (左闭右开)
	 * @param from
	 * @param to
	 * @return
	 */
	public static Set<Character> rangeSet(int from, int to) {
		return Creator.hashSet(range(from, to));
	}

	/**
	 * 获取所有字符的可迭代对象
	 * @return
	 */
	public static Iterable<Character> all() {
		return range(1, Character.MIN_VALUE);
	}

	/**
	 * 获取所有字符的集合
	 * @return
	 */
	public static Set<Character> set() {
		return Creator.hashSet(all());
	}
}
