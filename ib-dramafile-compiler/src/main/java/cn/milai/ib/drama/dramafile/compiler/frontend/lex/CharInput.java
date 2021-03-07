package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import cn.milai.common.base.Collects;
import cn.milai.ib.drama.dramafile.compiler.frontend.Input;

/**
 * 处理 char 类型的 Input
 * @author milai
 * @date 2020.02.04
 */
public class CharInput extends Input<Character> {

	public CharInput(String str) {
		super(toChracterArray(str));
	}

	public CharInput(List<Character> chs) {
		super(chs.toArray(new Character[0]));
	}

	private static Character[] toChracterArray(String str) {
		List<Character> list = new ArrayList<>();
		for (char ch : str.toCharArray()) {
			list.add(ch);
		}
		return list.toArray(new Character[0]);
	}

	@Override
	public CharInput filter(Predicate<Character> p) {
		return new CharInput(Collects.filterList(elements, p));
	}
}
