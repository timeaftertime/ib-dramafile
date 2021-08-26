package cn.milai.ib.drama.dramafile.compiler.frontend;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

/**
 * {@link StringUtil} 的测试类
 * @author milai
 * @date 2021.08.23
 */
public class StringUtilTest {

	@Test
	public void testToCharacterArray() {
		String s = "[]abc999";
		assertArrayEquals(new Character[] { '[', ']', 'a', 'b', 'c', '9', '9', '9' }, StringUtil.toCharacterArray(s));
	}
}
