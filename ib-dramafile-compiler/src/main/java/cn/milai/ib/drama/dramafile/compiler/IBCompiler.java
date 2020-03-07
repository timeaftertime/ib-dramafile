package cn.milai.ib.drama.dramafile.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

import cn.milai.ib.drama.dramafile.compiler.backend.DramaBuilder;
import cn.milai.ib.drama.dramafile.compiler.backend.SimpleTranslator;
import cn.milai.ib.drama.dramafile.compiler.ex.IBCompilerException;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.CharInput;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.Lexer;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.TokenDef;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.GrammerReader;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Parser;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.TokenType;
import cn.milai.ib.util.IOUtil;

/**
 * 剧本编译器
 * @author milai
 * @date 2020.02.29
 */
public class IBCompiler {

	private static final String GRAMMER_DEFINITION = "/grammer.txt";

	public static byte[] compile(InputStream in) {
		try {
			CharInput input = new CharInput(IOUtil.toStringFilter(in, line -> !line.trim().startsWith("#")));
			return DramaBuilder.build(
				new SimpleTranslator().translate(
					newParser().parse(
						newLexer().lex(
							input))));
		} catch (IOException e) {
			throw new IBCompilerException(e);
		}
	}

	private static Parser newParser() {
		return new Parser(GrammerReader.parseGrammer(IBCompiler.class.getResourceAsStream(GRAMMER_DEFINITION)));
	}

	private static Lexer newLexer() {
		return new Lexer(Arrays.stream(TokenType.values())
			.map(t -> new TokenDef(t.getRE(), t.getCode())).collect(Collectors.toSet()));
	}
}
