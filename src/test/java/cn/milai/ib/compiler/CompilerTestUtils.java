package cn.milai.ib.compiler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;

public class CompilerTestUtils {

	public static byte[] getBytes() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream writer = new DataOutputStream(out);
		try {
			writer.writeFloat(1.0f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	@Test
	public void printBytes() {
		for (byte b : getBytes()) {
			String s = b >= 0 ? "" : "-";
			System.out.print(s + "0x" + Integer.toString(Math.abs(b), 16));
			System.out.print(", ");
		}
	}

}
