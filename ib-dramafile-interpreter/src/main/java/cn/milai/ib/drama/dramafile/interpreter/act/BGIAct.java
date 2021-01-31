package cn.milai.ib.drama.dramafile.interpreter.act;

import java.io.IOException;

import cn.milai.ib.container.DramaContainer;
import cn.milai.ib.drama.dramafile.act.ActType;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Frame;

/**
 * 设置背景图片的指令
 * @author milai
 * @date 2020.02.21
 */
public class BGIAct extends AbstractAct {

	@Override
	public ActType getCode() { return ActType.BGI; }

	@Override
	protected void action(Frame frame, DramaContainer container) throws Exception {
		throw new UnsupportedOperationException("暂未实现");
	}

	@Override
	protected void readOperands(ByteReader reader) throws IOException {}

}
