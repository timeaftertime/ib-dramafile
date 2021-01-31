package cn.milai.ib.drama.dramafile.compiler.backend;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import cn.milai.common.ex.unchecked.Uncheckeds;

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

	/**
	 * 添加指定字节数组到最后
	 * @param data
	 * @return
	 */
	public ByteArrayBuilder append(byte[] data) {
		return Uncheckeds.rethrow(() -> {
			out.write(data);
			return this;
		}, "构造字节数组错误");
	}

	/**
	 * 将一个字符串转换为 MUTF-8 字节数组
	 * @param data
	 * @return
	 */
	public ByteArrayBuilder append(String data) {
		return Uncheckeds.rethrow(() -> {
			dataOut.writeUTF(data);
			return this;
		}, "构造字节数组错误");
	}

	/**
	 * 将一个无符号16位整数添加到末尾
	 * @param data
	 * @return
	 */
	public ByteArrayBuilder appendUInt16(int data) {
		return Uncheckeds.rethrow(() -> {
			dataOut.writeShort(data);
			return this;
		}, "构造字节数组错误");
	}

	public ByteArrayBuilder append(ByteArrayBuilder bb) {
		return Uncheckeds.rethrow(() -> {
			out.write(bb.toBytes());
			return this;
		}, "构造字节数组错误");
	}

	public byte[] toBytes() {
		return out.toByteArray();
	}
}
