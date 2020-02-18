package cn.milai.ib.compiler.frontend.parsing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;

import cn.milai.ib.compiler.ex.IBCompilerException;

/**
 * 语法定义文件解析器
 * @author milai
 * @date 2020.02.14
 */
public class GrammerReader {

	/**
	 * 从输入流读取并解析语法
	 * @param input
	 * @return
	 */
	public static Grammer parseGrammer(InputStream input) {
		BufferedReader in = new BufferedReader(new InputStreamReader(input, Charsets.UTF_8));
		Map<String, String> punctualToCode = Maps.newHashMap();
		int lineNumber = 0;
		Grammer.Builder builder = new Grammer.Builder();
		for (String line : in.lines().collect(Collectors.toList())) {
			lineNumber++;
			if (StringUtils.isEmpty(line) || line.trim().startsWith("#")) {
				continue;
			}
			String[] words = line.split("\\s+");
			if (words.length < 3) {
				throw new IBCompilerException(String.format("语法定义不合法 line %d： %s", lineNumber, line));
			}
			if (words[1].equals("=")) {
				punctualToCode.put(words[0], words[2]);
				continue;
			}
			for (int i = 0; i < words.length; i++) {
				if (i == 1) {
					continue;
				}
				words[i] = codeOf(punctualToCode, words[i]);
			}
			if (words[1].equals("->")) {
				builder.addProduction(words[0], Arrays.copyOfRange(words, 2, words.length));
				continue;
			}
			throw new IBCompilerException(String.format("未知语法 line %d： %s", lineNumber, line));
		}
		return builder.build();
	}

	/**
	 * 获取 str 所对应的 code
	 * @param refs
	 * @param str
	 */
	private static String codeOf(Map<String, String> refs, String str) {
		if (refs.containsKey(str)) {
			return refs.get(str);
		}
		return str;
	}
}
