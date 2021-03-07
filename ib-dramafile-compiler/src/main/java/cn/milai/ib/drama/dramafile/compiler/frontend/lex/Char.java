package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cn.milai.common.base.Chars;

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

	private static final Set<Character> NUMBERS = toSet(Chars.DIGITS);
	private static final Set<Character> UPPER_LETTERS = toSet(Chars.UPPERS);
	private static final Set<Character> LOWER_LETTERS = toSet(Chars.LOWERS);

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

	private static Set<Character> toSet(String str) {
		Set<Character> set = new HashSet<>();
		for (char ch : str.toCharArray()) {
			set.add(ch);
		}
		return set;
	}

	/**
	 * 0~9 的字符集合
	 * @return
	 */
	public static Set<Character> numbers() {
		return new HashSet<>(NUMBERS);
	}

	/**
	 * 所有小写字母集合
	 * @return
	 */
	public static Set<Character> lowers() {
		return new HashSet<>(LOWER_LETTERS);
	}

	/**
	 * 所有大写字母集合
	 * @return
	 */
	public static Set<Character> uppers() {
		return new HashSet<>(UPPER_LETTERS);
	}

	public static Set<Character> range(char start, char end) {
		if (start > end) {
			throw new IllegalArgumentException(String.format("非法范围表达式 %c-%c", start, end));
		}
		HashSet<Character> set = new HashSet<>();
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
		Set<Character> set = new HashSet<>();
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
		Set<Character> set = new HashSet<>();
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
		Set<Character> set = new HashSet<>();
		set.add(BLANK);
		set.add(TAB);
		set.add(Chars.C_LF);
		set.add(Chars.C_CR);
		return set;
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
	public static CharAcceptor slash(char ch) {
		if (CAN_SLASH.contains(ch)) {
			return new IncludeAcceptor(new HashSet<>(Arrays.asList(ch)));
		}
		switch (ch) {
			case 't' :
				return new IncludeAcceptor(new HashSet<>(Arrays.asList(TAB)));
			case 'n' :
				return new IncludeAcceptor(new HashSet<>(Arrays.asList(Chars.C_LF)));
			case 'r' :
				return new IncludeAcceptor(new HashSet<>(Arrays.asList(Chars.C_CR)));
			case 's' :
				return new IncludeAcceptor(Char.unvisible());
			case 'S' :
				return new ExcludeAcceptor(Char.unvisible());
			case 'w' :
				return new IncludeAcceptor(normals());
			case 'W' :
				return new ExcludeAcceptor(normals());
			case 'd' :
				return new IncludeAcceptor(numbers());
			case 'D' :
				return new ExcludeAcceptor(numbers());
		}
		throw new IllegalArgumentException("未知转义字符：" + SLASH + ch);
	}

}
