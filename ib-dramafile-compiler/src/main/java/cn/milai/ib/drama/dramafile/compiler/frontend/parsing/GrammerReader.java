package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cn.milai.common.io.InputStreams;
import cn.milai.ib.drama.dramafile.compiler.ex.IBCompilerException;

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
		Map<String, String> aliases = new HashMap<>();
		int lineNumber = 0;
		Grammer.Builder builder = new Grammer.Builder();
		for (String line : InputStreams.readLines(input)) {
			lineNumber++;
			if (isEmptyLine(line)) {
				continue;
			}
			String[] words = parseWords(aliases, line);
			if (words.length >= 3) {
				switch (words[1]) {
					case Keywords.ALIAS :
						aliases.put(words[0], words[2]);
						continue;
					case Keywords.PRODUCTION :
						builder.addProduction(words[0], Arrays.copyOfRange(words, 2, words.length));
						continue;
				}
			}
			throw new IBCompilerException(String.format("未知语法 line %d: %s", lineNumber, line));
		}
		return builder.build();
	}

	private static boolean isEmptyLine(String line) {
		return StringUtils.isEmpty(line) || line.trim().startsWith(Keywords.COMMENT_START);
	}

	private static String[] parseWords(Map<String, String> aliases, String line) {
		String[] words = line.split("\\s+");
		for (int i = 0; i < words.length; i++) {
			if (i == 1) {
				continue;
			}
			words[i] = aliases.getOrDefault(words[i], words[i]);
		}
		return words;
	}

}
