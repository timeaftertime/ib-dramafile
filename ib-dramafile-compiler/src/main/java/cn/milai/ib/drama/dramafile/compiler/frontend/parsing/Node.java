package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

import java.util.List;

import com.google.common.collect.Lists;

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
		this.children = Lists.newArrayList();
	}

	public Token getToken() {
		if (symbol.isNonTerminal()) {
			throw new UnsupportedOperationException("非终结符结点没有对应Token");
		}
		return token;
	}

	public void setToken(Token token) {
		if (symbol.isNonTerminal()) {
			throw new UnsupportedOperationException("非终结符结点没有对应Token");
		}
		this.token = token;
	}

	/**
	 * 获取上次使用的产生式的下标
	 * @return
	 */
	public int getProductionIndex() {
		if (!symbol.isNonTerminal()) {
			throw new UnsupportedOperationException("终结符结点没有子节点");
		}
		return productionIndex;
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
		if (!symbol.isNonTerminal()) {
			throw new UnsupportedOperationException("终结符结点没有子节点");
		}
		this.productionIndex = productionIndex;
	}

	public Symbol getSymbol() { return symbol; }

	/**
	 * 设置子节点列表
	 * @param children
	 */
	public void setChildren(List<Node> children) {
		if (!symbol.isNonTerminal()) {
			throw new UnsupportedOperationException("终结符结点没有子节点");
		}
		this.children = children;
	}

	public List<Node> getChildren() {
		if (!symbol.isNonTerminal()) {
			throw new UnsupportedOperationException("终结符结点没有子节点：symbol = " + symbol);
		}
		return children;
	}

	public Node getPre() { return pre; }

	public void setPre(Node pre) { this.pre = pre; }

	/**
	 * 获取当前结点及子节点所匹配的原始字符串
	 * @return
	 */
	public String getOrigin() {
		StringBuilder sb = new StringBuilder();
		parseOrigin(sb, this);
		return sb.toString();
	}

	private static void parseOrigin(StringBuilder sb, Node node) {
		if (Symbol.isEpsilon(node.symbol)) {
			return;
		}
		if (node.symbol.isNonTerminal()) {
			for (Node c : node.children) {
				parseOrigin(sb, c);
			}
		} else {
			sb.append(node.token.getOrigin());
		}
	}

	@Override
	public String toString() {
		return "Node [symbol=" + symbol + ", pre=" + (pre == null ? pre : pre.symbol)
			+ (symbol.isNonTerminal() ? ", productionIndex=" + productionIndex : "")
			+ (symbol.isNonTerminal() ? ", children=" + children : "")
			+ (symbol.isNonTerminal() ? "" : ", token=" + token)
			+ "]";
	}

}
