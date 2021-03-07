package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

import java.util.function.Predicate;

import cn.milai.common.base.Collects;
import cn.milai.ib.drama.dramafile.compiler.frontend.Input;

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

	@Override
	public TokenInput filter(Predicate<Token> p) {
		return new TokenInput(Collects.filterList(elements, p).toArray(new Token[0]));
	}
}
