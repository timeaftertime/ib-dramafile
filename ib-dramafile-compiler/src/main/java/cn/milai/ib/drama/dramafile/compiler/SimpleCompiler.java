package cn.milai.ib.drama.dramafile.compiler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.milai.ib.drama.dramafile.act.ActType;
import cn.milai.ib.drama.dramafile.compiler.constant.Constant;
import cn.milai.ib.drama.dramafile.compiler.ex.IBCompilerException;

/**
 * 临时简单编译器实现，目前实际上只是一个简单翻译器
 * 2019.12.21
 * @author milai
 */
public class SimpleCompiler {

	private static final Logger log = LoggerFactory.getLogger(SimpleCompiler.class);

	private static final String MAGIC_NUMBER = "IFNTBT";

	private static final int majorVersion = 1;

	private static final int minorVersion = 0;

	private static final String EMPTY_PLACEHOLDER = "_";

	private static final String DEFINE = "define";

	private static final String DEFINE_DRAMA_CODE = "dramacode";

	private static final String DEFINE_DRAMA_NAME = "dramaname";

	public static byte[] compile(InputStream in) {
		BufferedReader input = new BufferedReader(
			new InputStreamReader(new BOMInputStream(in), StandardCharsets.UTF_8)
		);
		List<String> actions = new ArrayList<>();
		String line = null;
		try {
			while ((line = input.readLine()) != null) {
				actions.add(line);
			}
			input.close();
			return doCompile(actions);
		} catch (IOException e) {
			log.error("编译失败", e);
			throw new IBCompilerException(e);
		}
	}

	private static byte[] doCompile(List<String> actions) throws NumberFormatException, IOException {
		SimpleCompilerData data = new SimpleCompilerData();
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutput);

		writeMagicNumber(out);
		writeVersions(out);

		parseDefineAndActions(actions, data);
		writeData(out, data);

		return byteOutput.toByteArray();
	}

	private static void writeMagicNumber(DataOutputStream out) throws IOException {
		out.writeUTF(MAGIC_NUMBER);
	}

	private static void writeVersions(DataOutputStream out) throws IOException {
		out.writeShort(majorVersion);
		out.writeShort(minorVersion);
	}

	private static void writeData(DataOutputStream out, SimpleCompilerData data) throws IOException {
		ConstantTable table = data.getConstTable();
		// dramaCode index
		String dramaCode = data.getDramaCode();
		if (dramaCode == null) {
			throw new IBCompilerException("必须指定 dramaCode");
		}
		out.writeShort(table.utf8Index(dramaCode));
		// dramaName index
		out.writeShort(table.utf8Index(data.getDramaName() == null ? dramaCode : data.getDramaName()));
		// Constants
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
		// actionBytes
		out.write(data.getActionBytes());
	}

	private static void parseDefineAndActions(List<String> actions, SimpleCompilerData data) throws IOException {
		ConstantTable table = data.getConstTable();
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutput);
		for (String line : actions) {
			if (StringUtils.isEmpty(line) || line.trim().startsWith("#")) {
				continue;
			}
			String[] tokens = line.trim().split("\\s+");
			String command = tokens[0].trim().toLowerCase();
			switch (command) {
				case DEFINE : {
					data.define(tokens[1], tokens[2]);
					continue;
				}
				case DEFINE_DRAMA_CODE : {
					data.setDramaCode(tokens[1]);
					continue;
				}
				case DEFINE_DRAMA_NAME : {
					data.setDramaName(tokens[1]);
					continue;
				}
			}
			switch (ActType.findByName(command)) {
				case ADD : {
					out.writeByte(ActType.ADD.getCode());
					// characterClass
					out.writeShort(table.utf8Index(getRealValue(data, tokens[1])));
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
					out.writeShort(table.utf8Index(getRealValue(data, tokens[1])));
					// xRate
					out.writeShort(table.floatIndex(Float.parseFloat(tokens[2])));
					// yRate
					out.writeShort(table.floatIndex(Float.parseFloat(tokens[3])));
					// spearkerClass
					int speakerClassIndex = EMPTY_PLACEHOLDER.equals(tokens[4]) ? 0
						: table.utf8Index(getRealValue(data, tokens[4]));
					out.writeShort(speakerClassIndex);
					// textCode
					out.writeShort(table.utf8Index(getRealValue(data, tokens[5])));
					break;
				}
				case BGM : {
					out.writeByte(ActType.BGM.getCode());
					// resource
					out.writeShort(table.utf8Index(getRealValue(data, tokens[1])));
					break;
				}
				case BGI : {
					out.writeByte(ActType.BGI.getCode());
					// resource
					out.writeShort(table.utf8Index(getRealValue(data, tokens[1])));
					break;
				}
				default:
					throw new IllegalArgumentException(String.format("未知动作指令 %s", tokens[0]));
			}
		}
		data.setActionBytes(byteOutput.toByteArray());
	}

	/**
	 * 获取一个字符串实际代表的值
	 * 若为占位符，返回宏定义的值，否则直接返回原字符串
	 * 若占位符未定义，返回 null
	 * @param data
	 * @param str
	 * @return
	 */
	private static String getRealValue(SimpleCompilerData data, String str) {
		if (str.startsWith("${") && str.endsWith("}")) {
			return data.getDefined(str.substring(2, str.length() - 1));
		}
		return str;
	}

}
