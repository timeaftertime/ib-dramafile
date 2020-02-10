package cn.milai.ib.compiler.frontend.lex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Sets;

import cn.milai.ib.compiler.frontend.LexToken;

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
		NFAStatus s = NFABuilder.newNFA(Arrays.asList(new LexToken("abcde", TEST_TOKEN_CODE1)));
		s = addEmptyHead(s);
		// nfa = head--ϵ-->s0--a-->s1--ϵ-->s2--b-->s3--ϵ-->s4--c-->s5--ϵ-->s6--d-->s7--ϵ-->s8--e-->s9
		for (int i = 0; i < 5; i++) {
			assertFalse(s.isAccept());
			assertTrue(s.getEdges().get(0).isEpsilon());
			s = s.getEdges().get(0).getTargetStatus();

			assertFalse(s.isAccept());
			assertEquals(Sets.newHashSet((char) ('a' + i)), s.getEdges().get(0).getAccepts());
			s = s.getEdges().get(0).getTargetStatus();
		}
		assertEquals(TEST_TOKEN_CODE1, s.token());
	}

	@Test
	public void testSimpleComposeNFA() {
		NFAStatus s = NFABuilder.newNFA(Arrays.asList(new LexToken("a(b)c(de)", TEST_TOKEN_CODE1)));
		s = addEmptyHead(s);
		// nfa = head--ϵ-->s0--a-->s1--ϵ-->s2--b-->s3--ϵ-->s4--c-->s5--ϵ-->s6--d-->s7--ϵ-->s8--e-->s9
		for (int i = 0; i < 5; i++) {
			assertFalse(s.isAccept());
			assertTrue(s.getEdges().get(0).isEpsilon());
			s = s.getEdges().get(0).getTargetStatus();

			assertFalse(s.isAccept());
			assertEquals(Sets.newHashSet((char) ('a' + i)), s.getEdges().get(0).getAccepts());
			s = s.getEdges().get(0).getTargetStatus();
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
		tmp.addEdge(s);
		return tmp;
	}

	@Test
	public void testRepeatNFA() {
		//                                              ↓----ϵ---+                                         ↓----ϵ----+
		// s0--a-->s1--ϵ-->s2--ϵ-->s3--b-->s4--ϵ-->s5--ϵ-->s6--ϵ-->s7--c-->s8--ϵ-->s9
		//                               +-----------ϵ--------------↑
		NFAStatus s0 = NFABuilder.newNFA(Arrays.asList(new LexToken("ab*c+", TEST_TOKEN_CODE1)));
		assertFalse(s0.isAccept());
		assertEquals(Sets.newHashSet('a'), s0.getEdges().get(0).getAccepts());
		NFAStatus s1 = s0.getEdges().get(0).getTargetStatus();
		assertFalse(s1.isAccept());
		assertTrue(s1.getEdges().get(0).isEpsilon());
		NFAStatus s2 = s1.getEdges().get(0).getTargetStatus();
		assertFalse(s2.isAccept());
		List<Edge> e2 = s2.getEdges();
		assertEquals(2, e2.size());
		// 以下两个变量的 0 / 1 取决于构造 NFA 时添加边的顺序
		Edge e2S3 = e2.get(0);
		Edge e2S5 = e2.get(1);
		NFAStatus s3 = e2S3.getTargetStatus();
		assertFalse(s3.isAccept());
		assertEquals(Sets.newHashSet('b'), s3.getEdges().get(0).getAccepts());
		NFAStatus s4 = s3.getEdges().get(0).getTargetStatus();
		assertFalse(s4.isAccept());
		List<Edge> e4 = s4.getEdges();
		Edge e4S3 = e4.get(0).getTargetStatus() == s3 ? e4.get(0) : e4.get(1);
		Edge e4S5 = e4.get(0).getTargetStatus() == s3 ? e4.get(1) : e4.get(0);
		NFAStatus s5 = e4S5.getTargetStatus();
		assertFalse(s5.isAccept());
		assertSame(s5, e2S5.getTargetStatus());
		assertSame(s3, e4S3.getTargetStatus());
		assertTrue(s5.getEdges().get(0).isEpsilon());
		NFAStatus s6 = s5.getEdges().get(0).getTargetStatus();
		assertFalse(s6.isAccept());
		assertTrue(s6.getEdges().get(0).isEpsilon());
		NFAStatus s7 = s6.getEdges().get(0).getTargetStatus();
		assertFalse(s7.isAccept());
		assertEquals(Sets.newHashSet('c'), s7.getEdges().get(0).getAccepts());
		NFAStatus s8 = s7.getEdges().get(0).getTargetStatus();
		assertFalse(s8.isAccept());
		List<Edge> e8 = s8.getEdges();
		Edge e8S7 = e8.get(0).getTargetStatus() == s7 ? e8.get(0) : e8.get(1);
		Edge e8S9 = e8.get(0) == e8S7 ? e8.get(1) : e8.get(0);
		assertSame(s7, e8S7.getTargetStatus());
		assertTrue(e8S9.isEpsilon());
		assertEquals(TEST_TOKEN_CODE1, e8S9.getTargetStatus().token());
	}

	@Test
	public void testInputSet() {
		//                                                                                     ↓------ϵ-----+
		// s0--a-->s1--ϵ-->s2--[xy0-9]-->s3--ϵ-->s4--ϵ-->s5--[a-z]-->s6--ϵ-->s7
		//                                                                      +---------------ϵ--------------↑
		NFAStatus s0 = NFABuilder.newNFA(Arrays.asList(new LexToken("a[xy\\d][a-z]*", TEST_TOKEN_CODE1)));
		assertFalse(s0.isAccept());
		assertEquals(Sets.newHashSet('a'), s0.getEdges().get(0).getAccepts());
		NFAStatus s1 = s0.getEdges().get(0).getTargetStatus();
		assertFalse(s1.isAccept());
		assertTrue(s1.getEdges().get(0).isEpsilon());
		NFAStatus s2 = s1.getEdges().get(0).getTargetStatus();
		assertFalse(s2.isAccept());
		HashSet<Character> xy0_9 = Sets.newHashSet();
		xy0_9.add('x');
		xy0_9.add('y');
		xy0_9.addAll(Char.numbers());
		assertEquals(xy0_9, s2.getEdges().get(0).getAccepts());
		NFAStatus s3 = s2.getEdges().get(0).getTargetStatus();
		assertFalse(s3.isAccept());
		assertTrue(s3.getEdges().get(0).isEpsilon());
		NFAStatus s4 = s3.getEdges().get(0).getTargetStatus();
		assertFalse(s4.isAccept());
		List<Edge> e4 = s4.getEdges();
		assertEquals(2, e4.size());
		Edge e4S5 = e4.get(0);
		Edge e4S7 = e4.get(1);
		NFAStatus s5 = e4S5.getTargetStatus();
		assertFalse(s5.isAccept());
		assertEquals(Char.lowers(), s5.getEdges().get(0).getAccepts());
		NFAStatus s6 = s5.getEdges().get(0).getTargetStatus();
		assertFalse(s6.isAccept());
		List<Edge> e6 = s6.getEdges();
		Edge e6S5 = e6.get(0).getTargetStatus() == s5 ? e6.get(0) : e6.get(1);
		Edge e6S7 = e6.get(0) == e6S5 ? e6.get(1) : e6.get(0);
		assertSame(s5, e6S5.getTargetStatus());
		NFAStatus s7 = e6S7.getTargetStatus();
		assertEquals(TEST_TOKEN_CODE1, s7.token());
		assertSame(s7, e4S7.getTargetStatus());
	}

	@Test
	public void testComposeRepeatNFA() {
		//                                              
		//                                                                                  +--->s6---e---->s7----+
		//                                                             +---s8--ϵ---+--->s4---d---->s5----+--ϵ-->s9--- +
		// s0--a-->s1--ϵ-->s12--ϵ-->s10--ϵ--+---------------->s2--[bc]-->s3-------------------+--ϵ-->s11--ϵ-->s13
		//                                 +             ↑--------------------------------ϵ------------------------------------+              ↑
		//                                 +-----------------------------------------ϵ-----------------------------------------------+
		NFAStatus s0 = NFABuilder.newNFA(Arrays.asList(new LexToken("a([bc]|d|e)*", TEST_TOKEN_CODE1)));
		assertFalse(s0.isAccept());
		assertEquals(Sets.newHashSet('a'), s0.getEdges().get(0).getAccepts());
		NFAStatus s1 = s0.getEdges().get(0).getTargetStatus();
		assertFalse(s1.isAccept());
		assertTrue(s1.getEdges().get(0).isEpsilon());
		NFAStatus s12 = s1.getEdges().get(0).getTargetStatus();
		assertFalse(s12.isAccept());
		List<Edge> e12 = s12.getEdges();
		assertEquals(2, e12.size());
		Edge e12S10 = e12.get(0);
		Edge e12S13 = e12.get(1);
		assertTrue(e12S10.isEpsilon());
		assertTrue(e12S13.isEpsilon());
		NFAStatus s10 = s12.getEdges().get(0).getTargetStatus();
		assertFalse(s10.isAccept());
		List<Edge> e10 = s10.getEdges();
		assertEquals(2, e10.size());
		Edge e10S8 = e10.get(0);
		Edge e10S2 = e10.get(1);
		assertTrue(e10S8.isEpsilon());
		assertTrue(e10S2.isEpsilon());
		NFAStatus s8 = e10S8.getTargetStatus();
		assertFalse(s8.isAccept());
		List<Edge> e8 = s8.getEdges();
		Edge e8S6 = e8.get(0);
		Edge e8S4 = e8.get(1);
		assertTrue(e8S6.isEpsilon());
		assertTrue(e8S4.isEpsilon());
		NFAStatus s6 = e8S6.getTargetStatus();
		assertFalse(s6.isAccept());
		assertEquals(Sets.newHashSet('e'), s6.getEdges().get(0).getAccepts());
		NFAStatus s7 = s6.getEdges().get(0).getTargetStatus();
		assertFalse(s7.isAccept());
		NFAStatus s4 = e8S4.getTargetStatus();
		assertFalse(s4.isAccept());
		assertEquals(Sets.newHashSet('d'), s4.getEdges().get(0).getAccepts());
		NFAStatus s5 = s4.getEdges().get(0).getTargetStatus();
		assertTrue(s7.getEdges().get(0).isEpsilon());
		assertTrue(s5.getEdges().get(0).isEpsilon());
		assertSame(s7.getEdges().get(0).getTargetStatus(), s5.getEdges().get(0).getTargetStatus());
		NFAStatus s9 = s7.getEdges().get(0).getTargetStatus();
		assertFalse(s9.isAccept());
		NFAStatus s2 = e10S2.getTargetStatus();
		assertFalse(s2.isAccept());
		assertEquals(Sets.newHashSet('b', 'c'), s2.getEdges().get(0).getAccepts());
		NFAStatus s3 = s2.getEdges().get(0).getTargetStatus();
		assertFalse(s3.isAccept());
		assertTrue(s3.getEdges().get(0).isEpsilon());
		assertTrue(s9.getEdges().get(0).isEpsilon());
		assertSame(s3.getEdges().get(0).getTargetStatus(), s9.getEdges().get(0).getTargetStatus());
		NFAStatus s11 = s3.getEdges().get(0).getTargetStatus();
		assertFalse(s11.isAccept());
		List<Edge> e11 = s11.getEdges();
		Edge e11S10 = e11.get(0);
		Edge e11S13 = e11.get(1);
		assertTrue(e11S10.isEpsilon());
		assertTrue(e11S13.isEpsilon());
		assertSame(s10, e11S10.getTargetStatus());
		NFAStatus s13 = e11S13.getTargetStatus();
		assertEquals(TEST_TOKEN_CODE1, s13.token());
		assertSame(s13, e12S13.getTargetStatus());
	}

	@Test
	public void testMultipleRe() {
		//             +-->s0---a---->s1
		// s4--ϵ--+-->s2--[cd]-->s3
		NFAStatus s4 = NFABuilder.newNFA(Arrays.asList(
			new LexToken("a", TEST_TOKEN_CODE1),
			new LexToken("[cd]", TEST_TOKEN_CODE2)));
		assertFalse(s4.isAccept());
		List<Edge> e4 = s4.getEdges();
		Edge e4S0 = e4.get(0);
		Edge e4S2 = e4.get(1);
		assertTrue(e4S0.isEpsilon());
		assertTrue(e4S2.isEpsilon());
		NFAStatus s0 = e4S0.getTargetStatus();
		assertFalse(s0.isAccept());
		assertEquals(Sets.newHashSet('a'), s0.getEdges().get(0).getAccepts());
		NFAStatus s2 = e4S2.getTargetStatus();
		assertFalse(s2.isAccept());
		assertEquals(Sets.newHashSet('c', 'd'), s2.getEdges().get(0).getAccepts());
		NFAStatus s1 = s0.getEdges().get(0).getTargetStatus();
		assertEquals(TEST_TOKEN_CODE1, s1.token());
		NFAStatus s3 = s2.getEdges().get(0).getTargetStatus();
		assertEquals(TEST_TOKEN_CODE2, s3.token());
	}

}
