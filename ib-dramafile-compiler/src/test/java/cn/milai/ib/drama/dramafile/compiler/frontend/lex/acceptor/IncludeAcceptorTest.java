package cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * {@link IncludeAcceptor} 测试类
 * @author milai
 * @date 2021.08.21
 */
public class IncludeAcceptorTest {

	@Test
	public void testAccept() {
		CharAcceptor acceptor = new IncludeAcceptor('a', 'b', 'x', 'y', '_');
		assertTrue(acceptor.accept('a'));
		assertTrue(acceptor.accept('b'));
		assertTrue(acceptor.accept('x'));
		assertTrue(acceptor.accept('_'));
		assertTrue(acceptor.accept('y'));
		assertFalse(acceptor.accept('c'));
		assertFalse(acceptor.accept('d'));
		assertFalse(acceptor.accept('X'));
		assertFalse(acceptor.accept('Y'));
	}

}
