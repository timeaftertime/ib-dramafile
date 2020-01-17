package cn.milai.ib.compiler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import cn.milai.ib.compiler.constant.Constant;
import cn.milai.ib.constant.ActType;

/**
 * 临时简单编译器实现，目前实际上只是一个简单翻译器
 * 2019.12.21
 * @author milai
 */
public class SimpleCompiler {

	private static final String MAGIC_NUMBER = "IFNTBT";

	private static final int majorVersion = 1;

	private static final int minorVersion = 0;

	public static byte[] compile(InputStream in) throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(new BOMInputStream(in), Charsets.UTF_8));
		List<String> actions = Lists.newArrayList();
		String line = null;
		while ((line = input.readLine()) != null) {
			actions.add(line);
		}
		input.close();
		return doCompile(actions);
	}

	private static byte[] doCompile(List<String> actions) throws NumberFormatException, IOException {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutput);
		ConstantTable constantTable = new ConstantTable();

		writeMagicNumber(out);
		writeVersions(out);

		byte[] actionBytes = parseActions(actions, constantTable);
		writeConstantTable(out, constantTable);
		out.write(actionBytes);

		return byteOutput.toByteArray();
	}

	private static void writeMagicNumber(DataOutputStream out) throws IOException {
		out.writeUTF(MAGIC_NUMBER);
	}

	private static void writeVersions(DataOutputStream out) throws IOException {
		out.writeShort(majorVersion);
		out.writeShort(minorVersion);
	}

	private static void writeConstantTable(DataOutputStream out, ConstantTable constantTable) throws IOException {
		Constant<?>[] constants = constantTable.getConstants();
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

	private static byte[] parseActions(List<String> actions, ConstantTable table) throws IOException {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutput);
		for (String line : actions) {
			if (line.trim().startsWith("#")) {
				continue;
			}
			String[] tokens = line.split("\\s+");
			String command = tokens[0].trim().toLowerCase();
			switch (ActType.findByName(command)) {
				case NEW : {
					out.writeByte(ActType.NEW.getCode());
					// characterClass
					out.writeShort(table.utf8Index(tokens[1]));
					// xRate
					out.writeShort(table.floatIndex(Float.parseFloat(tokens[2])));
					// yRate
					out.writeShort(table.floatIndex(Float.parseFloat(tokens[3])));
					break;
				}
				case SLEEP : {
					out.writeByte(ActType.SLEEP.getCode());
					// sleepFrame
					out.writeShort(table.longIndex(Long.parseLong(tokens[1])));
					break;
				}
				case DIALOG : {
					out.writeByte(ActType.DIALOG.getCode());
					// characterClass
					out.writeShort(table.utf8Index(tokens[1]));
					// xRate
					out.writeShort(table.floatIndex(Float.parseFloat(tokens[2])));
					// yRate
					out.writeShort(table.floatIndex(Float.parseFloat(tokens[3])));
					// text
					out.writeShort(table.utf8Index(String.join(" ", Arrays.copyOfRange(tokens, 4, tokens.length)).replace("\\n", "\n")));
					break;
				}
				default :
					throw new IllegalArgumentException(String.format("未知动作指令 %s", tokens[0]));
			}
		}
		return byteOutput.toByteArray();
	}

}
