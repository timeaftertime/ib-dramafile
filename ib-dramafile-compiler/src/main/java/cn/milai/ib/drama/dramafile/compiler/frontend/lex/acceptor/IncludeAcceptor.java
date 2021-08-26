package cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor;

import java.util.Collection;
import java.util.Set;

/**
 * 接受在字符 {@link Set} 的 {@link CharAcceptor}
 * @author milai
 * @date 2020.03.19
 */
public class IncludeAcceptor extends CharSetAcceptor {

	public IncludeAcceptor(char... included) {
		super(included);
	}

	public IncludeAcceptor(Collection<Character> included) {
		super(included);
	}

	@Override
	protected boolean accept(Set<Character> chs, char c) {
		return chs.contains(c);
	}

	@Override
	public String toString() {
		return String.format("include(%s)", charsString());
	}
}
