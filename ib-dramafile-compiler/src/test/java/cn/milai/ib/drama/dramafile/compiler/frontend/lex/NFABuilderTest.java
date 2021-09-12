package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

/**
 * 测试 NFA 构造算法
 * @author milai
 * @date 2020.02.07
 */
public class NFABuilderTest {

	private static final String TEST_TOKEN_CODE1 = "TEST_TOKEN_CODE1";
	private static final String TEST_TOKEN_CODE2 = "TEST_TOKEN_CODE2";

	@Test
	public void testLineNFA() {
		Node s = NFABuilder.newNFA(new HashSet<>(Arrays.asList(new TokenDefinition("abcde", TEST_TOKEN_CODE1))));
		s = addEmptyHead(s);
		// nfa = head--ϵ-->s0--a-->s1--ϵ-->s2--b-->s3--ϵ-->s4--c-->s5--ϵ-->s6--d-->s7--ϵ-->s8--e-->s9
		for (int i = 0; i < 5; i++) {
			assertFalse(s.isAccept());
			assertEquals(1, s.epsilonNexts().size());
			s = (Node) s.epsilonNexts().toArray()[0];

			assertFalse(s.isAccept());
			char ch = (char) ('a' + i);
			s = s.next(ch);
			assertNotNull(s);
		}
		assertEquals(TEST_TOKEN_CODE1, s.token());
	}

	@Test
	public void testSimpleComposeNFA() {
		Node s = NFABuilder.newNFA(new HashSet<>(Arrays.asList(new TokenDefinition("a(b)c(de)", TEST_TOKEN_CODE1))));
		s = addEmptyHead(s);
		// nfa = head--ϵ-->s0--a-->s1--ϵ-->s2--b-->s3--ϵ-->s4--c-->s5--ϵ-->s6--d-->s7--ϵ-->s8--e-->s9
		for (int i = 0; i < 5; i++) {
			assertFalse(s.isAccept());
			assertEquals(1, s.epsilonNexts().size());
			s = (Node) s.epsilonNexts().toArray()[0];

			assertFalse(s.isAccept());
			char ch = (char) ('a' + i);
			s = s.next(ch);
			assertNotNull(s);
		}
		assertEquals(TEST_TOKEN_CODE1, s.token());
	}

	/**
	 * 给 s 前方添加空串头，方便遍历
	 * @param s
	 * @return
	 */
	private Node addEmptyHead(Node s) {
		NFANode tmp = new NFANode();
		tmp.addEpsilonNext(s);
		return tmp;
	}

	@Test
	public void testRepeatNFA() {
		//                                              ↓----ϵ---+                                         ↓----ϵ----+
		// s0--a-->s1--ϵ-->s2--ϵ-->s3--b-->s4--ϵ-->s5--ϵ-->s6--ϵ-->s7--c-->s8--ϵ-->s9
		//                               +-----------ϵ--------------↑
		Node s0 = NFABuilder.newNFA(new HashSet<>(Arrays.asList(new TokenDefinition("ab*c+", TEST_TOKEN_CODE1))));
		assertFalse(s0.isAccept());
		Node s1 = s0.next('a');
		assertNotNull(s1);
		assertFalse(s1.isAccept());
		assertEquals(1, s1.epsilonNexts().size());
		Node s2 = (Node) s1.epsilonNexts().toArray()[0];
		assertFalse(s2.isAccept());
		Node[] s2Nexts = s2.epsilonNexts().toArray(new Node[0]);
		assertEquals(2, s2Nexts.length);
		Node s3 = null;
		if (s2Nexts[0].accepts().isEmpty()) {
			s3 = s2Nexts[1];
		} else {
			s3 = s2Nexts[0];
		}
		assertFalse(s3.isAccept());
		Node s4 = s3.next('b');
		assertNotNull(s4);
		assertFalse(s4.isAccept());
		Node[] s4Nexts = s4.epsilonNexts().toArray(new Node[0]);
		assertEquals(2, s4Nexts.length);
		Node s5 = null;
		if (s4Nexts[0] == s3) {
			s5 = s4Nexts[1];
		} else {
			assertSame(s3, s4Nexts[1]);
			s5 = s4Nexts[0];
		}
		assertFalse(s5.isAccept());
		assertSame(s5, s2Nexts[0] == s3 ? s2Nexts[1] : s2Nexts[0]);
		assertEquals(1, s5.epsilonNexts().size());
		Node s6 = (Node) s5.epsilonNexts().toArray()[0];
		assertFalse(s6.isAccept());
		assertEquals(1, s6.epsilonNexts().size());
		Node s7 = (Node) s6.epsilonNexts().toArray()[0];
		assertFalse(s7.isAccept());
		Node s8 = s7.next('c');
		assertNotNull(s8);
		assertFalse(s8.isAccept());
		Node[] s8Nexts = s8.epsilonNexts().toArray(new Node[0]);
		assertEquals(2, s8Nexts.length);
		Node s9 = null;
		if (s8Nexts[0] == s7) {
			s9 = s8Nexts[1];
		} else {
			assertSame(s7, s8Nexts[1]);
			s9 = s8Nexts[0];
		}
		assertEquals(TEST_TOKEN_CODE1, s9.token());
	}

