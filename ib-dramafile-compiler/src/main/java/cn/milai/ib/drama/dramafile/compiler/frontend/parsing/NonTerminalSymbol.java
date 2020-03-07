package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 语法分析的非终结符
 * @author milai
 * @date 2020.02.14
 */
public class NonTerminalSymbol extends Symbol {

	private List<Production> productions = Lists.newArrayList();

	public NonTerminalSymbol(String code) {
		super(code);
	}

	/**
	 *  给当前非终结符添加一个产生式
	 * @param rights
	 * @return 是否实际新增了产生式
	 */
	public boolean addProduction(List<Symbol> rights) {
		return productions.add(new Production(this, rights));
	}

	/**
	 * 给当前非终结符添加一个空产生式
	 * @return 是否实际进行了添加操作
	 */
	public boolean addEpsilonProduction() {
		return productions.add(new Production(this));
	}

	/**
	 * 移除一个产生式
	 * @param p
	 * @return 是否实际进行了移除操作
	 */
	public boolean removeProduction(Production p) {
		return productions.remove(p);
	}

	public List<Production> getProductions() {
		return Lists.newArrayList(productions);
	}

	@Override
	public boolean isNonTerminal() {
		return true;
	}

}
