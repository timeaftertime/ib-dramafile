package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cn.milai.common.base.Chars;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor.CharAcceptor;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor.ExcludeAcceptor;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor.IncludeAcceptor;

/**
 * 词法分析字符集相关类
 * @author milai
 * @date 2020.02.04
 */
public class CharSets {

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
	private static final Set<Character> SLASH_ORIGINAL = toSet(".{}?+*|-[]()\\");

	public static final char UNDERLINE = '_';
	public static final char INVERT = '^';
	public static final char RANGE = '-';
	
	public static final char NONE_OR_ONE = '?';
	public static final char ONE_OR_MORE = '+';
	public static final char NONE_OR_MORE = '*';
	
	public static final char OPEN_BRACE = '{';
	public static final char CLOSE_BRACE = '}';
	public static final char OPEN_BRACKET = '[';
	public static final char CLOSE_BRAKET = ']';
	public static final char OPEN_PARENTHESIS = '(';
	public static final char CLOSE_PARENTHESIS = ')';
	
	public static final char SLASH = '\\';
	public static final char OR = '|';

	/**
	 * 匹配换行符 \n \r 以外任何单个字符
	 */
	public static final char NOT_CRLF = '.';

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
	 * 将转义字符 \ 后的字符转换为对应的特殊字符
	 * @param ch
	 * @return
	 */
	public static CharAcceptor slash(char ch) {
		if (SLASH_ORIGINAL.contains(ch)) {
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
				return new IncludeAcceptor(CharSets.unvisible());
			case 'S' :
				return new ExcludeAcceptor(CharSets.unvisible());
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
