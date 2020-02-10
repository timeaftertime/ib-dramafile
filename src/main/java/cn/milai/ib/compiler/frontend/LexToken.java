package cn.milai.ib.compiler.frontend;

/**
 * 词法分析的 Token
 * @author milai
 * @date 2020.02.10
 */
public class LexToken {

	/**
	 * 正则表达式定义
	 */
	private String re;
	
	/**
	 * 对应 TokenType 枚举的 code
	 */
	private String tokenCode;

	public LexToken(String re, String tokenCode) {
		super();
		this.re = re;
		this.tokenCode = tokenCode;
	}

	public String getRE() {
		return re;
	}

	public String getCode() {
		return tokenCode;
	}

}
