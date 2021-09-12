package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

/**
 * {@link NodeUtil} 测试类
 * @author milai
 * @date 2021.09.08
 */
public class NodeUtilTest {

	private static final String TEST_TOKEN_CODE1 = "TEST_TOKEN_CODE1";
	private static final String TEST_TOKEN_CODE2 = "TEST_TOKEN_CODE2";

	@Test
	public void testMinimizeDFA() {
		// s0--f-->s1--[ie]-->s2--e-->s3
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
		s0 = NodeUtil.minimize(s0);
		assertEquals(new HashSet<>(Arrays.asList('f')), s0.accepts());
		assertFalse(s0.isAccept());
		Node s1 = s0.next('f');
		assertEquals(new HashSet<>(Arrays.asList('i', 'e')), s1.accepts());
		assertFalse(s1.isAccept());
		Node s2 = s1.next('i');
		assertSame(s2, s1.next('e'));
		assertFalse(s2.isAccept());
		assertEquals(new HashSet<>(Arrays.asList('e')), s2.accepts());
		Node s3 = s2.next('e');
		assertEquals(new HashSet<>(Arrays.asList(TEST_TOKEN_CODE1, TEST_TOKEN_CODE2)), s3.tokens());
	}

	@Test
	public void testFindFirstStatus() {
		// head ---> n1 ---> n3-b->n4
		//           +-a->n2
		Node head = new NFANode();
		Node n1 = new NFANode();
		head.addEpsilonNext(n1);
		Node n2 = new DFANode();
		head.addNext('a', n2);
		Node n3 = new NFANode();
		n1.addEpsilonNext(n3);
		Node n4 = new DFANode();
		n3.addNext('b', n4);
		List<Node> nodes = Arrays.asList(n1, n4, n3, head, n2);
		assertSame(head, NodeUtil.firstStatusOf(nodes));
		n3.addEpsilonNext(head);
		try {
			NodeUtil.firstStatusOf(nodes);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail();
	}

	@Test
	public void testClouse() {
		// n1 --> n2 --> n3
		//       +--> n4 -x-> n5
		//       +-c->n6 --> n7
		Node n1 = new NFANode();
		Node n2 = new NFANode();
		n1.addEpsilonNext(n2);
		Node n3 = new DFANode();
		n2.addEpsilonNext(n3);
		Node n4 = new NFANode();
		n1.addEpsilonNext(n4);
		Node n5 = new NFANode();
		n4.addNext('x', n5);
		Node n6 = new NFANode();
		n1.addNext('c', n6);
		Node n7 = new NFANode();
		n6.addEpsilonNext(n7);
		assertEquals(new HashSet<>(Arrays.asList(n1, n2, n3, n4)), NodeUtil.closure(n1));
		assertEquals(new HashSet<>(Arrays.asList(n2, n3)), NodeUtil.closure(n2));
		assertEquals(new HashSet<>(Arrays.asList(n2, n3, n6, n7)), NodeUtil.closure(Arrays.asList(n2, n6)));
		assertEquals(new HashSet<>(Arrays.asList(n4)), NodeUtil.closure(n4));
	}

	@Test
	public void testDFANext() {
		// n1 ---4-> n2
		//        +-3->n3-1->n5
		//        +-2->n4
		//        +-1-->n6
		DFANode n1 = new DFANode();
		DFANode n2 = new DFANode();
		n1.addNext('3', n2);
		DFANode n3 = new DFANode();
		n1.addNext('3', n3);
		DFANode n4 = new DFANode();
		n1.addNext('2', n4);
		DFANode n5 = new DFANode();
		n3.addNext('1', n5);
		DFANode n6 = new DFANode();
		n1.addNext('1', n6);
		assertEquals(new HashSet<>(Arrays.asList(n3)), NodeUtil.nextsOf(Arrays.asList(n1, n3), '3'));
		assertEquals(new HashSet<>(Arrays.asList(n3)), NodeUtil.nextsOf(Arrays.asList(n1), '3'));
		assertEquals(Collections.emptySet(), NodeUtil.nextsOf(Arrays.asList(n3), '2'));
		assertEquals(Collections.emptySet(), NodeUtil.nextsOf(Arrays.asList(n3, n6, n4), '2'));
		assertEquals(new HashSet<>(Arrays.asList(n6, n5)), NodeUtil.nextsOf(Arrays.asList(n1, n3), '1'));
	}
}
