package cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import cn.milai.beginning.collection.Creator;
import cn.milai.common.base.Chars;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.Alphabet;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.CharSets;

/**
 * {@link CharAcceptor} 测试类
 * @author milai
 * @date 2021.09.05
 */
public class CharAcceptorTest {

	@Test
	public void testBoolAccept() {
		CharAcceptor c1 = new IncludeAcceptor('a', 'b', 'c');
		CharAcceptor c2 = new IncludeAcceptor('1', '0', '2');
		CharAcceptor c3 = CharAcceptor.and(c1, c2);
		CharAcceptor c4 = CharAcceptor.or(c1, c2);
		assertNotSame(c1, c3);
		assertNotSame(c2, c3);
		assertNotSame(c1, c4);
		assertNotSame(c2, c4);
		for (char ch : (Chars.DIGITS + Chars.LETTERS).toCharArray()) {
			assertFalse(c3.accept(ch));
		}
		for (char ch : "abc102".toCharArray()) {
			assertTrue(c4.accept(ch));
		}

		CharAcceptor c5 = CharAcceptor.and(new IncludeAcceptor(CharSets.normals()), new ExcludeAcceptor('x', 'y', 'z'));
		assertTrue(c5.accept('a'));
		assertTrue(c5.accept('1'));
		assertTrue(c5.accept('0'));
		assertFalse(c5.accept('x'));
		assertFalse(c5.accept('y'));
		assertFalse(c5.accept('z'));
	}

	@Test
	public void testCombineAccepts() {
		CharAcceptor a1 = new CharAcceptor() {
			@Override
			public boolean accept(char c) {
				return false;
			}

			@Override
			public Set<Character> accepts(Set<Character> alphabet) {
				return Creator.asSet('a', 'c');
			}
		};
		CharAcceptor a2 = new CharAcceptor() {
			@Override
			public boolean accept(char c) {
				return false;
			}

			@Override
			public Set<Character> accepts(Set<Character> alphabet) {
				return Creator.asSet('b', 'c');
			}
		};
		CharAcceptor a3 = CharAcceptor.and(a1, a2);
		CharAcceptor a4 = CharAcceptor.or(a1, a2);
		assertEquals(Creator.asSet('c'), a3.accepts(Creator.hashSet(Alphabet.all())));
		assertEquals(Creator.asSet('a', 'b', 'c'), a4.accepts(Creator.hashSet(Alphabet.all())));
	}

	@Test
	public void testNegate() {
		Set<Character> set = new HashSet<>(Arrays.asList('a', '0', '0', '+'));
		CharAcceptor a = new ExcludeAcceptor(set);
		CharAcceptor b = a.negate();
		assertNotSame(b, a);
		for (char ch : set) {
			assertFalse(a.accept(ch));
			assertTrue(b.accept(ch));
		}
	}
}
