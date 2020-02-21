package cn.milai.ib.compiler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;

public class ByteTestUtils {

	@Test
	public void printUTF8Bytes() {
		for (byte b : getBytes("测试用的剧本")) {
			String s = b >= 0 ? "" : "-";
			System.out.print(s + "0x" + Integer.toString(Math.abs(b), 16));
			System.out.print(", ");
		}
	}

	@Test
	public void printIntBytes() {
		for (byte b : getBytes(1)) {
			String s = b >= 0 ? "" : "-";
			System.out.print(s + "0x" + Integer.toString(Math.abs(b), 16));
			System.out.print(", ");
		}
	}

	@Test
	public void printShortBytes() {
		for (byte b : getShortBytes(20)) {
			String s = b >= 0 ? "" : "-";
			System.out.print(s + "0x" + Integer.toString(Math.abs(b), 16));
			System.out.print(", ");
		}
	}

	@Test
	public void printLongBytes() {
		for (byte b : getBytes(20L)) {
			String s = b >= 0 ? "" : "-";
			System.out.print(s + "0x" + Integer.toString(Math.abs(b), 16));
			System.out.print(", ");
		}
	}

	@Test
	public void printFloatBytes() {
		for (byte b : getBytes(-0.1f)) {
			String s = b >= 0 ? "" : "-";
			System.out.print(s + "0x" + Integer.toString(Math.abs(b), 16));
			System.out.print(", ");
		}
	}

	public static byte[] getBytes(String value) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream writer = new DataOutputStream(out);
		try {
			writer.writeUTF(value);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	public static byte[] getBytes(int value) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream writer = new DataOutputStream(out);
		try {
			writer.writeInt(value);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	public static byte[] getBytes(long value) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream writer = new DataOutputStream(out);
		try {
			writer.writeLong(value);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	public static byte[] getShortBytes(int value) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream writer = new DataOutputStream(out);
		try {
			writer.writeShort(value);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	public static byte[] getBytes(float value) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream writer = new DataOutputStream(out);
		try {
			writer.writeFloat(value);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}
}
