package cn.milai.ib.compiler.frontend.lex;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * 词法分析字符集相关类
 * @author milai
 * @date 2020.02.04
 */
public class Char {

	/**
	 * 表示输入结束
	 */
	public static final char EOF = 0;

	private static final Set<Character> NUMBERS = toSet("0123456789");
	private static final Set<Character> UPPER_LETTERS = toSet("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	private static final Set<Character> LOWER_LETTERS = toSet("abcdefghijklmnopqrstuvwxyz");

	public static final char UNDERLINE = '_';
	public static final char INVERT_CHAR = '^';
	public static final char RANGE_CHAR = '-';
	public static final char ONE_OR_MORE_CHAR = '+';
	public static final char NONE_OR_MORE_CHAR = '*';
	public static final char TIMES_LEFT_CHAR = '{';
	public static final char TIMES_RIGHT_CHAR = '}';
	public static final char SET_LEFT_CHAR = '[';
	public static final char SET_RIGHT_CHAR = ']';
	public static final char COMP_LEFT_CHAR = '(';
	public static final char COMP_RIGHT_CHAR = ')';
	public static final char SLASH = '\\';
	public static final char OR = '|';

	/**
	 * 匹配换行符 \n \r 以外任何单个字符
	 */
	public static final char INVERT_LF = '.';

	public static final char BLANK = ' ';
	public static final char TAB = '\t';
	public static final char LINEFEED = '\n';
	public static final char CARRIAGE_RETURN = '\r';

	private static Set<Character> toSet(String str) {
		Set<Character> set = Sets.newHashSet();
		for (char ch : str.toCharArray()) {
			set.add(ch);
		}
		return set;
	}

	/**
	 * 所有字符的集合
	 * @return
	 */
	public static Set<Character> all() {
		Set<Character> set = Sets.newHashSet();
		set.addAll(visible());
		set.add(INVERT_CHAR);
		set.add(RANGE_CHAR);
		set.add(ONE_OR_MORE_CHAR);
		set.add(NONE_OR_MORE_CHAR);
		set.add(BLANK);
		set.add(TAB);
		set.add(LINEFEED);
		set.add(CARRIAGE_RETURN);
		return set;
	}

	/**
	 * 0~9 的字符集合
	 * @return
	 */
	public static Set<Character> numbers() {
		return Sets.newHashSet(NUMBERS);
	}

	/**
	 * 所有小写字母集合
	 * @return
	 */
	public static Set<Character> lowers() {
		return Sets.newHashSet(LOWER_LETTERS);
	}

	/**
	 * 所有大写字母集合
	 * @return
	 */
	public static Set<Character> uppers() {
		return Sets.newHashSet(UPPER_LETTERS);
	}

	public static Set<Character> range(char start, char end) {
		if (start > end) {
			throw new IllegalArgumentException(String.format("非法范围表达式 %c-%c", start, end));
		}
		HashSet<Character> set = Sets.newHashSet();
		for (char i = start; i <= end; i++) {
			set.add(i);
		}
		return set;
	}

	/**
	 * 可见字符（数字、字母、下划线）集合
	 * @return
	 */
	public static Set<Character> visible() {
		Set<Character> set = Sets.newHashSet();
		set.addAll(NUMBERS);
		set.addAll(UPPER_LETTERS);
		set.addAll(LOWER_LETTERS);
		set.add(UNDERLINE);
		return set;
	}

	/**
	 * 不可见的空白字符（'\t'、' '、'\n' 等）集合
	 * @return
	 */
	public static Set<Character> unvisible() {
		Set<Character> set = Sets.newHashSet();
		set.add(BLANK);
		set.add(TAB);
		set.add(LINEFEED);
		set.add(CARRIAGE_RETURN);
		return set;
	}

	/**
	 * 获取 set 的补集
	 * @param set
	 * @return
	 */
	public static Set<Character> invert(Set<Character> set) {
		return Sets.difference(all(), set);
	}

	/**
	 * 是否为普通字符
	 * @param ch
	 * @return
	 */
	public static boolean isNormal(char ch) {
		return visible().contains(ch);
	}

	/**
	 * 将转义字符 \ 后的字符转换为对应的特殊字符
	 * @param ch
	 * @return
	 */
	public static Set<Character> slash(char ch) {
		switch (ch) {
			case 't':
				return Sets.newHashSet(TAB);
			case 'n':
				return Sets.newHashSet(LINEFEED);
			case 'r':
				return Sets.newHashSet(CARRIAGE_RETURN);
			case 'w':
				return Char.visible();
			case 'W':
				return Char.invert(Char.visible());
			case 'd':
				return Char.numbers();
			case 'D':
				return Char.invert(Char.numbers());
		}
		throw new IllegalArgumentException("未知转移字符：" + SLASH + ch);
	}

}
