package cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * {@link ExcludeAcceptor} 测试类
 * @author milai
 * @date 2021.08.21
 */
public class ExcludeAcceptorTest {

	@Test
	public void testAcceptor() {
		CharAcceptor acceptor = new ExcludeAcceptor('a', 'b', 'x', 'y', '_');
		assertFalse(acceptor.accept('a'));
		assertFalse(acceptor.accept('b'));
		assertFalse(acceptor.accept('x'));
		assertFalse(acceptor.accept('_'));
		assertFalse(acceptor.accept('y'));
		assertTrue(acceptor.accept('c'));
		assertTrue(acceptor.accept('d'));
		assertTrue(acceptor.accept('X'));
		assertTrue(acceptor.accept('Y'));
	}
}
