package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import java.util.List;
import java.util.Map;

import cn.milai.ib.drama.dramafile.compiler.ConstantTable;
import cn.milai.ib.drama.dramafile.compiler.backend.ValueType;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * 简单持有一个 Factor 的 Term
 * @author milai
 * @date 2020.02.29
 */
public class FactorTerm implements Term {

	private Factor factor;

	public FactorTerm(List<Node> children) {
		factor = Factor.build(children.get(0).getChildren());
	}

	@Override
	public byte[] toBytes(Map<String, String> alias, ConstantTable table) {
		return factor.toBytes(alias, table);
	}

	@Override
	public ValueType getType() {
		return factor.getType();
	}

}
