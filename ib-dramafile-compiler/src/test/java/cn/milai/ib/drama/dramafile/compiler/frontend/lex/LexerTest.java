package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Test;

import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Token;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.TokenInput;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.TokenType;

public class LexerTest {

	@Test
	public void testIfStatement() {
		Lexer lexer = new Lexer(
			Arrays.stream(TokenType.values())
				.map(t -> new TokenDef(t.getRE(), t.getCode()))
				.collect(Collectors.toSet())
		);
		TokenInput tokens = lexer.lex(new CharInput("if(player.getLife() == 0) { gameOver(); }"));
		assertArrayEquals(
			new Token[] {
				new Token("if", TokenType.IF),
				new Token("(", TokenType.BRACKET_LEFT),
				new Token("player", TokenType.IDENTIFIER),
				new Token(".", TokenType.PROP),
				new Token("getLife", TokenType.IDENTIFIER),
				new Token("(", TokenType.BRACKET_LEFT),
				new Token(")", TokenType.BRACKET_RIGHT),
				new Token(" ", TokenType.BLANK),
				new Token("==", TokenType.EQUALS),
				new Token(" ", TokenType.BLANK),
				new Token("0", TokenType.INT),
				new Token(")", TokenType.BRACKET_RIGHT),
				new Token(" ", TokenType.BLANK),
				new Token("{", TokenType.BLOCK_LEFT),
				new Token(" ", TokenType.BLANK),
				new Token("gameOver", TokenType.IDENTIFIER),
				new Token("(", TokenType.BRACKET_LEFT),
				new Token(")", TokenType.BRACKET_RIGHT),
				new Token(";", TokenType.STMD_END),
				new Token(" ", TokenType.BLANK),
				new Token("}", TokenType.BLOCK_RIGHT),
			}, tokens.toArray()
		);
	}

	@Test
	public void testWhileAndNew() {
		Lexer lexer = new Lexer(
			Arrays.stream(TokenType.values())
				.map(t -> new TokenDef(t.getRE(), t.getCode()))
				.collect(Collectors.toSet())
		);
		TokenInput tokens = lexer.lex(
			new CharInput(
				"while(boss.isAlive()) { add(\"cn.milai.ib.character.plane.WelcomePlane\"); }"
			)
		);
		assertArrayEquals(
			new Token[] {
				new Token("while", TokenType.WHILE),
				new Token("(", TokenType.BRACKET_LEFT),
				new Token("boss", TokenType.IDENTIFIER),
				new Token(".", TokenType.PROP),
				new Token("isAlive", TokenType.IDENTIFIER),
				new Token("(", TokenType.BRACKET_LEFT),
				new Token(")", TokenType.BRACKET_RIGHT),
				new Token(")", TokenType.BRACKET_RIGHT),
				new Token(" ", TokenType.BLANK),
				new Token("{", TokenType.BLOCK_LEFT),
				new Token(" ", TokenType.BLANK),
				new Token("add", TokenType.ADD),
				new Token("(", TokenType.BRACKET_LEFT),
				new Token("\"cn.milai.ib.character.plane.WelcomePlane\"", TokenType.STR),
				new Token(")", TokenType.BRACKET_RIGHT),
				new Token(";", TokenType.STMD_END),
				new Token(" ", TokenType.BLANK),
				new Token("}", TokenType.BLOCK_RIGHT),
			}, tokens.toArray()
		);
	}

}
