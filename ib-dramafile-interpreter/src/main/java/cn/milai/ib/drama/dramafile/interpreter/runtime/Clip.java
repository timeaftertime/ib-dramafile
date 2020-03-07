package cn.milai.ib.drama.dramafile.interpreter.runtime;

import java.util.Map;

import com.google.common.collect.Maps;

import cn.milai.ib.drama.dramafile.interpreter.statics.DramaMetadata;

/**
 * 对应一个剧本文件
 * 2019.12.24
 * @author milai
 */
public class Clip {

	protected DramaMetadata drama;
	protected final Map<String, String> PARAMS;

	public Clip(DramaMetadata drama) {
		this.drama = drama;
		this.PARAMS = Maps.newHashMap();
	}

	/**
	 * 获取剧本唯一标识
	 * @return
	 */
	public String getCode() {
		return drama.getCode();
	}

	/**
	 * 获取剧本可读名字
	 */
	public String getName() {
		return drama.getName();
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

	/**
	 * 获取剧本的字节数据
	 * @return
	 */
	public byte[] getBytes() {
		return drama.getClipBytes();
	}

	/**
	 * 获取常量池中序号为 index 的 int 类型常量
	 * @param index
	 * @return
	 */
	public int getIntConst(int index) {
		return drama.getIntConst(index);
	}

	/**
	 * 获取常量池中序号为 index 的 long 类型常量
	 * @param index
	 * @return
	 */
	public long getLongConst(int index) {
		return drama.getLongConst(index);
	}

	/**
	 * 获取常量池中序号为 index 的 float 类型常量
	 * @param index
	 * @return
	 */
	public float getFloatConst(int index) {
		return drama.getFloatConst(index);
	}

	/**
	 * 获取常量池中序号为 index 的 M-UTF8字符串类型常量
	 * @param index
	 * @return
	 */
	public String getUTF8Const(int index) {
		return drama.getUTF8Const(index);
	}

}
