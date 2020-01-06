package cn.milai.ib.compiler.constant;

import org.apache.commons.lang3.ArrayUtils;

/**
 * 字节转换工具类
 * 2020.01.02
 * @author milai
 */
public class ByteUtils {

	/**
	 * 将 int 转换为 byte 数组
	 * @param value
	 * @param size
	 * @return
	 */
	public static byte[] intToBytes(int value) {
		int size = 4;
		byte[] result = new byte[size];
		for (int i = 0; i < size; i++) {
			int shift = i * 8;
			// 0xffL 即 long 类型的 0xff
			long mask = 0xffL << shift;
			result[size - 1 - i] = (byte) ((mask & value) >> shift);
		}
		return result;
	}

	/**
	 * 将 long 转换为大小为 16 的字节数组
	 * @param value
	 * @return
	 */
	public static byte[] longToBytes(long value) {
		int highBits = (int) ((0xffffffff00000000L & value) >> 32);
		int lowBits = (int) (0x00000000ffffffffffffL & value);
		return ArrayUtils.addAll(intToBytes(highBits), intToBytes(lowBits));
	}

	/**
	 * 将 String 转换为 MUTF-8 字节数组 
	 * @param str
	 * @return
	 */
	public static byte[] strToBytes(String str) {
		int strlen = str.length();
		int utflen = 0;
		int c, count = 0;

		/* use charAt instead of copying String to char array */
		for (int i = 0; i < strlen; i++) {
			c = str.charAt(i);
			if ((c >= 0x0001) && (c <= 0x007F)) {
				utflen++;
			} else if (c > 0x07FF) {
				utflen += 3;
			} else {
				utflen += 2;
			}
		}

		if (utflen > 65535) {
			throw new IllegalArgumentException(String.format("对应的字节数组大小超过限制：%s bytes", utflen));
		}
		byte[] bytearr = new byte[utflen + 2];
		bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
		bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);

		for (int i = 0; i < strlen; i++) {
			c = str.charAt(i);
			if ((c >= 0x0001) && (c <= 0x007F)) {
				bytearr[count++] = (byte) c;
			} else if (c > 0x07FF) {
				bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
				bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
				bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			} else {
				bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
				bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			}
		}
		return bytearr;
	}

}
