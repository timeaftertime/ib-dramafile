package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

/**
 * 语法解析的 Token
 * @author milai
 * @date 2020.02.12
 */
public class Token {

	/**
	 * 匹配到的原始字符串
	 */
	private String origin;

	private TokenType type;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	public Token(String orgin, TokenType type) {
		this.origin = orgin;
		this.type = type;
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
		Token token = (Token) obj;
		return type == token.type && origin.equals(token.origin);
	}

	@Override
	public String toString() {
		return "Token [origin=" + origin + ", type=" + type + "]";
	}

}
