package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

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

	/**
	 * 可以用过转义字符 \ 来表示原字符的特殊字符
	 */
	private static final Set<Character> CAN_SLASH = toSet(".{}?+*|-[]()\\");

	public static final char UNDERLINE = '_';
	public static final char INVERT = '^';
	public static final char RANGE = '-';
	public static final char NONE_OR_ONE = '?';
	public static final char ONE_OR_MORE = '+';
	public static final char NONE_OR_MORE = '*';
	public static final char BLOCK_LEFT_CHAR = '{';
	public static final char BLOCK_RIGHT_CHAR = '}';
	public static final char SET_LEFT_CHAR = '[';
	public static final char SET_RIGHT_CHAR = ']';
	public static final char COMP_LEFT_CHAR = '(';
	public static final char COMP_RIGHT_CHAR = ')';
	public static final char SLASH = '\\';
	public static final char OR = '|';

	/**
	 * 匹配换行符 \n \r 以外任何单个字符
	 */
	public static final char INVERT_CRLF = '.';

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
		set.addAll(unvisible());
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
	 * 数字、字母、下划线集合
	 * @return
	 */
	public static Set<Character> normals() {
		Set<Character> set = Sets.newHashSet();
		set.addAll(NUMBERS);
		set.addAll(UPPER_LETTERS);
		set.addAll(LOWER_LETTERS);
		set.add(UNDERLINE);
		return set;
	}

	/**
	 * 可见（即空格、换行、回车等空白字符以外的）字符集合
	 * @return
	 */
	public static Set<Character> visible() {
		Set<Character> set = Sets.newHashSet();
		set.addAll(normals());
		set.add(INVERT_CRLF);
		set.add(INVERT);
		set.add(RANGE);
		set.add(NONE_OR_ONE);
		set.add(ONE_OR_MORE);
		set.add(NONE_OR_MORE);
		set.add(BLOCK_LEFT_CHAR);
		set.add(BLOCK_RIGHT_CHAR);
		set.add(SET_LEFT_CHAR);
		set.add(SET_RIGHT_CHAR);
		set.add(COMP_LEFT_CHAR);
		set.add(COMP_RIGHT_CHAR);
		set.add(SLASH);
		set.add(OR);
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
	 * 全集去掉 \n \r 的字符集
	 * @return
	 */
	public static Set<Character> invertCRLF() {
		return invert(Sets.newHashSet(CARRIAGE_RETURN, LINEFEED));
	}

	/**
	 * 是否为普通字符
	 * @param ch
	 * @return
	 */
	public static boolean isNormal(char ch) {
		return normals().contains(ch);
	}

	/**
	 * 将转义字符 \ 后的字符转换为对应的特殊字符
	 * @param ch
	 * @return
	 */
	public static Set<Character> slash(char ch) {
		if (CAN_SLASH.contains(ch)) {
			return Sets.newHashSet(ch);
		}
		switch (ch) {
			case 't':
				return Sets.newHashSet(TAB);
			case 'n':
				return Sets.newHashSet(LINEFEED);
			case 'r':
				return Sets.newHashSet(CARRIAGE_RETURN);
			case 's':
				return Char.unvisible();
			case 'S':
				return Char.invert(Char.unvisible());
			case 'w':
				return Char.normals();
			case 'W':
				return Char.invert(Char.normals());
			case 'd':
				return Char.numbers();
			case 'D':
				return Char.invert(Char.numbers());
		}
		throw new IllegalArgumentException("未知转义字符：" + SLASH + ch);
	}

}
