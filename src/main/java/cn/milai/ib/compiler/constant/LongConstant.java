package cn.milai.ib.compiler.constant;

import cn.milai.ib.constant.ConstantType;

/**
 * long 类型常量
 * 2020.01.04
 * @author milai
 */
public class LongConstant extends Constant<Long> {

	public LongConstant(Long value) {
		super(value);
	}

	@Override
	public ConstantType getType() {
		return ConstantType.LONG;
	}

	@Override
	public byte[] getBytes() {
		return ByteUtils.longToBytes(value);
	}

}
