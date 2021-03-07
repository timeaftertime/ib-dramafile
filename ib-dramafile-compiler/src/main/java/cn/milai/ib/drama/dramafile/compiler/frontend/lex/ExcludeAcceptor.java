package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.HashSet;
import java.util.Set;

/**
 * 接受不在字符 Set 的字符接收器
 * @author milai
 * @date 2020.03.19
 */
public class ExcludeAcceptor implements CharAcceptor {

	/**
	 * 不接受的字符集合
	 */
	private Set<Character> chs;

	public ExcludeAcceptor(char... chs) {
		this(toSet(chs));
	}

	public ExcludeAcceptor(Set<Character> chs) {
		this.chs = chs;
	}

	@Override
	public boolean accept(char ch) {
		return !chs.contains(ch);
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
		if (obj.getClass() != ExcludeAcceptor.class) {
			return false;
		}
		ExcludeAcceptor acceptor = (ExcludeAcceptor) obj;
		return acceptor.chs.equals(chs);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (char ch : chs) {
			sb.append(ch);
		}
		return String.format("exclude(%s)", sb.toString());
	}

}
