package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import java.util.List;

import cn.milai.ib.drama.dramafile.compiler.ex.UnknownChildrenException;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * 对应 Stmd 语法树
 * @author milai
 * @date 2020.02.29
 */
public interface Stmd extends SyntaxTree {

	public static final String NODE_CODE = "Stmd";

	/**
	 * 根据 Stmd 节点的子节点列表构造一个 Stmd 实例
	 * @param children
	 * @return
	 */
	static Stmd build(List<Node> children) {
		switch (children.get(0).getSymbol().getCode()) {
			case Expr.NODE_CODE : {
				return new ExprStmd(children.get(0));
			}
			default: {
				throw new UnknownChildrenException(NODE_CODE, children);
			}
		}
	}

}
