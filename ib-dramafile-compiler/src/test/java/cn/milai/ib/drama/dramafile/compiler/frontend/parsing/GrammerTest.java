package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import cn.milai.beginning.collection.Creator;
import cn.milai.beginning.collection.Mapping;

public class GrammerTest {

	private static final String FILE = "/parsing/testParseProductions.txt";

	private String[] codes = new String[] { "Expr", "CFG", "Factor", "Term", "Expr\'", "Term\'", "CFG\'" };
	private Grammer grammer = GrammerReader.parseGrammer(getClass().getResourceAsStream(FILE));

	@Test
	public void testSelectSet() {
		// Expr
		checkSelects(
			grammer.symbolOf(codes[0]), Arrays.asList(
				Creator.asSet("IDENTIFIER"),
				Creator.asSet("IDENTIFIER", "BRACKET_LEFT", "INT", "FLOAT")
			)
		);
		// CFG
		checkSelects(
			grammer.symbolOf(codes[1]), Arrays.asList(
				Creator.asSet("IDENTIFIER", "BRACKET_LEFT", "INT", "FLOAT"),
				Creator.asSet("IDENTIFIER"),
				Creator.asSet("IDENTIFIER", "BRACKET_LEFT", "INT", "FLOAT")
			)
		);
		// Factor
		checkSelects(
			grammer.symbolOf(codes[2]), Arrays.asList(
				Creator.asSet("IDENTIFIER"),
				Creator.asSet("BRACKET_LEFT"),
				Creator.asSet("INT"),
				Creator.asSet("FLOAT")
			)
		);
		// Term
		checkSelects(
			grammer.symbolOf(codes[3]), Arrays.asList(
				Creator.asSet("BRACKET_LEFT"),
				Creator.asSet("INT"),
				Creator.asSet("FLOAT"),
				Creator.asSet("IDENTIFIER")
			)
		);
		// Expr'
		checkSelects(
			grammer.symbolOf(codes[4]), Arrays.asList(
				Creator.asSet(Keywords.EPSILON, Keywords.EOF, "PLUS", "MINUS", "BRACKET_RIGHT"),
				Creator.asSet("PLUS"),
				Creator.asSet("MINUS")
			)
		);
		// Term'
		checkSelects(
			grammer.symbolOf(codes[5]), Arrays.asList(
				Creator.asSet(Keywords.EPSILON, Keywords.EOF, "PLUS", "MINUS", "BRACKET_RIGHT"),
				Creator.asSet("TIMES"),
				Creator.asSet("DIVISION")
			)
		);
		// CFG'
		checkSelects(
			grammer.symbolOf(codes[6]), Arrays.asList(
				Creator.asSet("PLUS"),
				Creator.asSet("MINUS")
			)
		);
	}

	private void checkSelects(Symbol s, List<Set<String>> selectsCodes) {
		NonTerminalSymbol left = (NonTerminalSymbol) s;
		List<Production> productions = left.getProductions();
		assertEquals(selectsCodes.size(), productions.size());
		assertEquals(new HashSet<>(selectsCodes), Mapping.set(productions, p -> codeSet(grammer.getSelect(p))));
	}

