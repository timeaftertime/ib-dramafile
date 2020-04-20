package cn.milai.ib.drama.dramafile.interpreter.runtime;

import java.util.Map;

import cn.milai.ib.drama.dramafile.interpreter.DramaFileLoader;
import cn.milai.ib.drama.dramafile.interpreter.DramaFileNotFoundException;

/**
 * 剧本空间，存储一次剧本执行过程中需要的数据
 * 类似 JVM 的线程空间
 * 2019.12.14
 * @author milai
 */
public class DramaSpace {

	/**
	 * 剧情帧的栈
	 */
	private DramaStack stack = new DramaStack();

	private String dramaName;
	private String dramaCode;

	/**
	 * 程序计数器
	 */
	private int pc;

	/**
	 * 创建一个剧本对应的剧本空间
	 * @param clip
	 * @throws DramaFileNotFoundException 若获取剧本文件失败
	 */
	public DramaSpace(Clip clip) throws DramaFileNotFoundException {
		pushClip(clip);
		dramaName = clip.getName();
		dramaCode = clip.getCode();
	}

	/**
	 * 获取该剧本空间对应的剧本名字
	 * @return
	 */
	public String getDramaName() {
		return dramaName;
	}

	/**
	 * 获取该剧本空间对应的剧本唯一标识
	 * @return
	 */
	public String getDramaCode() {
		return dramaCode;
	}

	/**
	 * 下一个要被执行指令位置
	 * @return
	 */
	public int getPC() {
		return pc;
	}

	/**
	 * 设置 PC
	 * @param pc
	 */
	public void setPC(int pc) {
		this.pc = pc;
	}

	/**
	 * 调用剧本片段
	 * @param clipCode
	 * @param params
	 * @return
	 * @throws DramaFileNotFoundException 若无法找到剧本定义文件
	 */
	public void callClip(String clipCode, Map<String, String> params) throws DramaFileNotFoundException {
		Clip clip = new Clip(DramaFileLoader.loadDrama(clipCode));
		Frame frame = pushClip(clip);
		copyParams(frame, params);
	}

	/**
	 * 构造一个对应 Clip 的新帧并压入栈顶，返回创建的新栈
	 * @param clip
	 * @return
	 */
	private Frame pushClip(Clip clip) {
		Frame frame = new Frame(clip, this);
		stack.pushFrame(frame);
		return frame;
	}

	/**
	 * 检查并复制 params 中属性到 {@link Frame}
	 * @param frame
	 * @param params 需要复制的参数 map ，若为 null 表示不需要复制
	 * @return
	 */
	private static final void copyParams(Frame frame, Map<String, String> params) {
		if (params == null) {
			return;
		}
		for (String key : params.keySet()) {
			String value = params.get(key);
			if (value == null) {
				throw new ClipParamInvalidExcecption(key, value);
			}
			frame.setVariable(key, value);
		}
	}

	public Frame popCurrentFrame() {
		return stack.popFrame();
	}

	/**
	 * 剧本是否执行完成
	 */
	public boolean isFinished() {
		return stack.isEmpty();
	}

	/**
	 * 获取当前正在执行的 Frame
	 * @return
	 */
	public Frame currentFrame() {
		return stack.currentFrame();
	}

}
