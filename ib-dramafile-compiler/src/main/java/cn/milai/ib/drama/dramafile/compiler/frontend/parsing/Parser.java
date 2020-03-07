package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

import java.util.List;
import java.util.Stack;

import org.apache.commons.compress.utils.Lists;

import cn.milai.ib.drama.dramafile.compiler.ex.IBCompilerException;

/**
 * 自顶向下语法解析器
 * @author milai
 * @date 2020.02.22
 */
public class Parser {

	private Grammer grammer;

	public Parser(Grammer grammer) {
		this.grammer = grammer;
	}

	public Node parse(TokenInput input) {
		input = input.filter(t -> !t.getType().equals(TokenType.BLANK));
		Stack<Node> stack = new Stack<>();
		Node root = new Node(grammer.getStartSymbol());
		// null 表示匹配结束
		stack.push(null);
		stack.push(root);
		Node last = null;
		while (!stack.isEmpty()) {
			Token token = input.getNext();
			Node now = stack.pop();
			if (now == null) {
				if (token != null) {
					rollback(input, now, stack);
					continue;
				}
				return root;
			}
			now.setPre(last);
			last = now;
			Symbol symbol = now.getSymbol();
			if (symbol.isNonTerminal()) {
				if (selectNextProduction(input, stack, now)) {
					continue;
				}
				rollback(input, now, stack);
				continue;
			}
			if (match(symbol, token)) {
				now.setToken(token);
				input.next();
				continue;
			}
			rollback(input, now, stack);
		}
		throw new IllegalStateException("匹配完成前栈空");
	}

	private void rollback(TokenInput input, Node now, Stack<Node> stack) {
		while (true) {
			// 回到当前结点的父节点或左兄弟节点
			now = now.getPre();
			// 回滚到根节点，回滚失败
			if (now == null) {
				throw new IBCompilerException("匹配失败");
			}
			// 对于之前匹配的终结符，直接回退一个输入单词
			if (!now.getSymbol().isNonTerminal()) {
				input.seek(-1);
				continue;
			}
			NonTerminalSymbol s = (NonTerminalSymbol) now.getSymbol();
			Production p = s.getProductions().get(now.getProductionIndex());
			if(!p.isEpsilon()) {
				for (Symbol child : p.getRights()) {
					// 弹出在栈中尚未匹配的兄弟节点
					if (stack.peek() != null && stack.peek().getSymbol() == child) {
						stack.pop();
					}
				}
			}
			if (selectNextProduction(input, stack, now)) {
				return;
			}
		}
	}

	/**
	 * 选择当前结点能使用的下一个展开式
	 * 返回是否成功选择下一个展开式
	 * @param input
	 * @param stack
	 * @param now
	 * @return
	 */
	private boolean selectNextProduction(TokenInput input, Stack<Node> stack, Node now) {
		NonTerminalSymbol symbol = (NonTerminalSymbol) now.getSymbol();
		List<Production> productions = symbol.getProductions();
		for (int i = now.getProductionIndex() + 1; i < productions.size(); i++) {
			Production p = productions.get(i);
			if (contains(p, input.getNext())) {
				if (p.isEpsilon()) {
					return true;
				}
				List<Node> children = Lists.newArrayList();
				for (Symbol child : p.getRights()) {
					children.add(new Node(child));
				}
				now.setChildren(children);
				for (int j = children.size() - 1; j >= 0; j--) {
					stack.push(children.get(j));
				}
				now.setProductionIndex(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * 待匹配 token 为 nowToken 时是否可以使用该产生式
	 * @param p
	 * @param nowToken
	 * @return
	 */
	private boolean contains(Production p, Token nowToken) {
		// 输入匹配完时，token 为 null ，但可能仍有剩余的、可以推导出空串的非终结符
		// 此时应该使用 SELECT 集包含 $ 的推导式
		String type = nowToken == null ? "$" : nowToken.getType().getCode();
		for (Symbol s : grammer.getSelect(p)) {
			if (s.getCode().equals(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断终结符 s 是否与 token 匹配
	 * @param s
	 * @param token
	 * @return
	 */
	private static boolean match(Symbol s, Token token) {
		if (s == null) {
			throw new IllegalArgumentException("符号不能为 null");
		}
		if (!(s instanceof TerminalSymbol)) {
			throw new IllegalArgumentException("符号必须为终结符：" + s);
		}
		return s.getCode().equals(token.getType().getCode());
	}

}
