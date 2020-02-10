package cn.milai.ib.compiler.frontend.lex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;

import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * 测试 DFA 构造算法
 * @author milai
 * @date 2020.02.10
 */
public class DFABuilderTest {

	@Test
	public void testLineDFA() {
		// s0--a-->s1--b-->2--c-->s3--d-->s4--e-->s5
		DFA dfa = DFABuilder.newDFA(NFABuilder.newNFA(Arrays.asList("abcde")));
		DFAStatus s = dfa.getStart();
		for (int i = 0; i < 5; i++) {
			char ch = (char) ('a' + i);
			assertEquals(Sets.newHashSet(ch), s.accepts());
			s = s.next(ch);
		}
	}

	@Test
	public void testOrDFA() {
		// s0--f-->s1--[ie]-->s2--e-->s3
		DFA dfa = DFABuilder.newDFA(NFABuilder.newNFA(Arrays.asList("fee", "fie")));
		DFAStatus s0 = dfa.getStart();
		assertEquals(Sets.newHashSet('f'), s0.accepts());
		DFAStatus s1 = s0.next('f');
		assertEquals(Sets.newHashSet('i', 'e'), s1.accepts());
		DFAStatus s2 = s1.next('i');
		assertSame(s2, s1.next('e'));
		assertEquals(Sets.newHashSet('e'), s2.accepts());
	}

}
