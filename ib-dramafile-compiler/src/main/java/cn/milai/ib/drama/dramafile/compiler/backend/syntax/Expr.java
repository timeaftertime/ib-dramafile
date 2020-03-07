package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import java.util.List;

import cn.milai.ib.drama.dramafile.compiler.ex.UnknownChildrenException;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * 对应 Expr 语法树
 * @author milai
 * @date 2020.02.29
 */
public interface Expr extends ValueSyntaxTree {

	public static final String NODE_CODE = "Expr";

	/**
	 * 根据 Expr 节点的子节点列表构造对应的 Expr 实例
	 * @param children
	 * @return
	 */
	static Expr build(List<Node> children) {
		switch (children.get(0).getSymbol().getCode()) {
			case Term.NODE_CODE : {
				return new TermExpr(children);
			}
			case "ADD" : {
				return new AddExpr(children);
			}
			case "IMG" : {
				return new ImgExpr(children);
			}
			case "AUDIO" : {
				return new AudioExpr(children);
			}
			default: {
				throw new UnknownChildrenException(NODE_CODE, children);
			}
		}
	}
}
