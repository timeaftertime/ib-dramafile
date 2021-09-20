package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

/**
 * 语法分析产生式符号（终结符、非终结符）
 * @author milai
 * @date 2020.02.14
 */
public abstract class Symbol implements Comparable<Symbol> {

	private String code;

	public Symbol(String code) {
		this.code = code;
	}

	/**
	 * 语法定义中唯一表示该符号的字符串
	 * @return
	 */
	public String getCode() { return code; }

	@Override
	public String toString() {
		return getCode();
	}

	@Override
	public int compareTo(Symbol o) {
		return code.compareTo(o.code);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		// 必须是同一种符号
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		return getCode().equals(((Symbol) obj).getCode());
	}

	@Override
	public int hashCode() {
		return code.hashCode();
	}

	/**
	 * 是否为非终结符
	 * @return
	 */
	public abstract boolean isNonTerminal();

	public static final Symbol EOF = new Symbol(Keywords.EOF) {
		@Override
		public boolean isNonTerminal() { return false; }
	};

	public static final Symbol EPSILON = new Symbol(Keywords.EPSILON) {
		@Override
		public boolean isNonTerminal() { return false; }
	};

	/**
	 * 判断 s 是否为输入结束符号
	 * @param s
	 * @return
	 */
	public static boolean isEOF(Symbol s) {
		return s == EOF;
	}

	/**
	 * 判断 s 是否为空产生式 ϵ 符号
	 * @param s
	 * @return
	 */
	public static boolean isEpsilon(Symbol s) {
		return s == EPSILON;
	}

	/**
	 * 判断指定 {@code code} 是否为 ϵ 符号的 {@link #code}
	 * @param code
	 * @return
	 */
	public static boolean isEpsilon(String code) {
		return EPSILON.getCode().equals(code);
	}

}
