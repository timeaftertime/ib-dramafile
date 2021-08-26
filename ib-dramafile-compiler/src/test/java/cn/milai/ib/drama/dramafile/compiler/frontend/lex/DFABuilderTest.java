package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

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
			NFABuilder.newNFA(new HashSet<>(Arrays.asList(new TokenDefinition("abcde", TEST_TOKEN_CODE1))))
		);
		for (int i = 0; i < 5; i++) {
			char ch = (char) ('a' + i);
			assertEquals(new HashSet<>(Arrays.asList(ch)), s.accepts());
			assertFalse(s.isAccept());
			s = s.next(ch);
		}
		assertEquals(new HashSet<>(Arrays.asList(TEST_TOKEN_CODE1)), s.tokens());
	}

	@Test
	public void testCombineDFA() {
		// s0--f-->s1--e-->s2--e-->s3
		//                +---i-->s4--e-->s5
		DFAStatus s0 = DFABuilder.newDFA(
			NFABuilder.newNFA(
				new HashSet<>(
					Arrays.asList(
						new TokenDefinition("fee", TEST_TOKEN_CODE1),
						new TokenDefinition("fie", TEST_TOKEN_CODE2)
					)
				)
			)
		);
		assertEquals(new HashSet<>(Arrays.asList('f')), s0.accepts());
		assertFalse(s0.isAccept());
		DFAStatus s1 = s0.next('f');
		assertEquals(new HashSet<>(Arrays.asList('i', 'e')), s1.accepts());
		assertFalse(s1.isAccept());
		DFAStatus s2 = s1.next('e');
		assertFalse(s2.isAccept());
		assertEquals(new HashSet<>(Arrays.asList('e')), s2.accepts());
		DFAStatus s3 = s2.next('e');
		assertEquals(new HashSet<>(Arrays.asList(TEST_TOKEN_CODE1)), s3.tokens());
		DFAStatus s4 = s1.next('i');
		assertFalse(s4.isAccept());
		assertEquals(new HashSet<>(Arrays.asList('e')), s4.accepts());
		DFAStatus s5 = s4.next('e');
		assertEquals(new HashSet<>(Arrays.asList(TEST_TOKEN_CODE2)), s5.tokens());
	}

	@Test
	public void testMinimizeDFA() {
		// s0--f-->s1--[ie]-->s2--e-->s3
		DFAStatus s0 = DFABuilder.newDFA(
			NFABuilder.newNFA(
				new HashSet<>(
					Arrays.asList(
						new TokenDefinition("fee", TEST_TOKEN_CODE1),
						new TokenDefinition("fie", TEST_TOKEN_CODE2)
					)
				)
			)
		);
		s0 = DFABuilder.minimize(s0);
		assertEquals(new HashSet<>(Arrays.asList('f')), s0.accepts());
		assertFalse(s0.isAccept());
		DFAStatus s1 = s0.next('f');
		assertEquals(new HashSet<>(Arrays.asList('i', 'e')), s1.accepts());
		assertFalse(s1.isAccept());
		DFAStatus s2 = s1.next('i');
		assertSame(s2, s1.next('e'));
		assertFalse(s2.isAccept());
		assertEquals(new HashSet<>(Arrays.asList('e')), s2.accepts());
		DFAStatus s3 = s2.next('e');
		assertEquals(new HashSet<>(Arrays.asList(TEST_TOKEN_CODE1, TEST_TOKEN_CODE2)), s3.tokens());
	}

}
