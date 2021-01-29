package cn.milai.ib.drama.dramafile.compiler.backend;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cn.milai.ib.ex.IBIOException;

/**
 * 类似于 StringBuilder 的字节数组构造器
 * @author milai
 * @date 2020.02.29
 */
public class ByteArrayBuilder {

	private ByteArrayOutputStream out;

	private DataOutputStream dataOut;

	public ByteArrayBuilder() {
		out = new ByteArrayOutputStream();
		dataOut = new DataOutputStream(out);
	}

	public ByteArrayBuilder appendByte(int data) {
		out.write(data);
		return this;
	}

	public ByteArrayBuilder append(byte[] data) {
		try {
			out.write(data);
		} catch (IOException e) {
			throw new IBIOException("构造字节数组发生未知错误", e);
		}
		return this;
	}

	/**
	 * 将一个字符串转换为 MUTF-8 字节数组
	 * @param data
	 * @return
	 */
	public ByteArrayBuilder append(String data) {
		try {
			dataOut.writeUTF(data);
		} catch (IOException e) {
			throw new IBIOException("构造字节数组发生未知错误", e);
		}
		return this;
	}

	/**
	 * 将一个无符号16位整数添加到末尾
	 * @param data
	 * @return
	 */
	public ByteArrayBuilder appendUInt16(int data) {
		try {
			dataOut.writeShort(data);
		} catch (IOException e) {
			throw new IBIOException("构造字节数组发生未知错误", e);
		}
		return this;
	}

	public ByteArrayBuilder append(ByteArrayBuilder bb) {
		try {
			out.write(bb.toBytes());
		} catch (IOException e) {
			throw new IBIOException("构造字节数组发生未知错误", e);
		}
		return this;
	}

	public byte[] toBytes() {
		return out.toByteArray();
	}
}