	@Test
	public void testInputSet() {
		//                                                                                     ↓------ϵ-----+
		// s0--a-->s1--ϵ-->s2--[xy0-9]-->s3--ϵ-->s4--ϵ-->s5--[a-z]-->s6--ϵ-->s7
		//                                                                      +---------------ϵ--------------↑
		Node s0 = NFABuilder.newNFA(
			new HashSet<>(Arrays.asList(new TokenDefinition("a[xy\\d][a-z]*", TEST_TOKEN_CODE1)))
		);
		assertFalse(s0.isAccept());
		Node s1 = s0.next('a');
		assertNotNull(s1);
		assertFalse(s1.isAccept());
		assertEquals(1, s1.epsilonNexts().size());
		Node s2 = (Node) s1.epsilonNexts().toArray()[0];
		assertFalse(s2.isAccept());
		Node s3 = s2.next('x');
		assertSame(s3, s2.next('y'));
		assertSame(s3, s2.next('0'));
		assertSame(s3, s2.next('4'));
		assertSame(s3, s2.next('9'));
		assertNull(s2.next('a'));
		assertFalse(s3.isAccept());
		assertEquals(1, s3.epsilonNexts().size());
		Node s4 = (Node) s3.epsilonNexts().toArray()[0];
		assertFalse(s4.isAccept());
		Node[] s4Nexts = s4.epsilonNexts().toArray(new Node[0]);
		assertEquals(2, s4Nexts.length);
		Node s5 = null;
		if (s4Nexts[0].accepts().isEmpty()) {
			s5 = s4Nexts[1];
		} else {
			s5 = s4Nexts[0];
		}
		assertFalse(s5.isAccept());
		Node s6 = s5.next('c');
		assertSame(s6, s5.next('a'));
		assertSame(s6, s5.next('d'));
		assertSame(s6, s5.next('z'));
		assertFalse(s6.isAccept());
		Node[] s6Nexts = s6.epsilonNexts().toArray(new Node[0]);
		assertEquals(2, s6Nexts.length);
		Node s7 = null;
		if (s6Nexts[0] == s5) {
			s7 = s6Nexts[1];
		} else {
			assertSame(s6Nexts[1], s5);
			s7 = s6Nexts[0];
		}
		assertSame(s4Nexts[0] == s5 ? s4Nexts[1] : s4Nexts[0], s7);
		assertEquals(TEST_TOKEN_CODE1, s7.token());
	}

