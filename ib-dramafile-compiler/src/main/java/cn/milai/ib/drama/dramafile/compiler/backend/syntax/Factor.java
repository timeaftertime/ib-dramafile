package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import java.util.List;

import cn.milai.ib.drama.dramafile.compiler.ex.UnknownChildrenException;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * 对应 Factory 语法树
 * @author milai
 * @date 2020.02.29
 */
public interface Factor extends ValueSyntaxTree {

	public static final String NODE_CODE = "Factor";

	static Factor build(List<Node> children) {
		switch (children.get(0).getSymbol().getCode()) {
			case "Const" :
				return new ConstFactor(children.get(0).getChildren());
			default: {
				throw new UnknownChildrenException(NODE_CODE, children);
			}
		}
	}

}
