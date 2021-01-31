package cn.milai.ib.drama.dramafile.compiler;

import org.junit.Test;

import cn.milai.common.base.Bytes;

/**
 * 协助单元测试的类
 * @author milai
 * @date 2021.01.31
 */
public class ByteTestUtils {

	@Test
	public void printUTF8Bytes() {
		for (byte b : Bytes.fromStr("cn.milai.ib.demo.drama.Hello")) {
			String s = b >= 0 ? "" : "-";
			System.out.print(s + "0x" + Integer.toString(Math.abs(b), 16));
			System.out.print(", ");
		}
	}

	@Test
	public void printIntBytes() {
		for (byte b : Bytes.fromInt(-67)) {
			String s = b >= 0 ? "" : "-";
			System.out.print(s + "0x" + Integer.toString(Math.abs(b), 16));
			System.out.print(", ");
		}
	}

	@Test
	public void printShortBytes() {
		for (byte b : Bytes.fromShort(85)) {
			String s = b >= 0 ? "" : "-";
			System.out.print(s + "0x" + Integer.toString(Math.abs(b), 16));
			System.out.print(", ");
		}
	}

	@Test
	public void printLongBytes() {
		for (byte b : Bytes.fromLong(20L)) {
			String s = b >= 0 ? "" : "-";
			System.out.print(s + "0x" + Integer.toString(Math.abs(b), 16));
			System.out.print(", ");
		}
	}

	@Test
	public void printFloatBytes() {
		for (byte b : Bytes.fromFloat(-0.2f)) {
			String s = b >= 0 ? "" : "-";
			System.out.print(s + "0x" + Integer.toString(Math.abs(b), 16));
			System.out.print(", ");
		}
	}

}
