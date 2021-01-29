package cn.milai.ib.drama.dramafile.interpreter.act;

import java.io.IOException;

import cn.milai.ib.container.DramaContainer;
import cn.milai.ib.drama.dramafile.act.ActType;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Frame;

/**
 * 加载一个常量并压入栈顶
 * @author milai
 * @date 2020.04.19
 */
public class LDCAct extends AbstractAct {

	private int constIndex;

	@Override
	public ActType getCode() {
		return ActType.LDC;
	}

	@Override
	protected void action(Frame frame, DramaContainer container) throws Exception {
		frame.getOperands().push(frame.getClip().getValueConst(constIndex));
	}

	@Override
	protected void readOperands(ByteReader reader) throws IOException {
		constIndex = reader.readUint16();
	}

}
