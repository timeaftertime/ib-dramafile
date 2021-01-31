package cn.milai.ib.drama.dramafile.compiler.constant;

import cn.milai.common.base.Bytes;
import cn.milai.ib.drama.dramafile.constant.ConstantType;

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
	public ConstantType getType() { return ConstantType.UTF8; }

	@Override
	public byte[] getBytes() { return Bytes.fromStr(value); }

}
