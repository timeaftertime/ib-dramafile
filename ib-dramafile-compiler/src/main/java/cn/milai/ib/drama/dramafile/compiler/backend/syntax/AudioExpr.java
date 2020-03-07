package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import java.util.List;
import java.util.Map;

import cn.milai.ib.drama.dramafile.compiler.ConstantTable;
import cn.milai.ib.drama.dramafile.compiler.backend.ByteArrayBuilder;
import cn.milai.ib.drama.dramafile.compiler.backend.ValueType;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;
import cn.milai.ib.util.StringUtil;

/**
 * 表示一个音频资源的 Expr
 * @author milai
 * @date 2020.03.03
 */
public class AudioExpr implements Expr {

	private String resource;

	public AudioExpr(List<Node> children) {
		resource = StringUtil.slice(children.get(2).getToken().getOrigin(), 1, -1);
	}

	@Override
	public ValueType getType() {
		return ValueType.AUDIO;
	}

	@Override
	public byte[] toBytes(Map<String, String> alias, ConstantTable table) {
		return new ByteArrayBuilder().appendUInt16(table.utf8Index(resource)).toBytes();
	}

}
