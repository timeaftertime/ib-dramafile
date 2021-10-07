package cn.milai.ib.drama.dramafile.compiler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cn.milai.beginning.collection.Mapping;
import cn.milai.common.ex.unchecked.Uncheckeds;
import cn.milai.common.io.InputStreams;
import cn.milai.ib.drama.dramafile.compiler.backend.CFG;
import cn.milai.ib.drama.dramafile.compiler.backend.CompilerData;
import cn.milai.ib.drama.dramafile.compiler.backend.Method;
import cn.milai.ib.drama.dramafile.compiler.constant.Constant;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.CharScanner;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.Lexer;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.TokenDefinition;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.GrammerReader;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Keywords;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Parser;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.TokenType;

/**
 * 剧本编译器
 * @author milai
 * @date 2020.02.29
 */
public class IBCompiler {

	private static final String MAGIC_NUMBER = "IFNTBT";

	private static final int majorVersion = 1;

	private static final int minorVersion = 0;

	private static final String GRAMMER_FILE = "/grammer.txt";

	public static byte[] compile(InputStream in) {
		CharScanner input = new CharScanner(filterCommentLines(InputStreams.readLines(in)));
		return Uncheckeds.rethrow(
			() -> build(
				CFG.parse(
					newParser().parse(
						newLexer().lex(input)
					)
				)
			)
		);
	}

	private static String filterCommentLines(List<String> lines) {
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			if (line.trim().startsWith(Keywords.COMMENT_START)) {
				continue;
			}
			sb.append(line + "\n");
		}
		return sb.toString();
	}

	private static Parser newParser() {
		return new Parser(GrammerReader.parseGrammer(IBCompiler.class.getResourceAsStream(GRAMMER_FILE)));
	}

	private static Lexer newLexer() {
		return new Lexer(Mapping.set(TokenType.values(), t -> new TokenDefinition(t.getRE(), t.getCode())));
	}

	private static byte[] build(CompilerData data) throws IOException {
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
