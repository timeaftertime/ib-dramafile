package cn.milai.ib.drama.dramafile.compiler.backend;

import java.util.List;
import java.util.Map;

import cn.milai.ib.drama.dramafile.compiler.ConstantTable;

/**
 * 剧本的方法
 * @author milai
 * @date 2020.02.28
 */
public class Method {

	private String name;
	private String descriptor;

	private List<Stmd> stmds;

	public Method(String name, String descriptor, List<Stmd> stmds) {
		this.name = name;
		this.descriptor = descriptor;
		this.stmds = stmds;
	}

	public byte[] toBytes(Map<String, String> alias, ConstantTable table) {
		ByteArrayBuilder bytes = new ByteArrayBuilder();
		bytes.appendUInt16(table.utf8Index(name));
		bytes.appendUInt16(table.utf8Index(descriptor));
		ByteArrayBuilder codeBytes = new ByteArrayBuilder();
		for (Stmd stmd : stmds) {
			codeBytes.append(stmd.toBytes(alias, table));
		}
		bytes.appendUInt16(codeBytes.toBytes().length);
		bytes.append(codeBytes);
		return bytes.toBytes();
	}

}
