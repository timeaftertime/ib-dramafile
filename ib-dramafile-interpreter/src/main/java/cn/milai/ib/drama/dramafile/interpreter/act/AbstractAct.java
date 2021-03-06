package cn.milai.ib.drama.dramafile.interpreter.act;

import java.io.IOException;

import cn.milai.ib.container.DramaContainer;
import cn.milai.ib.drama.dramafile.interpreter.act.ex.ActExecuteException;
import cn.milai.ib.drama.dramafile.interpreter.act.ex.ActNotInitializedException;
import cn.milai.ib.drama.dramafile.interpreter.act.ex.ActReadOperandsException;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Frame;

/**
 * 动作的抽象基类
 * 主要检查初始化、将 cheked 异常转变为 RuntimeException
 *
 * 2019.12.16
 *
 * @author milai
 */
public abstract class AbstractAct implements Act {

	private boolean initialized = false;

	@Override
	public void execute(Frame frame, DramaContainer container) {
		if (!initialized) {
			throw new ActNotInitializedException();
		}
		try {
			action(frame, container);
		} catch (Exception e) {
			throw new ActExecuteException(this, e);
		}
	}

	/**
	 * 真正执行动作
	 * 
	 * @param frame
	 * @param container
	 */
	protected abstract void action(Frame frame, DramaContainer container) throws Exception;

	@Override
	public void initiailze(ByteReader reader) {
		try {
			readOperands(reader);
		} catch (IOException e) {
			throw new ActReadOperandsException(this, e);
		}
		initialized = true;
	}

	/**
	 * 读取操作数
	 * 
	 * @param reader
	 */
	protected abstract void readOperands(ByteReader reader) throws IOException;

}
