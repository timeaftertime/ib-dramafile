package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
		Node s = DFABuilder.newDFA(
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
		Node s0 = DFABuilder.newDFA(
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
		Node s1 = s0.next('f');
		assertEquals(new HashSet<>(Arrays.asList('i', 'e')), s1.accepts());
		assertFalse(s1.isAccept());
		Node s2 = s1.next('e');
		assertFalse(s2.isAccept());
		assertEquals(new HashSet<>(Arrays.asList('e')), s2.accepts());
		Node s3 = s2.next('e');
		assertEquals(new HashSet<>(Arrays.asList(TEST_TOKEN_CODE1)), s3.tokens());
		Node s4 = s1.next('i');
		assertFalse(s4.isAccept());
		assertEquals(new HashSet<>(Arrays.asList('e')), s4.accepts());
		Node s5 = s4.next('e');
		assertEquals(new HashSet<>(Arrays.asList(TEST_TOKEN_CODE2)), s5.tokens());
	}

}
