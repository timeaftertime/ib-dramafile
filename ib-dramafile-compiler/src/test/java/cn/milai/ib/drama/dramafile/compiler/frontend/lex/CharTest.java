package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cn.milai.common.base.Chars;

/**
 * {@link Char} 测试类
 * @author milai
 * @date 2021.03.07
 */
public class CharTest {

	@Test
	public void testSlash() {
		CharAcceptor lfAcceptor = Char.slash('n');
		assertTrue(lfAcceptor.accept(Chars.C_LF));
		assertFalse(lfAcceptor.accept(Chars.C_CR));
		assertFalse(lfAcceptor.accept('A'));
		assertFalse(lfAcceptor.accept('2'));
		assertFalse(lfAcceptor.accept('='));
		assertFalse(lfAcceptor.accept(' '));
		assertTrue(Char.slash('r').accept(Chars.C_CR));
		assertTrue(Char.slash('t').accept(Char.TAB));
		assertTrue(Char.slash('r').accept(Chars.C_CR));
		assertTrue(Char.slash('s').accept(' '));
		assertTrue(Char.slash('S').accept('['));
		assertTrue(Char.slash('w').accept('5'));
		assertTrue(Char.slash('W').accept(Chars.C_CR));
		assertTrue(Char.slash('d').accept('0'));
		assertTrue(Char.slash('D').accept('a'));
	}

	@Test
	public void testIsNormal() {
		assertTrue(Char.isNormal('a'));
		assertTrue(Char.isNormal('b'));
		assertTrue(Char.isNormal('c'));
		assertTrue(Char.isNormal('j'));
		assertTrue(Char.isNormal('k'));
		assertTrue(Char.isNormal('l'));
		assertTrue(Char.isNormal('4'));
		assertTrue(Char.isNormal('7'));
		assertTrue(Char.isNormal('8'));
		assertTrue(Char.isNormal('_'));
		assertTrue(Char.isNormal('x'));
		assertFalse(Char.isNormal(Chars.C_CR));
		assertFalse(Char.isNormal(Chars.C_LF));
		assertFalse(Char.isNormal(Char.BLANK));
		assertFalse(Char.isNormal(Char.TAB));
		assertFalse(Char.isNormal(Char.INVERT));
	}
}
