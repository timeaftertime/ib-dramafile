package cn.milai.ib.drama.dramafile.compiler.constant;

import cn.milai.ib.drama.dramafile.constant.ConstantType;

/**
 * 常量类
 * 2020.01.01
 * @author milai
 */
public abstract class Constant<T> {

	protected T value;

	public abstract ConstantType getType();

	public Constant(T value) {
		this.value = value;
	}

	/**
	 * 获取常量所代表的值
	 * @return
	 */
	public T getValue() {
		return value;
	}

	/**
	 * 获取常量值的字节数组形式
	 * @return
	 */
	public abstract byte[] getBytes();

	@Override
	public String toString() {
		return getType() + " " + value;
	}

}
