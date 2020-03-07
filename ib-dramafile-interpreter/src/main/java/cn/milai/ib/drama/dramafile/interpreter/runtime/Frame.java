package cn.milai.ib.drama.dramafile.interpreter.runtime;

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

	private int pc;

	/**
	 * 对应的 Clip
	 */
	private Clip clip;

	public Frame(Clip clip, DramaSpace dramSpace) {
		this.clip = clip;
		this.dramaSpace = dramSpace;
	}

	public Clip getClip() {
		return clip;
	}

	/**
	 * 获取帧所在的 DramaSpace
	 * @return
	 */
	public DramaSpace getSpace() {
		return dramaSpace;
	}

	/**
	 * 设置当前帧将要执行的指令的位置
	 * @param pc
	 */
	public void setPC(int pc) {
		this.pc = pc;
	}

	/**
	 * 获取当前帧将要执行的指令的位置
	 * @return
	 */
	public int getPC() {
		return pc;
	}

	/**
	 * 将当前帧的 PC 同步所属到 DramaSpace
	 */
	public void synchronizeDramaSpacePC() {
		dramaSpace.setPC(pc);
	}

}
