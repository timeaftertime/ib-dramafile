package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

/**
 * 词法分析的单词定义
 * @author milai
 * @date 2020.02.10
 */
public class TokenDefinition {

	/**
	 * 正则表达式定义
	 */
	private String regex;

	/**
	 * 对应 TokenType 枚举的 code
	 */
	private String code;

	public TokenDefinition(String regex, String code) {
		this.regex = regex;
		this.code = code;
	}

	public String getRegex() { return regex; }

	public String getCode() { return code; }

	@Override
	public int hashCode() {
		return code.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		return code.equals(((TokenDefinition) obj).code);
	}

}