	@Test
	public void testFirstAndFollowSets() {
		/**
		 *                FIRST                                                                              FOLLOW
		 * Expr       IDENTIFIER,BRACKET_LEFT,INT,FLOAT                        $,PLUS,MINUS,BRACKET_RIGHT
		 * CFG        IDENTIFIER,BRACKET_LEFT,INT,FLOAT                        $
		 * Factor    IDENTIFIER,BRACKET_LEFT INT,FLOAT                        $,PLUS,MINUS,BRACKET_RIGHT,TIMES,DIVISION,
		 * Term      IDENTIFIER,BRACKET_LEFT,INT,FLOAT                        $,PLUS,MINUS,BRACKET_RIGHT
		 * Expr'      PLUS,MINUS,ϵ                                                                $,PLUS,MINUS,BRACKET_RIGHT
		 * Term'     TIMES,DIVISION,ϵ                                                          $,PLUS,MINUS,BRACKET_RIGHT
		 * CFG'       PLUS,MINUS,                                                                  $
		 */
		Set<String> first1 = new HashSet<>(Arrays.asList("IDENTIFIER", "BRACKET_LEFT", "INT", "FLOAT"));
		Set<String> first2 = new HashSet<>(Arrays.asList("PLUS", "MINUS", "ϵ"));
		Set<String> first3 = new HashSet<>(Arrays.asList("TIMES", "DIVISION", "ϵ"));
		Set<String> first4 = new HashSet<>(Arrays.asList("PLUS", "MINUS"));
		Set<String> follow1 = new HashSet<>(Arrays.asList("$", "PLUS", "MINUS", "BRACKET_RIGHT"));
		Set<String> follow2 = new HashSet<>(Arrays.asList("$"));
		Set<String> follow3 = new HashSet<>(Arrays.asList("$", "PLUS", "MINUS", "BRACKET_RIGHT", "TIMES", "DIVISION"));
		assertEquals(first1, codeSet(grammer.getFirst(codes[0])));
		assertEquals(first1, codeSet(grammer.getFirst(codes[1])));
		assertEquals(first1, codeSet(grammer.getFirst(codes[2])));
		assertEquals(first1, codeSet(grammer.getFirst(codes[3])));
		assertEquals(first2, codeSet(grammer.getFirst(codes[4])));
		assertEquals(first3, codeSet(grammer.getFirst(codes[5])));
		assertEquals(first4, codeSet(grammer.getFirst(codes[6])));
		assertEquals(follow1, codeSet(grammer.getFollow(codes[0])));
		assertEquals(follow2, codeSet(grammer.getFollow(codes[1])));
		assertEquals(follow3, codeSet(grammer.getFollow(codes[2])));
		assertEquals(follow1, codeSet(grammer.getFollow(codes[3])));
		assertEquals(follow1, codeSet(grammer.getFollow(codes[4])));
		assertEquals(follow1, codeSet(grammer.getFollow(codes[5])));
		assertEquals(follow2, codeSet(grammer.getFollow(codes[6])));
	}

	private static Set<String> codeSet(Collection<? extends Symbol> symbols) {
		return Mapping.set(symbols, Symbol::getCode);
	}

	private static List<String> codeList(Collection<? extends Symbol> symbols) {
		return Mapping.list(symbols, Symbol::getCode);
	}

	@Test
	public void testParseProductions() {
		assertEquals(codes.length, grammer.getNonTerminals().size());
		// Expr
		checkNonTerminal(
			grammer.symbolOf(codes[0]), Arrays.asList(
				Arrays.asList("IDENTIFIER", "ASSIGN", "Expr", "Expr\'"),
				Arrays.asList("Term", "Expr\'")
			)
		);
		// CFG
		checkNonTerminal(
			grammer.symbolOf(codes[1]), Arrays.asList(
				Arrays.asList("Term"),
				Arrays.asList("IDENTIFIER", "ASSIGN", "Expr"),
				Arrays.asList("Expr", "CFG\'")
			)
		);
		// Factor
		checkNonTerminal(
			grammer.symbolOf(codes[2]), Arrays.asList(
				Arrays.asList("IDENTIFIER"),
				Arrays.asList("BRACKET_LEFT", "Expr", "BRACKET_RIGHT"),
				Arrays.asList("INT"),
				Arrays.asList("FLOAT")
			)
		);
		// Term
		checkNonTerminal(
			grammer.symbolOf(codes[3]), Arrays.asList(
				Arrays.asList("BRACKET_LEFT", "Expr", "BRACKET_RIGHT", "Term\'"),
				Arrays.asList("INT", "Term\'"),
				Arrays.asList("FLOAT", "Term\'"),
				Arrays.asList("IDENTIFIER", "Term\'")
			)
		);
		// Expr'
		checkNonTerminal(
			grammer.symbolOf(codes[4]), Arrays.asList(
				Arrays.asList(Keywords.EPSILON),
				Arrays.asList("PLUS", "Term", "Expr\'"),
				Arrays.asList("MINUS", "Term", "Expr\'")
			)
		);
		// Term'
		checkNonTerminal(
			grammer.symbolOf(codes[5]), Arrays.asList(
				Arrays.asList(Keywords.EPSILON),
				Arrays.asList("TIMES", "Factor", "Term\'"),
				Arrays.asList("DIVISION", "Factor", "Term\'")
			)
		);
		// CFG'
		checkNonTerminal(
			grammer.symbolOf(codes[6]), Arrays.asList(
				Arrays.asList("PLUS", "Term"),
				Arrays.asList("MINUS", "Term")
			)
		);
	}

	/**
	 * 检查非终结符 s 是否符合期望
	 * @param s
	 * @param productionsRights 期望的 s 产生式右式 code 列表
	 */
	private void checkNonTerminal(Symbol s, List<List<String>> productionsRights) {
		assertTrue(s.isNonTerminal());
		List<Production> productions = ((NonTerminalSymbol) s).getProductions();
		assertEquals(productionsRights.size(), productions.size());
		assertEquals(new HashSet<>(productionsRights), Mapping.set(productions, p -> codeList(p.getRights())));
	}

}
