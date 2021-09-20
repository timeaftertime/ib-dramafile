package cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor;

import java.util.Set;

import cn.milai.beginning.collection.Filter;
import cn.milai.beginning.collection.Intersection;
import cn.milai.beginning.collection.Merge;

/**
 * 字符接收器
 * @author milai
 * @date 2020.03.19
 */
public interface CharAcceptor {

	/**
	 * 是否能接受指定字符
	 * @param c
	 * @return
	 */
	boolean accept(char c);

	/**
	 * 获取当前 {@link CharAcceptor} 可以接收的字符串集合
	 * @param alphabet
	 * @return
	 */
	default Set<Character> accepts(Set<Character> alphabet) {
		return Filter.set(alphabet, this::accept);
	}

	/**
	 * 返回一个新 {@link CharAcceptor}，其 {@link #accept(char)} 实现为两个 {@link CharAcceptor} 的逻辑与
	 * @param a1
	 * @param a2
	 * @return
	 */
	static CharAcceptor and(CharAcceptor a1, CharAcceptor a2) {
		return new CharAcceptor() {
			@Override
			public boolean accept(char c) {
				return a1.accept(c) && a2.accept(c);
			}

			@Override
			public Set<Character> accepts(Set<Character> alphabet) {
				return Intersection.set(a1.accepts(alphabet), a2.accepts(alphabet));
			}
		};
	}

	/**
	 * 返回一个新 {@link CharAcceptor}，其 {@link #accept(char)} 实现为两个 {@link CharAcceptor} 的逻辑或
	 * @param a1
	 * @param a2
	 * @return
	 */
	static CharAcceptor or(CharAcceptor a1, CharAcceptor a2) {
		return new CharAcceptor() {
			@Override
			public boolean accept(char c) {
				return a1.accept(c) || a2.accept(c);
			}

			@Override
			public Set<Character> accepts(Set<Character> alphabet) {
				return Merge.set(a1.accepts(alphabet), a2.accepts(alphabet));
			}
		};
	}

	/**
	 * 获取跟当前 {@link CharAcceptor} 接收相反字符集的 {@link CharAcceptor}
	 * @return
	 */
	default CharAcceptor negate() {
		return c -> !accept(c);
	}

}
