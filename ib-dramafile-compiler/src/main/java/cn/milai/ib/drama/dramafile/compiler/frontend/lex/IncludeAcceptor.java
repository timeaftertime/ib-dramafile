package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.HashSet;
import java.util.Set;

/**
 * 接受在字符 Set 的字符接收器
 * @author milai
 * @date 2020.03.19
 */
public class IncludeAcceptor implements CharAcceptor {

	private Set<Character> chs;

	public IncludeAcceptor(char... chs) {
		this(toSet(chs));
	}

	public IncludeAcceptor(Set<Character> chs) {
		this.chs = chs;
	}

	@Override
	public boolean accept(char ch) {
		return chs.contains(ch);
	}

	private static Set<Character> toSet(char... chs) {
		Set<Character> set = new HashSet<>();
		for (char ch : chs) {
			set.add(ch);
		}
		return set;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != IncludeAcceptor.class) {
			return false;
		}
		IncludeAcceptor acceptor = (IncludeAcceptor) obj;
		return acceptor.chs.equals(chs);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (char ch : chs) {
			sb.append(ch);
		}
		return String.format("include(%s)", sb.toString());
	}
}
