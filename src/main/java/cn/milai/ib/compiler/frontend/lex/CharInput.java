package cn.milai.ib.compiler.frontend.lex;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import cn.milai.ib.compiler.frontend.Input;

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
		List<Character> list = Lists.newArrayList();
		for (char ch : str.toCharArray()) {
			list.add(ch);
		}
		return list.toArray(new Character[0]);
	}

	@Override
	public CharInput filter(Predicate<Character> p) {
		return new CharInput((elements.stream().filter(p).collect(Collectors.toList())));
	}
}
