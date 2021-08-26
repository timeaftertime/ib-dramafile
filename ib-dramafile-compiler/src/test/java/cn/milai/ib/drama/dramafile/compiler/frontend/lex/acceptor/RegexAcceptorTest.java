package cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * {@link RegexAcceptor} 测试类
 * @author milai
 * @date 2021.08.21
 */
public class RegexAcceptorTest {

	@Test
	public void testAccept() {
		CharAcceptor acceptor = new RegexAcceptor("\\d+|=");
		assertTrue(acceptor.accept('8'));
		assertTrue(acceptor.accept('1'));
		assertTrue(acceptor.accept('2'));
		assertTrue(acceptor.accept('5'));
		assertTrue(acceptor.accept('9'));
		assertTrue(acceptor.accept('0'));
		assertTrue(acceptor.accept('='));
		assertFalse(acceptor.accept('a'));
		assertFalse(acceptor.accept('?'));
		assertFalse(acceptor.accept(';'));
		assertFalse(acceptor.accept('c'));
		assertFalse(acceptor.accept('x'));
	}

}
