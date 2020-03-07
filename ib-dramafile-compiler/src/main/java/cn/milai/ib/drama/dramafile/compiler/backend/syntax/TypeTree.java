package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import java.util.List;

import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * Type 语法树
 * @author milai
 * @date 2020.03.02
 */
public class TypeTree {

	private String origin;

	public TypeTree(List<Node> typeNode) {
		origin = typeNode.get(0).getToken().getOrigin();
	}

	public String getOrigin() {
		return origin;
	}

}
