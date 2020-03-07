package cn.milai.ib.drama.dramafile.compiler.backend;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cn.milai.ib.drama.dramafile.compiler.ConstantTable;
import cn.milai.ib.drama.dramafile.compiler.constant.Constant;

/**
 * 将编译数据转换为 drama 字节码的构造器
 * @author milai
 * @date 2020.03.01
 */
public class DramaBuilder {

	private static final String MAGIC_NUMBER = "IFNTBT";

	private static final int majorVersion = 1;

	private static final int minorVersion = 0;

	public static byte[] build(CompilerData data) throws IOException {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutput);
		// 魔数
		writeMagicNumber(out);
		// 主次版本号
		writeVersions(out);
		ConstantTable table = new ConstantTable();
		byte[] dramaInfoBytes = buildDramaInfoBytes(data, table);
		byte[] methodsBytes = buildMethodsBytes(data, table);
		// 常量池
		writeConstantTable(table, out);
		// 剧本 code 和 name
		out.write(dramaInfoBytes);
		// 剧本方法
		out.write(methodsBytes);
		return byteOutput.toByteArray();
	}

	private static void writeMagicNumber(DataOutputStream out) throws IOException {
		out.writeUTF(MAGIC_NUMBER);
	}

	private static void writeVersions(DataOutputStream out) throws IOException {
		out.writeShort(majorVersion);
		out.writeShort(minorVersion);
	}

	private static byte[] buildDramaInfoBytes(CompilerData data, ConstantTable table)
		throws IOException {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutput);
		// dramaCode index
		out.writeShort(table.utf8Index(data.getDramaCode()));
		// dramaName index
		out.writeShort(table.utf8Index(data.getDramaName()));
		return byteOutput.toByteArray();
	}

	private static byte[] buildMethodsBytes(CompilerData data, ConstantTable table) throws IOException {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutput);
		// 方法个数
		out.writeShort(data.getMethods().size());
		for (Method method : data.getMethods()) {
			out.write(method.toBytes(data.getImports(), table));
		}
		return byteOutput.toByteArray();
	}

	private static void writeConstantTable(ConstantTable table, DataOutputStream out) throws IOException {
		Constant<?>[] constants = table.getConstants();
		// 常量表最前面为 null ，所以实际大小为数组长度 -1
		out.writeShort(constants.length - 1);
		for (Constant<?> c : constants) {
			if (c == null) {
				continue;
			}
			out.writeByte(c.getType().getCode());
			out.write(c.getBytes());
		}
	}
}
