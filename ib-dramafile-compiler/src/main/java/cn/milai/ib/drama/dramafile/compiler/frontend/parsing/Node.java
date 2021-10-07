package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

/**
 * 语法分析树的节点
 * @author milai
 * @date 2020.02.22
 */
public class Node {

	private Node pre;
	private List<Node> children;
	private Token token;

	private Symbol symbol;

	private int productionIndex;

	public Node(Symbol symbol) {
		this.symbol = symbol;
		this.productionIndex = -1;
		this.children = new ArrayList<>();
	}

	public Token getToken() {
		checkHasToken();
		return token;
	}

	public void setToken(Token token) {
		checkHasToken();
		this.token = token;
	}

	private void checkHasToken() {
		Assert.isTrue(!symbol.isNonTerminal(), "非终结符结点没有对应Token: " + symbol.getCode());
	}

	/**
	 * 获取上次使用的产生式的下标
	 * @return
	 */
	public int getProductionIndex() {
		checkHasChildren();
		return productionIndex;
	}

	private void checkHasChildren() {
		Assert.isTrue(symbol.isNonTerminal(), "终结符结点没有子节点: " + symbol.getCode());
	}

	/**
	 * 获取当前非终结符结点所使用的产生式
	 * 若当前结点不是非终结符结点或尚未使用产生式将抛出异常
	 * @return
	 */
	public Production getNowProduction() {
		return ((NonTerminalSymbol) symbol).getProductions().get(getProductionIndex());
	}

	public void setProductionIndex(int productionIndex) {
		checkHasChildren();
		this.productionIndex = productionIndex;
	}

	public Symbol getSymbol() { return symbol; }

	/**
	 * 设置子节点列表
	 * @param children
	 */
	public void setChildren(List<Node> children) {
		checkHasChildren();
		this.children = children;
	}

	public List<Node> getChildren() {
		checkHasChildren();
		return children;
	}

	public Node getPre() { return pre; }

	public void setPre(Node pre) { this.pre = pre; }

	/**
	 * 获取当前结点及子节点所匹配的原始字符串
	 * @return
	 */
	public String getOrigin() {
		if (Symbol.isEpsilon(symbol)) {
			return "";
		}
		if (!symbol.isNonTerminal()) {
			return token.getOrigin();
		}
		StringBuilder sb = new StringBuilder();
		if (symbol.isNonTerminal()) {
			for (Node c : children) {
				sb.append(c.getOrigin());
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		boolean isNonterminal = symbol.isNonTerminal();
		return String.format(
			"Node [symbol=%s, pre=%s%s%s]",
			symbol.getCode(), pre,
			(isNonterminal ? String.format(", productionIndex = %d", productionIndex) : ""),
			(isNonterminal ? String.format(", children = %s", children) : "")
		);
	}

}
