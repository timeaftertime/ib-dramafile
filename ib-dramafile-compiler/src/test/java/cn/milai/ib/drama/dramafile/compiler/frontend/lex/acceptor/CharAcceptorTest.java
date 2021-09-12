package cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cn.milai.common.base.Chars;

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

		CharAcceptor c5 = CharAcceptor.and(new RegexAcceptor("\\w"), new ExcludeAcceptor('x', 'y', 'z'));
		assertTrue(c5.accept('a'));
		assertTrue(c5.accept('1'));
		assertTrue(c5.accept('0'));
		assertFalse(c5.accept('x'));
		assertFalse(c5.accept('y'));
		assertFalse(c5.accept('z'));
	}
}
