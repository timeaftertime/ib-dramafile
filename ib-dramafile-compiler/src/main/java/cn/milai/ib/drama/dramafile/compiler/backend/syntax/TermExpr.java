package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import java.util.List;
import java.util.Map;

import cn.milai.ib.drama.dramafile.compiler.ConstantTable;
import cn.milai.ib.drama.dramafile.compiler.backend.ValueType;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * 简单持有一个 Term 的 Expr
 * @author milai
 * @date 2020.02.29
 */
public class TermExpr implements Expr {

	private Term term;

	public TermExpr(List<Node> children) {
		this.term = Term.build(children.get(0).getChildren());
	}

	@Override
	public byte[] toBytes(Map<String, String> alias, ConstantTable table) {
		return term.toBytes(alias, table);
	}

	@Override
	public ValueType getType() {
		return term.getType();
	}

}