	@Test
	public void testComposeRepeatNFA() {
		//                                              
		//                                                                                  +--->s6---e---->s7----+
		//                                                             +---s8--ϵ---+--->s4---d---->s5----+--ϵ-->s9--- +
		// s0--a-->s1--ϵ-->s12--ϵ-->s10--ϵ--+---------------->s2--[bc]-->s3-------------------+--ϵ-->s11--ϵ-->s13
		//                                 +             ↑--------------------------------ϵ------------------------------------+              ↑
		//                                 +-----------------------------------------ϵ-----------------------------------------------+
		Node s0 = NFABuilder.newNFA(
			new HashSet<>(Arrays.asList(new TokenDefinition("a([bc]|d|e)*", TEST_TOKEN_CODE1)))
		);
		assertFalse(s0.isAccept());
		Node s1 = s0.next('a');
		assertNotNull(s1);
		assertFalse(s1.isAccept());
		assertEquals(1, s1.epsilonNexts().size());
		Node s12 = (Node) s1.epsilonNexts().toArray()[0];
		assertFalse(s12.isAccept());
		Node[] s12Nexts = s12.epsilonNexts().toArray(new Node[0]);
		assertEquals(2, s12Nexts.length);
		Node s10 = null;
		if (s12Nexts[0].epsilonNexts().isEmpty()) {
			s10 = s12Nexts[1];
		} else {
			s10 = s12Nexts[0];
		}
		assertFalse(s10.isAccept());
		Node[] s10Nexts = s10.epsilonNexts().toArray(new Node[0]);
		assertEquals(2, s10Nexts.length);
		Node s8 = null;
		Node s2 = null;
		if (s10Nexts[0].accepts().isEmpty()) {
			s8 = s10Nexts[0];
			s2 = s10Nexts[1];
		} else {
			s8 = s10Nexts[1];
			s2 = s10Nexts[0];
		}
		assertFalse(s8.isAccept());
		assertFalse(s2.isAccept());
		Node[] s8Nexts = s8.epsilonNexts().toArray(new Node[0]);
		assertEquals(2, s8Nexts.length);
		Node s4 = null;
		Node s6 = null;
		if (s8Nexts[0].accepts().contains('e')) {
			s6 = s8Nexts[0];
			s4 = s8Nexts[1];
		} else {
			s6 = s8Nexts[1];
			s4 = s8Nexts[0];
		}
		assertFalse(s4.isAccept());
		assertFalse(s6.isAccept());
		Node s7 = s6.next('e');
		assertNotNull(s7);
		assertFalse(s7.isAccept());
		assertEquals(1, s7.epsilonNexts().size());
		Node s5 = s4.next('d');
		assertNotNull(s5);
		assertEquals(1, s5.epsilonNexts().size());
		Node s9 = (Node) s7.epsilonNexts().toArray()[0];
		assertSame(s9, s5.epsilonNexts().toArray()[0]);
		assertFalse(s9.isAccept());
		assertEquals(1, s9.epsilonNexts().size());
		Node s3 = s2.next('b');
		assertNotNull(s3);
		assertFalse(s3.isAccept());
		assertSame(s3, s2.next('c'));
		assertEquals(1, s3.epsilonNexts().size());
		Node s11 = (Node) s3.epsilonNexts().toArray()[0];
		assertSame(s11, s9.epsilonNexts().toArray()[0]);
		assertFalse(s11.isAccept());
		Node[] s11Nexts = s11.epsilonNexts().toArray(new Node[0]);
		assertEquals(2, s11Nexts.length);
		Node s13 = null;
		if (s11Nexts[0] == s10) {
			s13 = s11Nexts[1];
		} else {
			assertSame(s11Nexts[1], s10);
			s13 = s11Nexts[0];
		}
		assertSame(s13, s12Nexts[0] == s10 ? s12Nexts[1] : s12Nexts[0]);
		assertEquals(TEST_TOKEN_CODE1, s13.token());
	}

