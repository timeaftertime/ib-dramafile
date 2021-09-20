package cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import cn.milai.ib.drama.dramafile.compiler.frontend.StringUtil;

/**
 * 根据 {@link Character} 的 {@link Set} 判断是否接受的 {@link CharAcceptor}
 * @author milai
 * @date 2021.08.21
 */
public abstract class CharSetAcceptor implements CharAcceptor {

	private Set<Character> chs;

	public CharSetAcceptor(char... chs) {
		this(Arrays.asList(StringUtil.toCharacterArray(new String(chs))));
	}

	public CharSetAcceptor(Collection<Character> chs) {
		this.chs = new HashSet<>(chs);
	}

	/**
	 * 获取持有的字符 {@link Set}
	 * @return
	 */
	protected Set<Character> getChs() { return chs; }

	@Override
	public boolean accept(char c) {
		return accept(chs, c);
	}

	/**
	 * 将持有的 {@link Set} 转换为字符串
	 * @return
	 */
	protected String charsString() {
		StringBuilder sb = new StringBuilder();
		for (char ch : chs) {
			sb.append(ch);
		}
		return sb.toString();
	}

	/**
	 * 是否接受指定字符
	 * @param chs 持有的 {@link Set}
	 * @param c
	 * @return
	 */
	protected abstract boolean accept(Set<Character> chs, char c);
}
