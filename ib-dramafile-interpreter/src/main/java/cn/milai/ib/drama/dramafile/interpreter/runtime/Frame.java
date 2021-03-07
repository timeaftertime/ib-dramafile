package cn.milai.ib.drama.dramafile.interpreter.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * 剧本帧
 * 2019.12.08
 * @author milai
 */
public class Frame {

	/**
	 * 所属的 DramaSpace
	 */
	private DramaSpace dramaSpace;

	protected final Map<String, String> PARAMS;
	private OperandsStack operands;

	private int pc;

	/**
	 * 对应的 Clip
	 */
	private Clip clip;

	public Frame(Clip clip, DramaSpace dramSpace) {
		this.clip = clip;
		this.dramaSpace = dramSpace;
		this.PARAMS = new HashMap<>();
		operands = new OperandsStack();
	}

	public Clip getClip() { return clip; }

	/**
	 * 获取帧所在的 DramaSpace
	 * @return
	 */
	public DramaSpace getSpace() { return dramaSpace; }

	/**
	 * 设置当前帧将要执行的指令的位置
	 * @param pc
	 */
	public void setPC(int pc) { this.pc = pc; }

	/**
	 * 获取当前帧将要执行的指令的位置
	 * @return
	 */
	public int getPC() { return pc; }

	/**
	 * 将当前帧的 PC 同步所属到 DramaSpace
	 */
	public void synchronizeDramaSpacePC() {
		dramaSpace.setPC(pc);
	}

	/**
	 * 设置一个参数值
	 * @param key
	 * @param value
	 */
	public void setVariable(String key, String value) {
		PARAMS.put(key, value);
	}

	/**
	 * 获取一个参数值
	 * @param key
	 * @return
	 */
	public String getVariable(String key) {
		return PARAMS.get(key);
	}

	public OperandsStack getOperands() { return operands; }

}
