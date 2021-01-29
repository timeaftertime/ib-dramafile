package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * 接受在字符 Set 的字符接收器
 * @author milai
 * @date 2020.03.19
 */
public class SetCharAcceptor implements CharAcceptor {

	private Set<Character> chs;

	public SetCharAcceptor(char... chs) {
		this(toSet(chs));
	}

	public SetCharAcceptor(Set<Character> chs) {
		this.chs = chs;
	}

	@Override
	public boolean accept(char ch) {
		return chs.contains(ch);
	}

	private static Set<Character> toSet(char... chs) {
		Set<Character> set = Sets.newHashSet();
		for (char ch : chs) {
			set.add(ch);
		}
		return set;
	}
}
