package cn.milai.ib.compiler.frontend.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Grammer;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.GrammerReader;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.NonTerminalSymbol;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Production;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Symbol;

public class GrammerTest {

	private static final String FILE = "/parsing/testParseProductions.txt";

	private String[] codes = new String[] { "Expr", "CFG", "Factor", "Term", "Expr\'", "Term\'", "CFG\'" };
	private Grammer grammer = GrammerReader.parseGrammer(getClass().getResourceAsStream(FILE));

	@Test
	public void testSelectSet() {
		// Expr
		checkSelects(grammer.findSymbol(codes[0]), Arrays.asList(
			Lists.newArrayList("IDENTIFIER"),
			Lists.newArrayList("IDENTIFIER", "BRACKET_LEFT", "INT", "FLOAT")));
		// CFG
		checkSelects(grammer.findSymbol(codes[1]), Arrays.asList(
			Lists.newArrayList("IDENTIFIER", "BRACKET_LEFT", "INT", "FLOAT"),
			Lists.newArrayList("IDENTIFIER"),
			Lists.newArrayList("IDENTIFIER", "BRACKET_LEFT", "INT", "FLOAT")));
		// Factor
		checkSelects(grammer.findSymbol(codes[2]), Arrays.asList(
			Lists.newArrayList("IDENTIFIER"),
			Lists.newArrayList("BRACKET_LEFT"),
			Lists.newArrayList("INT"),
			Lists.newArrayList("FLOAT")));
		// Term
		checkSelects(grammer.findSymbol(codes[3]), Arrays.asList(
			Lists.newArrayList("BRACKET_LEFT"),
			Lists.newArrayList("INT"),
			Lists.newArrayList("FLOAT"),
			Lists.newArrayList("IDENTIFIER")));
		// Expr'
		checkSelects(grammer.findSymbol(codes[4]), Arrays.asList(
			Lists.newArrayList("ϵ", "$", "PLUS", "MINUS", "BRACKET_RIGHT"),
			Lists.newArrayList("PLUS"),
			Lists.newArrayList("MINUS")));
		// Term'
		checkSelects(grammer.findSymbol(codes[5]), Arrays.asList(
			Lists.newArrayList("ϵ", "$", "PLUS", "MINUS", "BRACKET_RIGHT"),
			Lists.newArrayList("TIMES"),
			Lists.newArrayList("DIVISION")));
		// CFG'
		checkSelects(grammer.findSymbol(codes[6]), Arrays.asList(
			Lists.newArrayList("PLUS"),
			Lists.newArrayList("MINUS")));
	}

	private void checkSelects(Symbol s, List<List<String>> selectsCodes) {
		NonTerminalSymbol left = (NonTerminalSymbol) s;
		List<Production> productions = left.getProductions();
		assertEquals(selectsCodes.size(), productions.size());
		for (int i = 0; i < productions.size(); i++) {
			List<String> selectCodes = selectsCodes.get(i);
			Set<Symbol> select = grammer.getSelect(productions.get(i));
			assertEquals(selectCodes.size(), select.size());
			assertTrue(selectCodes.containsAll(extractCodes(select)));
		}
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
		Set<String> first1 = Sets.newHashSet("IDENTIFIER", "BRACKET_LEFT", "INT", "FLOAT");
		Set<String> first2 = Sets.newHashSet("PLUS", "MINUS", "ϵ");
		Set<String> first3 = Sets.newHashSet("TIMES", "DIVISION", "ϵ");
		Set<String> first4 = Sets.newHashSet("PLUS", "MINUS");
		Set<String> follow1 = Sets.newHashSet("$", "PLUS", "MINUS", "BRACKET_RIGHT");
		Set<String> follow2 = Sets.newHashSet("$");
		Set<String> follow3 = Sets.newHashSet("$", "PLUS", "MINUS", "BRACKET_RIGHT", "TIMES", "DIVISION");
		assertEquals(first1, extractCodes(grammer.getFirst(codes[0])));
		assertEquals(first1, extractCodes(grammer.getFirst(codes[1])));
		assertEquals(first1, extractCodes(grammer.getFirst(codes[2])));
		assertEquals(first1, extractCodes(grammer.getFirst(codes[3])));
		assertEquals(first2, extractCodes(grammer.getFirst(codes[4])));
		assertEquals(first3, extractCodes(grammer.getFirst(codes[5])));
		assertEquals(first4, extractCodes(grammer.getFirst(codes[6])));
		assertEquals(follow1, extractCodes(grammer.getFollow(codes[0])));
		assertEquals(follow2, extractCodes(grammer.getFollow(codes[1])));
		assertEquals(follow3, extractCodes(grammer.getFollow(codes[2])));
		assertEquals(follow1, extractCodes(grammer.getFollow(codes[3])));
		assertEquals(follow1, extractCodes(grammer.getFollow(codes[4])));
		assertEquals(follow1, extractCodes(grammer.getFollow(codes[5])));
		assertEquals(follow2, extractCodes(grammer.getFollow(codes[6])));
	}