	@Test
	public void testMultipleRe() {
		//             +-->s0---a---->s1
		// s4--ϵ--+-->s2--[cd]-->s3
		Node s4 = NFABuilder.newNFA(
			new HashSet<>(
				Arrays.asList(
					new TokenDefinition("a", TEST_TOKEN_CODE1),
					new TokenDefinition("[cd]", TEST_TOKEN_CODE2)
				)
			)
		);
		assertFalse(s4.isAccept());
		assertEquals(2, s4.epsilonNexts().size());
		Node[] s4Nexts = s4.epsilonNexts().toArray(new Node[0]);
		assertEquals(2, s4Nexts.length);
		Node s0 = null;
		Node s2 = null;
		if (s4Nexts[0].accepts().size() == 1) {
			s0 = s4Nexts[0];
			s2 = s4Nexts[1];
		} else {
			s0 = s4Nexts[1];
			s2 = s4Nexts[0];
		}
		assertFalse(s0.isAccept());
		Node s1 = s0.next('a');
		assertNotNull(s1);
		assertEquals(TEST_TOKEN_CODE1, s1.token());
		assertFalse(s2.isAccept());
		Node s3 = s2.next('c');
		assertNotNull(s3);
		assertSame(s3, s2.next('d'));
		assertEquals(TEST_TOKEN_CODE2, s3.token());
	}

	@Test
	//==================
	public void testNoneOrOne() {
		// s0--a-->s1--ϵ-->s2--ϵ-->s3--b--s4--ϵ-->s5--ϵ-->s6-->c-->s7
		//                               +------------------------↑
		Node s0 = NFABuilder.newNFA(new HashSet<>(Arrays.asList(new TokenDefinition("ab?c", TEST_TOKEN_CODE1))));
		assertFalse(s0.isAccept());
		Node s1 = s0.next('a');
		assertNotNull(s1);
		assertFalse(s1.isAccept());
		assertEquals(1, s1.epsilonNexts().size());
		Node s2 = (Node) s1.epsilonNexts().toArray()[0];
		assertFalse(s2.isAccept());
		Node[] s2Nexts = s2.epsilonNexts().toArray(new Node[0]);
		assertEquals(2, s2Nexts.length);
		Node s3 = null;
		if (s2Nexts[0].accepts().contains('b')) {
			s3 = s2Nexts[0];
		} else {
			s3 = s2Nexts[1];
		}
		assertFalse(s3.isAccept());
		Node s4 = s3.next('b');
		assertNotNull(s4);
		assertFalse(s4.isAccept());
		Node s5 = (Node) s4.epsilonNexts().toArray()[0];
		assertFalse(s5.isAccept());
		assertSame(s5, s2Nexts[0] == s3 ? s2Nexts[1] : s2Nexts[0]);
		Node s6 = (Node) s5.epsilonNexts().toArray()[0];
		assertFalse(s6.isAccept());
		Node s7 = s6.next('c');
		assertNotNull(s7);
		assertTrue(s7.isAccept());
		assertEquals(TEST_TOKEN_CODE1, s7.token());
	}

	@Test
	public void testInvertCRLF() {
		// s0--x-->s1--ϵ-->s2 -->(.)-->s3
		Node s0 = NFABuilder.newNFA(new HashSet<>(Arrays.asList(new TokenDefinition("x.", TEST_TOKEN_CODE1))));
		assertFalse(s0.isAccept());
		Node s1 = s0.next('x');
		assertNotNull(s1);
		assertFalse(s1.isAccept());
		Node s2 = (Node) s1.epsilonNexts().toArray()[0];
		assertFalse(s2.isAccept());
		Node s3 = s2.next('a');
		assertNotNull(s3);
		assertSame(s3, s2.next('x'));
		assertSame(s3, s2.next('2'));
		assertSame(s3, s2.next('0'));
		assertSame(s3, s2.next('='));
		assertSame(s3, s2.next('\\'));
		assertTrue(s3.isAccept());
		assertEquals(TEST_TOKEN_CODE1, s3.token());
	}

}
