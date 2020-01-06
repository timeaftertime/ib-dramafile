package cn.milai.ib.compiler;

import cn.milai.ib.ex.IBException;

/**
 * 常量表超出最大大小的异常
 * 2020.01.04
 * @author milai
 */
public class ConstantTableOverflow extends IBException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConstantTableOverflow() {
		super("常量表大小超出最大允许大小：65535");
	}

}
