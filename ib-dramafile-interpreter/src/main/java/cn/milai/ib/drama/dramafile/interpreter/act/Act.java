package cn.milai.ib.drama.dramafile.interpreter.act;

import cn.milai.ib.container.DramaContainer;
import cn.milai.ib.drama.dramafile.act.ActType;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Frame;

/**
 * 剧情里的一个动作
 *
 * 2019.12.08
 *
 * @author milai
 */
public interface Act {

	/**
	 * 获取 Act 的唯一标识
	 * 
	 * @return
	 */
	ActType getCode();

	/**
	 * 执行动作
	 * 
	 * @param frame 执行命令时所在帧
	 * @param container 执行命令的对象容器
	 */
	void execute(Frame frame, DramaContainer container);

	/**
	 * 初始化 Act ，例如从 reader 中读取操作数
	 */
	void initiailze(ByteReader reader);

}
