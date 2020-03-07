package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import java.util.List;
import java.util.Map;

import cn.milai.ib.drama.dramafile.act.ActType;
import cn.milai.ib.drama.dramafile.compiler.ConstantTable;
import cn.milai.ib.drama.dramafile.compiler.backend.ByteArrayBuilder;
import cn.milai.ib.drama.dramafile.compiler.backend.ValueType;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

public class AddExpr implements Expr {

	private String characterClass;
	private Params params;

	public AddExpr(List<Node> children) {
		characterClass = children.get(1).getToken().getOrigin();
		params = new Params(children.get(3).getChildren());
	}

	@Override
	public byte[] toBytes(Map<String, String> alias, ConstantTable table) {
		ByteArrayBuilder bb = new ByteArrayBuilder();
		bb.appendUInt16(ActType.ADD.getCode());
		bb.appendUInt16(table.utf8Index(alias.get(characterClass)));
		bb.append(params.toBytes(alias, table));
		return bb.toBytes();
	}

	@Override
	public ValueType getType() {
		throw new UnsupportedOperationException("暂不支持获取 Add 表达式的类型");
	}

}
