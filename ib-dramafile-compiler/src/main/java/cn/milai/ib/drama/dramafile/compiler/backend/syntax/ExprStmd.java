package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import java.util.Map;

import cn.milai.ib.drama.dramafile.compiler.ConstantTable;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * 简单包含一个 Expr 的 Stmd
 * @author milai
 * @date 2020.02.29
 */
public class ExprStmd implements Stmd {

	private Expr expr;

	public ExprStmd(Node exprNode) {
		expr = Expr.build(exprNode.getChildren());
	}

	@Override
	public byte[] toBytes(Map<String, String> alias, ConstantTable table) {
		return expr.toBytes(alias, table);
	}
}
