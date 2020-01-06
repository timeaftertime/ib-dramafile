package cn.milai.ib.compiler.constant;

import cn.milai.ib.constant.ConstantType;

/**
 * MUTF8 字符串类型常量
 * 2020.01.03
 * @author milai
 */
public class UTF8Constant extends Constant<String> {

	public UTF8Constant(String value) {
		super(value);
	}

	@Override
	public ConstantType getType() {
		return ConstantType.UTF8;
	}

	@Override
	public byte[] getBytes() {
		return ByteUtils.strToBytes(value);
	}

}
