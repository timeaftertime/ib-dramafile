package cn.milai.ib.drama.dramafile.compiler.backend;

import java.util.List;
import java.util.Map;

import cn.milai.common.util.Strings;
import cn.milai.ib.drama.dramafile.act.ActType;
import cn.milai.ib.drama.dramafile.compiler.ConstantTable;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * Stmd 语法树
 * @author milai
 * @date 2020.04.16
 */
public class Stmd {

	private Node node;

	public Stmd(Node stmdNode) {
		node = stmdNode;
	}

	public byte[] toBytes(Map<String, String> alias, ConstantTable table) {
		List<Node> children = node.getChildren();
		ByteArrayBuilder bb = new ByteArrayBuilder();
		switch (children.get(0).getToken().getType()) {
			// Stmd -> SLEEP ( INT ) ;
			case SLEEP : {
				bb.appendByte(ActType.SLEEP.getCode());
				bb.appendUInt16(table.longIndex(Long.parseLong(children.get(2).getOrigin())));
				break;
			}
			// Stmd -> ADD IDENTIFIER ( Params ) ;
			case ADD : {
				String desciptor = parseParams(bb, alias, table, children.get(3));
				bb.appendByte(ActType.ADD.getCode());
				String className = children.get(1).getOrigin();
				if (alias.containsKey(className)) {
					className = alias.get(children.get(1).getOrigin());
				}
				bb.appendUInt16(table.utf8Index(className));
				bb.appendUInt16(table.utf8Index(desciptor));
				break;
			}
			case IMG : {
				throw new UnsupportedOperationException("尚未实现");
			}
			case AUDIO : {
				throw new UnsupportedOperationException("尚未实现");
			}
			default:
				throw new IllegalArgumentException("Stmd 结点出现未知子节点：" + children);
		}
		return bb.toBytes();
	}

	/**
	 * 解析 Params 语法树，并将对应字节写入 {@link ByteArrayBuilder}
	 * 返回需要调用的函数的参数类型描述字符串
	 * @param bb
	 * @param alias
	 * @param table
	 * @param paramsNode
	 * @return
	 */
	private static String parseParams(ByteArrayBuilder bb, Map<String, String> alias, ConstantTable table,
		Node paramsNode) {
		List<Node> children = paramsNode.getChildren();
		StringBuilder sb = new StringBuilder();
		// Params -> ϵ
		if (children.isEmpty()) {
			return sb.toString();
		}
		// Params -> FirstParam NonFirstParams
		sb.append(parseFirstParam(bb, alias, table, children.get(0)));
		Node nonFirstParams = children.get(1);
		while (!(children = nonFirstParams.getChildren()).isEmpty()) {
			// NonFirstParams -> COMMA FirstParam NonFirstParams
			sb.append(parseFirstParam(bb, alias, table, nonFirstParams.getChildren().get(1)));
			nonFirstParams = nonFirstParams.getChildren().get(2);
		}
		return sb.toString();
	}

	private static String parseFirstParam(ByteArrayBuilder bb, Map<String, String> alias, ConstantTable table,
		Node firstParamNode) {
		List<Node> children = firstParamNode.getChildren();
		Node first = children.get(0);
		switch (first.getToken().getType()) {
			// FirstParam -> INT
			case INT : {
				bb.appendByte(ActType.LDC.getCode());
				bb.appendUInt16(table.int32Index(Integer.parseInt(first.getOrigin())));
				return ValueType.INT.getCanonical();
			}
			// FirstParam -> FLOAT
			case FLOAT : {
				bb.appendByte(ActType.LDC.getCode());
				bb.appendUInt16(table.floatIndex(Float.parseFloat(first.getOrigin())));
				return ValueType.FLOAT.getCanonical();
			}
			// FirstParam -> STR
			case STR : {
				bb.appendByte(ActType.LDC.getCode());
				bb.appendUInt16(table.utf8Index(Strings.slice(first.getOrigin(), 1, -1)));
				return ValueType.STR.getCanonical();
			}
			case ADD : {
				throw new UnsupportedOperationException("尚未实现");
			}
			// FirstParam -> IMG ( STR )
			case IMG : {
				bb.appendByte(ActType.IMG.getCode());
				bb.appendUInt16(table.utf8Index(Strings.slice(children.get(2).getOrigin(), 1, -1)));
				return ValueType.IMG.getCanonical();
			}
			// FirstParam -> AUDIO ( STR , STR )
			case AUDIO : {
				bb.appendByte(ActType.AUDIO.getCode());
				bb.appendUInt16(table.utf8Index(Strings.slice(children.get(2).getOrigin(), 1, -1)));
				bb.appendUInt16(table.utf8Index(Strings.slice(children.get(4).getOrigin(), 1, -1)));
				return ValueType.AUDIO.getCanonical();
			}
			default:
				throw new IllegalArgumentException("FirstParam 结点出现未知子节点：" + children);
		}
	}

}
