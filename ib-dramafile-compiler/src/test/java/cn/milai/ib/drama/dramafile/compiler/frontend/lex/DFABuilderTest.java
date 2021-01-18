package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * 测试 DFA 构造算法
 * @author milai
 * @date 2020.02.10
 */
public class DFABuilderTest {

	private static final String TEST_TOKEN_CODE1 = "TEST_TOKEN_CODE1";
	private static final String TEST_TOKEN_CODE2 = "TEST_TOKEN_CODE2";

	@Test
	public void testLineDFA() {
		// s0--a-->s1--b-->2--c-->s3--d-->s4--e-->s5
		DFAStatus s = DFABuilder.newDFA(
			NFABuilder.newNFA(Sets.newHashSet(new TokenDef("abcde", TEST_TOKEN_CODE1)))
		);
		for (int i = 0; i < 5; i++) {
			char ch = (char) ('a' + i);
			assertEquals(Sets.newHashSet(ch), s.accepts());
			assertFalse(s.isAccept());
			s = s.next(ch);
		}
		assertEquals(Sets.newHashSet(TEST_TOKEN_CODE1), s.tokens());
	}

	@Test
	public void testCombineDFA() {
		// s0--f-->s1--e-->s2--e-->s3
		//                +---i-->s4--e-->s5
		DFAStatus s0 = DFABuilder.newDFA(
			NFABuilder.newNFA(
				Sets.newHashSet(
					new TokenDef("fee", TEST_TOKEN_CODE1),
					new TokenDef("fie", TEST_TOKEN_CODE2)
				)
			)
		);
		assertEquals(Sets.newHashSet('f'), s0.accepts());
		assertFalse(s0.isAccept());
		DFAStatus s1 = s0.next('f');
		assertEquals(Sets.newHashSet('i', 'e'), s1.accepts());
		assertFalse(s1.isAccept());
		DFAStatus s2 = s1.next('e');
		assertFalse(s2.isAccept());
		assertEquals(Sets.newHashSet('e'), s2.accepts());
		DFAStatus s3 = s2.next('e');
		assertEquals(Sets.newHashSet(TEST_TOKEN_CODE1), s3.tokens());
		DFAStatus s4 = s1.next('i');
		assertFalse(s4.isAccept());
		assertEquals(Sets.newHashSet('e'), s4.accepts());
		DFAStatus s5 = s4.next('e');
		assertEquals(Sets.newHashSet(TEST_TOKEN_CODE2), s5.tokens());
	}

	@Test
	public void testMinimizeDFA() {
		// s0--f-->s1--[ie]-->s2--e-->s3
		DFAStatus s0 = DFABuilder.newDFA(
			NFABuilder.newNFA(
				Sets.newHashSet(
					new TokenDef("fee", TEST_TOKEN_CODE1),
					new TokenDef("fie", TEST_TOKEN_CODE2)
				)
			)
		);
		s0 = DFABuilder.minimize(s0);
		assertEquals(Sets.newHashSet('f'), s0.accepts());
		assertFalse(s0.isAccept());
		DFAStatus s1 = s0.next('f');
		assertEquals(Sets.newHashSet('i', 'e'), s1.accepts());
		assertFalse(s1.isAccept());
		DFAStatus s2 = s1.next('i');
		assertSame(s2, s1.next('e'));
		assertFalse(s2.isAccept());
		assertEquals(Sets.newHashSet('e'), s2.accepts());
		DFAStatus s3 = s2.next('e');
		assertEquals(Sets.newHashSet(TEST_TOKEN_CODE1, TEST_TOKEN_CODE2), s3.tokens());
	}

}
