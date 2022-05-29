package cn.milai.ib.drama.dramafile.interpreter.act;

import java.io.IOException;

import cn.milai.ib.drama.dramafile.act.ActType;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Frame;
import cn.milai.ib.stage.Stage;

/**
 * 设置 BGM 的指令
 * @author milai
 * @date 2020.02.19
 */
public class BGMAct extends AbstractAct {

	@Override
	public ActType getCode() { return ActType.BGM; }

	@Override
	protected void action(Frame frame, Stage container) throws Exception {
		throw new UnsupportedOperationException("暂未实现");
	}

	@Override
	protected void readOperands(ByteReader reader) throws IOException {}

}
