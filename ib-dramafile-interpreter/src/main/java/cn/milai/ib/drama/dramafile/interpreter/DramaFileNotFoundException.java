package cn.milai.ib.drama.dramafile.interpreter;

import cn.milai.ib.ex.IBException;

/**
 * 获取剧本定义文件失败的异常
 * @author milai
 * @date 2020.03.05
 */
public class DramaFileNotFoundException extends IBException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DramaFileNotFoundException(String dramaCode, Throwable e) {
		super(String.format("剧本定义文件不存在：dramaCode = %s", dramaCode), e);
	}

}
