package cn.milai.ib.drama.dramafile.compiler.constant;

import cn.milai.common.base.Bytes;
import cn.milai.ib.drama.dramafile.constant.ConstantType;

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
	public ConstantType getType() { return ConstantType.FLOAT; }

	@Override
	public byte[] getBytes() { return Bytes.fromInt(Float.floatToRawIntBits(value)); }

}
