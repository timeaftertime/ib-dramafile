package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cn.milai.common.base.Chars;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor.CharAcceptor;

/**
 * {@link CharSets} 测试类
 * @author milai
 * @date 2021.03.07
 */
public class CharSetsTest {

	@Test
	public void testSlash() {
		CharAcceptor lfAcceptor = CharSets.slash('n');
		assertTrue(lfAcceptor.accept(Chars.C_LF));
		assertFalse(lfAcceptor.accept(Chars.C_CR));
		assertFalse(lfAcceptor.accept('A'));
		assertFalse(lfAcceptor.accept('2'));
		assertFalse(lfAcceptor.accept('='));
		assertFalse(lfAcceptor.accept(' '));
		assertTrue(CharSets.slash('r').accept(Chars.C_CR));
		assertTrue(CharSets.slash('t').accept(CharSets.TAB));
		assertTrue(CharSets.slash('r').accept(Chars.C_CR));
		assertTrue(CharSets.slash('s').accept(' '));
		assertTrue(CharSets.slash('S').accept('['));
		assertTrue(CharSets.slash('w').accept('5'));
		assertTrue(CharSets.slash('W').accept(Chars.C_CR));
		assertTrue(CharSets.slash('d').accept('0'));
		assertTrue(CharSets.slash('D').accept('a'));
	}

	@Test
	public void testCanSlash() {
		for (char ch : "{}[]\\".toCharArray()) {
			assertTrue(CharSets.isCanSlash(ch));
		}
		assertFalse(CharSets.isCanSlash('a'));
		assertFalse(CharSets.isCanSlash('9'));
		assertFalse(CharSets.isCanSlash('5'));
	}

}
