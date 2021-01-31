package cn.milai.ib.drama.dramafile.compiler.constant;

import cn.milai.common.base.Bytes;
import cn.milai.ib.drama.dramafile.constant.ConstantType;

/**
 * int 类型常量
 * 2020.01.01
 * @author milai
 */
public class IntConstant extends Constant<Integer> {

	public IntConstant(Integer value) {
		super(value);
	}

	@Override
	public byte[] getBytes() { return Bytes.fromInt(value); }

	@Override
	public ConstantType getType() { return ConstantType.INT; }
}
