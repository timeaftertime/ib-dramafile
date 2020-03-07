package cn.milai.ib.drama.dramafile.interpreter.act;

import java.io.IOException;
import java.io.InputStream;

import cn.milai.ib.container.Container;
import cn.milai.ib.drama.dramafile.act.ActType;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Frame;
import cn.milai.ib.loader.DramaResLoader;
import cn.milai.ib.util.ImageUtil;

/**
 * 设置背景图片的指令
 * @author milai
 * @date 2020.02.21
 */
public class BGIAct extends AbstractAct {

	private int resourceIndex;

	@Override
	public ActType getCode() {
		return ActType.BGI;
	}

	@Override
	protected void action(Frame frame, Container container) throws Exception {
		String resource = frame.getClip().getUTF8Const(resourceIndex);
		InputStream in = DramaResLoader.load(frame.getClip().getCode(), resource);
		container.setBackgroud(ImageUtil.loadImage(in));
	}

	@Override
	protected void readOperands(ByteReader reader) throws IOException {
		resourceIndex = reader.readUint16();
	}

}
