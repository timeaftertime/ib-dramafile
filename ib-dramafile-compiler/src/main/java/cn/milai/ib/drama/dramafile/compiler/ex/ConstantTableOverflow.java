package cn.milai.ib.drama.dramafile.compiler.ex;

/**
 * 常量表超出最大大小的异常
 * 2020.01.04
 * @author milai
 */
public class ConstantTableOverflow extends IBCompilerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConstantTableOverflow() {
		super("常量表大小超出最大允许大小：65535");
	}

}
