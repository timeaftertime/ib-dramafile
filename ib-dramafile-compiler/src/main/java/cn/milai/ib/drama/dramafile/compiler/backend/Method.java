package cn.milai.ib.drama.dramafile.compiler.backend;

import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.Lists;

import cn.milai.ib.drama.dramafile.compiler.ConstantTable;
import cn.milai.ib.drama.dramafile.compiler.backend.syntax.ParamDeclares;
import cn.milai.ib.drama.dramafile.compiler.backend.syntax.Stmd;
import cn.milai.ib.drama.dramafile.compiler.backend.syntax.SyntaxTree;
import cn.milai.ib.drama.dramafile.compiler.backend.syntax.TypeTree;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * 剧本的方法
 * @author milai
 * @date 2020.02.28
 */
public class Method implements SyntaxTree {

	private String name;
	private String descriptor;

	private List<Stmd> stmds;

	public Method(Node methodNode) {
		List<Node> children = methodNode.getChildren();
		descriptor = parseDescriptor(
			new TypeTree(children.get(0).getChildren()),
			new ParamDeclares(children.get(3).getChildren()));
		this.name = children.get(1).getToken().getOrigin();
		this.stmds = parseStmds(children.get(5));
	}

	private static String parseDescriptor(TypeTree returnType, ParamDeclares paramDeclares) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (String paramType : paramDeclares.getTypeCanonicals()) {
			sb.append(paramType);
		}
		sb.append(")");
		sb.append(ValueType.of(returnType.getOrigin()).getCanonical());
		return sb.toString();
	}

	/**
	 * 解析 Stmds 语法树
	 * @param stmdsNode
	 * @return
	 */
	private List<Stmd> parseStmds(Node stmdsNode) {
		List<Stmd> stmds = Lists.newArrayList();
		List<Node> children = stmdsNode.getChildren();
		if (children.isEmpty()) {
			return stmds;
		}
		stmds.add(Stmd.build(children.get(0).getChildren()));
		stmds.addAll(parseStmds(children.get(1)));
		return stmds;
	}

	@Override
	public byte[] toBytes(Map<String, String> alias, ConstantTable table) {
		ByteArrayBuilder bytes = new ByteArrayBuilder();
		bytes.appendUInt16(table.utf8Index(name));
		bytes.appendUInt16(table.utf8Index(descriptor));
		ByteArrayBuilder codeBytes = new ByteArrayBuilder();
		for (Stmd stmd : stmds) {
			codeBytes.append(stmd.toBytes(alias, table));
		}
		return codeBytes.toBytes();
	}

}
