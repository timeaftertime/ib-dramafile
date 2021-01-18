package cn.milai.ib.drama.dramafile.compiler.constant;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;

import cn.milai.ib.drama.dramafile.compiler.ByteTestUtils;

public class ByteUtilsTest {

	@Test
	public void testIntToBytes() {
		int value = 123;
		assertArrayEquals(ByteTestUtils.getBytes(123), ByteUtils.intToBytes(value));
		value = -128;
		assertArrayEquals(ByteTestUtils.getBytes(value), ByteUtils.intToBytes(value));
		value = 0;
		assertArrayEquals(ByteTestUtils.getBytes(value), ByteUtils.intToBytes(value));
		value = 11223344;
		assertArrayEquals(ByteTestUtils.getBytes(value), ByteUtils.intToBytes(value));
	}

	@Test
	public void testLongToBytes() {
		long value = 999888777012L;
		assertArrayEquals(ByteTestUtils.getBytes(value), ByteUtils.longToBytes(value));
		value = 0L;
		assertArrayEquals(ByteTestUtils.getBytes(value), ByteUtils.longToBytes(value));
		value = -12345987600L;
		assertArrayEquals(ByteTestUtils.getBytes(value), ByteUtils.longToBytes(value));
	}

	@Test
	public void testStrToBytes() throws IOException {
		String[] values = { "IFBT", "", "cn.milai.ib.compiler.constant.ByteUtilsTest", "12345" };
		for (String value : values) {
			assertArrayEquals(getMUTF8(value), ByteUtils.strToBytes(value));
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 65536; i++) {
			sb.append('A');
		}
		try {
			ByteUtils.strToBytes(sb.toString());
		} catch (IllegalArgumentException e) {
			return;
		}
		fail();
	}

	private byte[] getMUTF8(String str) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(result);
		out.writeUTF(str);
		return result.toByteArray();
	}
}
