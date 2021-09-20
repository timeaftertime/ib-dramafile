package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import cn.milai.beginning.collection.Mapping;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.TokenType;

/**
 * {@link Lexer} 测试工具类
 * @author milai
 * @date 2021.09.16
 */
public class LexTestUtils {

	public static final Lexer LEXER = new Lexer(
		Mapping.set(TokenType.values(), t -> new TokenDefinition(t.getRE(), t.getCode()))
	);

}
