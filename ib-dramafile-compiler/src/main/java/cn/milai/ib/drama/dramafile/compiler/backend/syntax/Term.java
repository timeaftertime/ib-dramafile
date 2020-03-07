package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import java.util.List;

import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * 对应 Term 语法树
 * @author milai
 * @date 2020.02.29
 */
public interface Term extends ValueSyntaxTree {

	public static final String NODE_CODE = "Term";

	/**
	 * 构造子节点列表对应的 Term 语法树
	 * @param children
	 * @return
	 */
	static Term build(List<Node> children) {
		return new FactorTerm(children);
	}

}
