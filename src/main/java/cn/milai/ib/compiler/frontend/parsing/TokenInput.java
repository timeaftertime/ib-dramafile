package cn.milai.ib.compiler.frontend.parsing;

import cn.milai.ib.compiler.frontend.Input;

public class TokenInput extends Input<Token> {

	public TokenInput(Token[] array) {
		super(array);
	}

	/**
	 * 将剩余的元素转换为数组返回，不改变读指针位置
	 * @return
	 */
	public Token[] toArray() {
		return super.toArray(new Token[0]);
	}

}
