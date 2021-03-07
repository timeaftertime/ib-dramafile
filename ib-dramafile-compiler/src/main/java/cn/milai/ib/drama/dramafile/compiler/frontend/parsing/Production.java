package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.CollectionUtils;

/**
 * 语法分析产生式
 * @author milai
 * @date 2020.02.14
 */
public class Production {

	private NonTerminalSymbol left;

	private List<Symbol> rights;

	public Production(NonTerminalSymbol left, List<Symbol> rights) {
		if (CollectionUtils.isEmpty(rights)) {
			rights = new ArrayList<>(Arrays.asList(Symbol.EPSILON));
		}
		this.left = left;
		this.rights = rights;
	}

	public Production(NonTerminalSymbol left) {
		this(left, null);
	}

	/**
	 * 获取产生式左非终结符
	 * @return
	 */
	public NonTerminalSymbol getLeft() { return left; }

	/**
	 * 获取产生式右式符号列表，若为空产生式，返回一个只包含 Symbol.EPSILON 的列表
	 * @return
	 */
	public List<Symbol> getRights() { return new ArrayList<>(rights); }

	@Override
	public String toString() {
		return left + " -> " + toString(rights);
	}

	private static String toString(List<Symbol> rights) {
		StringBuilder sb = new StringBuilder();
		for (Symbol s : rights) {
			sb.append(s + " ");
		}
		return sb.toString();
	}

	/**
	 * 是否为空产生式
	 * @return
	 */
	public boolean isEpsilon() { return rights.size() == 1 && Symbol.isEpsilon(rights.get(0)); }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		Production p = (Production) obj;
		return p.left.equals(left) && p.rights.equals(rights);
	}

}
