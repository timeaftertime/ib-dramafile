package cn.milai.ib.compiler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.collect.Lists;

import cn.milai.ib.constant.ActType;

/**
 * 临时简单编译器实现，目前实际上只是一个简单翻译器
 * 2019.12.21
 * @author milai
 */
public class SimpleCompiler {

	public static byte[] compile(InputStream in) throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(in));
		List<String> drama = Lists.newArrayList();
		String line = null;
		while ((line = input.readLine()) != null) {
			drama.add(line);
		}
		return doCompile(drama);
	}

	private static byte[] doCompile(List<String> drama)
			throws NumberFormatException, IOException {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutput);
		for (String line : drama) {
			if (line.trim().startsWith("#")) {
				continue;
			}
			String[] tokens = line.split("\\s+");
			switch (ActType.findByName(tokens[0].trim().toLowerCase())) {
				case NEW : {
					out.writeByte(ActType.NEW.getCode());
					// characterClass
					out.writeUTF(tokens[1]);
					// xRate
					out.writeFloat(Float.parseFloat(tokens[2]));
					// yRate
					out.writeFloat(Float.parseFloat(tokens[3]));
					break;
				}
				case SLEEP : {
					out.writeByte(ActType.SLEEP.getCode());
					// sleepFrame
					out.writeLong(Long.parseLong(tokens[1]));
					break;
				}
				default :
					throw new IllegalArgumentException(
							String.format("未知动作指令 %s", tokens[0]));
			}
		}
		return byteOutput.toByteArray();
	}

}
