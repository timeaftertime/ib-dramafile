package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.Sets;

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
		NFAStatus s = NFABuilder.newNFA(Sets.newHashSet(new TokenDef("abcde", TEST_TOKEN_CODE1)));
		s = addEmptyHead(s);
		// nfa = head--ϵ-->s0--a-->s1--ϵ-->s2--b-->s3--ϵ-->s4--c-->s5--ϵ-->s6--d-->s7--ϵ-->s8--e-->s9
		for (int i = 0; i < 5; i++) {
			assertFalse(s.isAccept());
			assertEquals(1, s.getEpsilonNexts().size());
			s = s.getEpsilonNexts().get(0);

			assertFalse(s.isAccept());
			char ch = (char) ('a' + i);
			s = s.nextOf(ch);
			assertNotNull(s);
		}
		assertEquals(TEST_TOKEN_CODE1, s.token());
	}

	@Test
	public void testSimpleComposeNFA() {
		NFAStatus s = NFABuilder.newNFA(Sets.newHashSet(new TokenDef("a(b)c(de)", TEST_TOKEN_CODE1)));
		s = addEmptyHead(s);
		// nfa = head--ϵ-->s0--a-->s1--ϵ-->s2--b-->s3--ϵ-->s4--c-->s5--ϵ-->s6--d-->s7--ϵ-->s8--e-->s9
		for (int i = 0; i < 5; i++) {
			assertFalse(s.isAccept());
			assertEquals(1, s.getEpsilonNexts().size());
			s = s.getEpsilonNexts().get(0);

			assertFalse(s.isAccept());
			char ch = (char) ('a' + i);
			s = s.nextOf(ch);
			assertNotNull(s);
		}
		assertEquals(TEST_TOKEN_CODE1, s.token());
	}

	/**
	 * 给 s 前方添加空串头，方便遍历
	 * @param nfa
	 * @return
	 */
	private NFAStatus addEmptyHead(NFAStatus s) {
		NFAStatus tmp = new NFAStatus();
		tmp.addEpsilonNext(s);
		return tmp;
	}

	@Test
	public void testRepeatNFA() {
		//                                              ↓----ϵ---+                                         ↓----ϵ----+
		// s0--a-->s1--ϵ-->s2--ϵ-->s3--b-->s4--ϵ-->s5--ϵ-->s6--ϵ-->s7--c-->s8--ϵ-->s9
		//                               +-----------ϵ--------------↑
		NFAStatus s0 = NFABuilder.newNFA(Sets.newHashSet(new TokenDef("ab*c+", TEST_TOKEN_CODE1)));
		assertFalse(s0.isAccept());
		NFAStatus s1 = s0.nextOf('a');
		assertNotNull(s1);
		assertFalse(s1.isAccept());
		assertEquals(1, s1.getEpsilonNexts().size());
		NFAStatus s2 = s1.getEpsilonNexts().get(0);
		assertFalse(s2.isAccept());
		NFAStatus s3 = s2.getEpsilonNexts().get(0);
		assertFalse(s3.isAccept());
		NFAStatus s4 = s3.nextOf('b');
		assertNotNull(s4);
		assertFalse(s4.isAccept());
		assertSame(s3, s2.getEpsilonNexts().get(0));
		assertSame(s3, s4.getEpsilonNexts().get(0));
		NFAStatus s5 = s4.getEpsilonNexts().get(1);
		assertFalse(s5.isAccept());
		assertSame(s5, s2.getEpsilonNexts().get(1));
		assertEquals(1, s5.getEpsilonNexts().size());
		NFAStatus s6 = s5.getEpsilonNexts().get(0);
		assertFalse(s6.isAccept());
		assertEquals(1, s6.getEpsilonNexts().size());
		NFAStatus s7 = s6.getEpsilonNexts().get(0);
		assertFalse(s7.isAccept());
		NFAStatus s8 = s7.nextOf('c');
		assertNotNull(s8);
		assertFalse(s8.isAccept());
		assertEquals(2, s8.getEpsilonNexts().size());
		assertSame(s7, s8.getEpsilonNexts().get(0));
		NFAStatus s9 = s8.getEpsilonNexts().get(1);
		assertEquals(TEST_TOKEN_CODE1, s9.token());
	}

	@Test
	public void testInputSet() {
		//                                                                                     ↓------ϵ-----+
		// s0--a-->s1--ϵ-->s2--[xy0-9]-->s3--ϵ-->s4--ϵ-->s5--[a-z]-->s6--ϵ-->s7
		//                                                                      +---------------ϵ--------------↑
		NFAStatus s0 = NFABuilder.newNFA(Sets.newHashSet(new TokenDef("a[xy\\d][a-z]*", TEST_TOKEN_CODE1)));
		assertFalse(s0.isAccept());
		NFAStatus s1 = s0.nextOf('a');
		assertNotNull(s1);
		assertFalse(s1.isAccept());
		assertEquals(1, s1.getEpsilonNexts().size());
		NFAStatus s2 = s1.getEpsilonNexts().get(0);
		assertFalse(s2.isAccept());
		NFAStatus s3 = s2.nextOf('x');
		assertSame(s3, s2.nextOf('y'));
		assertSame(s3, s2.nextOf('0'));
		assertSame(s3, s2.nextOf('4'));
		assertSame(s3, s2.nextOf('9'));
		assertNull(s2.nextOf('a'));
		assertFalse(s3.isAccept());
		assertEquals(1, s3.getEpsilonNexts().size());
		NFAStatus s4 = s3.getEpsilonNexts().get(0);
		assertFalse(s4.isAccept());
		NFAStatus s5 = s4.getEpsilonNexts().get(0);
		assertFalse(s5.isAccept());
		NFAStatus s6 = s5.nextOf('c');
		assertSame(s6, s5.nextOf('a'));
		assertSame(s6, s5.nextOf('d'));
		assertSame(s6, s5.nextOf('z'));
		assertFalse(s6.isAccept());
		assertSame(s5, s6.getEpsilonNexts().get(0));
		NFAStatus s7 = s6.getEpsilonNexts().get(1);
		assertSame(s7, s4.getEpsilonNexts().get(1));
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
		NFAStatus s0 = NFABuilder.newNFA(Sets.newHashSet(new TokenDef("a([bc]|d|e)*", TEST_TOKEN_CODE1)));
		assertFalse(s0.isAccept());
		NFAStatus s1 = s0.nextOf('a');
		assertNotNull(s1);
		assertFalse(s1.isAccept());
		assertEquals(1, s1.getEpsilonNexts().size());
		NFAStatus s12 = s1.getEpsilonNexts().get(0);
		assertFalse(s12.isAccept());
		NFAStatus s10 = s12.getEpsilonNexts().get(0);
		assertFalse(s10.isAccept());
		assertEquals(2, s10.getEpsilonNexts().size());
		NFAStatus s8 = s10.getEpsilonNexts().get(0);
		assertFalse(s8.isAccept());
		NFAStatus s6 = s8.getEpsilonNexts().get(0);
		assertFalse(s6.isAccept());
		NFAStatus s7 = s6.nextOf('e');
		assertNotNull(s7);
		assertFalse(s7.isAccept());
		assertEquals(1, s7.getEpsilonNexts().size());
		NFAStatus s4 = s8.getEpsilonNexts().get(1);
		assertFalse(s4.isAccept());
		NFAStatus s5 = s4.nextOf('d');
		assertNotNull(s5);
		assertEquals(1, s5.getEpsilonNexts().size());
		NFAStatus s9 = s7.getEpsilonNexts().get(0);
		assertSame(s9, s5.getEpsilonNexts().get(0));
		assertFalse(s9.isAccept());
		assertEquals(1, s9.getEpsilonNexts().size());
		NFAStatus s2 = s10.getEpsilonNexts().get(1);
		assertFalse(s2.isAccept());
		NFAStatus s3 = s2.nextOf('b');
		assertNotNull(s3);
		assertFalse(s3.isAccept());
		assertSame(s3, s2.nextOf('c'));
		assertEquals(1, s3.getEpsilonNexts().size());
		NFAStatus s11 = s3.getEpsilonNexts().get(0);
		assertSame(s11, s9.getEpsilonNexts().get(0));
		assertFalse(s11.isAccept());
		NFAStatus s13 = s11.getEpsilonNexts().get(1);
		assertSame(s10, s11.getEpsilonNexts().get(0));
		assertSame(s13, s12.getEpsilonNexts().get(1));
		assertEquals(TEST_TOKEN_CODE1, s13.token());
	}

	@Test
	public void testMultipleRe() {
		//             +-->s0---a---->s1
		// s4--ϵ--+-->s2--[cd]-->s3
		NFAStatus s4 = NFABuilder.newNFA(
			Sets.newHashSet(
				new TokenDef("a", TEST_TOKEN_CODE1),
				new TokenDef("[cd]", TEST_TOKEN_CODE2)
			)
		);
		assertFalse(s4.isAccept());
		assertEquals(2, s4.getEpsilonNexts().size());
		NFAStatus s0 = s4.getEpsilonNexts().get(0);
		assertFalse(s0.isAccept());
		NFAStatus s1 = s0.nextOf('a');
		assertNotNull(s1);
		assertEquals(TEST_TOKEN_CODE1, s1.token());
		NFAStatus s2 = s4.getEpsilonNexts().get(1);
		assertFalse(s2.isAccept());
		NFAStatus s3 = s2.nextOf('c');
		assertNotNull(s3);
		assertSame(s3, s2.nextOf('d'));
		assertEquals(TEST_TOKEN_CODE2, s3.token());
	}

	@Test
	public void testNoneOrOne() {
		// s0--a-->s1--ϵ-->s2--ϵ-->s3--b--s4--ϵ-->s5--ϵ-->s6-->c-->s7
		//                               +------------------------↑
		NFAStatus s0 = NFABuilder.newNFA(Sets.newHashSet(new TokenDef("ab?c", TEST_TOKEN_CODE1)));
		assertFalse(s0.isAccept());
		NFAStatus s1 = s0.nextOf('a');
		assertNotNull(s1);
		assertFalse(s1.isAccept());
		assertEquals(1, s1.getEpsilonNexts().size());
		NFAStatus s2 = s1.getEpsilonNexts().get(0);
		assertFalse(s2.isAccept());
		assertEquals(2, s2.getEpsilonNexts().size());
		NFAStatus s3 = s2.getEpsilonNexts().get(0);
		assertFalse(s3.isAccept());
		NFAStatus s4 = s3.nextOf('b');
		assertNotNull(s4);
		assertFalse(s4.isAccept());
		NFAStatus s5 = s4.getEpsilonNexts().get(0);
		assertFalse(s5.isAccept());
		assertSame(s5, s2.getEpsilonNexts().get(1));
		NFAStatus s6 = s5.getEpsilonNexts().get(0);
		assertFalse(s6.isAccept());
		NFAStatus s7 = s6.nextOf('c');
		assertNotNull(s7);
		assertTrue(s7.isAccept());
		assertEquals(TEST_TOKEN_CODE1, s7.token());
	}

	@Test
	public void testInvertCRLF() {
		// s0--x-->s1--ϵ-->s2 -->(.)-->s3
		NFAStatus s0 = NFABuilder.newNFA(Sets.newHashSet(new TokenDef("x.", TEST_TOKEN_CODE1)));
		assertFalse(s0.isAccept());
		NFAStatus s1 = s0.nextOf('x');
		assertNotNull(s1);
		assertFalse(s1.isAccept());
		NFAStatus s2 = s1.getEpsilonNexts().get(0);
		assertFalse(s2.isAccept());
		NFAStatus s3 = s2.nextOf('a');
		assertNotNull(s3);
		assertSame(s3, s2.nextOf('x'));
		assertSame(s3, s2.nextOf('2'));
		assertSame(s3, s2.nextOf('0'));
		assertSame(s3, s2.nextOf('='));
		assertSame(s3, s2.nextOf('\\'));
		assertTrue(s3.isAccept());
		assertEquals(TEST_TOKEN_CODE1, s3.token());
	}

}