	private static Set<String> extractCodes(Collection<? extends Symbol> symbols) {
		return symbols.stream().map(Symbol::getCode).collect(Collectors.toSet());
	}

	@Test
	public void testParseProductions() {
		assertEquals(codes.length, grammer.getNonTerminals().size());
		// Expr
		checkNonTerminal(grammer.findSymbol(codes[0]), Arrays.asList(
			Lists.newArrayList("IDENTIFIER", "ASSIGN", "Expr"),
			Lists.newArrayList("Term", "Expr\'")));
		// CFG
		checkNonTerminal(grammer.findSymbol(codes[1]), Arrays.asList(
			Lists.newArrayList("Term"),
			Lists.newArrayList("IDENTIFIER", "ASSIGN", "Expr"),
			Lists.newArrayList("Expr", "CFG\'")));
		// Factor
		checkNonTerminal(grammer.findSymbol(codes[2]), Arrays.asList(
			Lists.newArrayList("IDENTIFIER"),
			Lists.newArrayList("BRACKET_LEFT", "Expr", "BRACKET_RIGHT"),
			Lists.newArrayList("INT"),
			Lists.newArrayList("FLOAT")));
		// Term
		checkNonTerminal(grammer.findSymbol(codes[3]), Arrays.asList(
			Lists.newArrayList("BRACKET_LEFT", "Expr", "BRACKET_RIGHT"),
			Lists.newArrayList("INT"),
			Lists.newArrayList("FLOAT"),
			Lists.newArrayList("IDENTIFIER", "Term\'")));
		// Expr'
		checkNonTerminal(grammer.findSymbol(codes[4]), Arrays.asList(
			Lists.newArrayList(),
			Lists.newArrayList("PLUS", "Term", "Expr\'"),
			Lists.newArrayList("MINUS", "Term", "Expr\'")));
		// Term'
		checkNonTerminal(grammer.findSymbol(codes[5]), Arrays.asList(
			Lists.newArrayList(),
			Lists.newArrayList("TIMES", "Factor", "Term\'"),
			Lists.newArrayList("DIVISION", "Factor", "Term\'")));
		// CFG'
		checkNonTerminal(grammer.findSymbol(codes[6]), Arrays.asList(
			Lists.newArrayList("PLUS", "Term"),
			Lists.newArrayList("MINUS", "Term")));
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
		for (int i = 0; i < productionsRights.size(); i++) {
			List<String> rightCodes = productionsRights.get(i);
			List<Symbol> rights = productions.get(i).getRights();
			if (rightCodes.isEmpty()) {
				assertEquals(Lists.newArrayList(Symbol.EPSILON), rights);
				continue;
			}
			assertEquals(rightCodes.size(), rights.size());
			for (int j = 0; j < rightCodes.size(); j++) {
				String rightCode = rightCodes.get(j);
				assertSame(grammer.findSymbol(rightCode), productions.get(i).getRights().get(j));
			}
		}
	}

}
