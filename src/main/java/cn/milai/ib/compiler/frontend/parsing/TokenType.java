package cn.milai.ib.compiler.frontend.parsing;

/**
 * 词法分析 Token 类型枚举
 * @author milai
 * @date 2020.02.10
 */
public enum TokenType {

	// 空白间隔
	BLANK("\\s+", 0),
	// 标点符号
	BLOCK_LEFT("{", 1), BLOCK_RIGHT("}", 1), STM_END(";", 1), ASSIGN("=", 1), PROP("\\.", 1),
	BRACKET_LEFT("(", 1), BRACKET(")", 1),
	// 关键字
	IF("if", 2), WHILE("while", 2), NEW("new", 2),
	// 标识符
	IDENTIFIER("[_a-zA-Z]\\w*", 3),
	// 常量
	INT("[1-9][0-9]*", 4), FLOAT("[0-9]+\\.[0-9]+", 4), STR("\"[^\"]*\"", 4),
	;

	/**
	 * 正则表达式
	 */
	private String re;

	/**
	 * 匹配顺序，越靠前，越优先匹配
	 */
	private int order;

	TokenType(String re, int order) {
		this.re = re;
		this.order = order;
	}

	public String getRE() {
		return re;
	}

	public int getOrder() {
		return order;
	}

	public String getCode() {
		return name();
	}

}
