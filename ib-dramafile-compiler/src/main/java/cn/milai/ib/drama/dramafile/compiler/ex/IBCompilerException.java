package cn.milai.ib.drama.dramafile.compiler.ex;

import cn.milai.ib.ex.IBException;

/**
 * 编译时异常
 * @author milai
 * @date 2020.02.02
 */
public class IBCompilerException extends IBException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IBCompilerException(Throwable e) {
		super(e);
	}

	public IBCompilerException(String msg) {
		super(msg);
	}

	public IBCompilerException(String msg, Throwable e) {
		super(msg, e);
	}

}
