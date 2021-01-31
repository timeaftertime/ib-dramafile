package cn.milai.ib.drama.dramafile.interpreter.act;

import java.io.IOException;

import cn.milai.ib.container.DramaContainer;
import cn.milai.ib.container.ui.Image;
import cn.milai.ib.drama.dramafile.act.ActType;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Clip;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Frame;
import cn.milai.ib.loader.ImageLoader;

/**
 * 加载一张图片并压入栈顶的指令
 * @author milai
 * @date 2020.04.19
 */
public class IMGAct extends AbstractAct {

	private int resourceIndex;

	@Override
	public ActType getCode() { return ActType.IMG; }

	@Override
	protected void action(Frame frame, DramaContainer container) throws Exception {
		Clip clip = frame.getClip();
		Image img = ImageLoader.load(clip.getCode(), clip.getUTF8Const(resourceIndex));
		frame.getOperands().push(img);
	}

	@Override
	protected void readOperands(ByteReader reader) throws IOException {
		resourceIndex = reader.readUint16();
	}

}
