package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;

import cn.milai.ib.drama.dramafile.compiler.ConstantTable;
import cn.milai.ib.drama.dramafile.compiler.backend.ByteArrayBuilder;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * Params 语法树
 * @author milai
 * @date 2020.03.02
 */
public class Params implements SyntaxTree {

	private List<Expr> exprs = Lists.newArrayList();

	public Params(List<Node> children) {
		parseExprs(children);
	}

	private void parseExprs(List<Node> children) {
		if (children.isEmpty()) {
			return;
		}
		exprs.add(Expr.build(children.get(1).getChildren()));
		parseExprs(children.get(2).getChildren());
	}

	/**
	 * 获取参数类型最简名称列表
	 * @return
	 */
	public List<String> getTypeCanonicals() {
		return exprs.stream().map(expr -> expr.getType().getCanonical()).collect(Collectors.toList());
	}

	@Override
	public byte[] toBytes(Map<String, String> alias, ConstantTable table) {
		ByteArrayBuilder bb = new ByteArrayBuilder();
		bb.appendByte(exprs.size());
		for (Expr expr : exprs) {
			bb.append(expr.toBytes(alias, table));
		}
		return bb.toBytes();
	}

}
