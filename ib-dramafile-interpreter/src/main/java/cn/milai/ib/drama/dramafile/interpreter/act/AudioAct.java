package cn.milai.ib.drama.dramafile.interpreter.act;

import java.io.IOException;

import cn.milai.ib.container.DramaContainer;
import cn.milai.ib.container.ui.Audio;
import cn.milai.ib.drama.dramafile.act.ActType;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Clip;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Frame;
import cn.milai.ib.loader.AudioLoader;

/**
 * 加载一个音频实例并压入栈顶
 * @author milai
 * @date 2020.04.19
 */
public class AudioAct extends AbstractAct {

	private int codeIndex;
	private int resourceIndex;

	@Override
	public ActType getCode() {
		return ActType.AUDIO;
	}

	@Override
	protected void action(Frame frame, DramaContainer container) throws Exception {
		Clip clip = frame.getClip();
		String audioCode = clip.getUTF8Const(codeIndex);
		String resource = clip.getUTF8Const(resourceIndex);
		Audio audio = AudioLoader.load(audioCode, clip.getCode(), resource);
		frame.getOperands().push(audio);
	}

	@Override
	protected void readOperands(ByteReader reader) throws IOException {
		codeIndex = reader.readUint16();
		resourceIndex = reader.readUint16();
	}

}
