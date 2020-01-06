package cn.milai.ib.compiler.constant;

import cn.milai.ib.constant.ConstantType;

/**
 * float 类型常量
 * 2020.01.01
 * @author milai
 */
public class FloatConstant extends Constant<Float> {

	public FloatConstant(Float value) {
		super(value);
	}

	@Override
	public ConstantType getType() {
		return ConstantType.FLOAT;
	}

	@Override
	public byte[] getBytes() {
		return ByteUtils.intToBytes(Float.floatToRawIntBits(value));
	}

}
