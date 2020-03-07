package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import java.util.List;
import java.util.Map;

import cn.milai.ib.drama.dramafile.compiler.ConstantTable;
import cn.milai.ib.drama.dramafile.compiler.backend.ByteArrayBuilder;
import cn.milai.ib.drama.dramafile.compiler.backend.ValueType;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Token;

/**
 * 简单持有一个常量的 Factor
 * @author milai
 * @date 2020.02.29
 */
public class ConstFactor implements Factor {

	private String value;
	private ValueType type;

	public ConstFactor(List<Node> children) {
		Token token = children.get(0).getToken();
		type = ValueType.ofToken(token.getType());
		value = token.getOrigin();
	}

	@Override
	public byte[] toBytes(Map<String, String> alias, ConstantTable table) {
		ByteArrayBuilder bb = new ByteArrayBuilder();
		if (type == ValueType.INT) {
			bb.appendUInt16(table.int32Index(Integer.parseInt(value)));
		} else if (type == ValueType.FLOAT) {
			bb.appendUInt16(table.floatIndex(Float.parseFloat(value)));
		} else {
			bb.appendUInt16(table.utf8Index(value));
		}
		return bb.toBytes();
	}

	@Override
	public ValueType getType() {
		return type;
	}

}
