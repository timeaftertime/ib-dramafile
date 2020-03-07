package cn.milai.ib.drama.dramafile.compiler;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 保存一次简单编译过程中的数据
 * @author milai
 * @date 2020.02.21
 */
public class SimpleCompilerData {

	private ConstantTable consts = new ConstantTable();
	private Map<String, String> defined = Maps.newHashMap();
	private byte[] actionBytes;

	/**
	 * 剧本唯一标识
	 */
	private String dramaCode;

	/**
	 * 剧本可读名字
	 */
	private String dramaName;

	/**
	 * 添加一个宏字符串定义
	 * @param key
	 * @param value
	 */
	public void define(String key, String value) {
		defined.put(key, value);
	}

	/**
	 * 获取一个宏字符串的值
	 * @param key
	 * @return
	 */
	public String getDefined(String key) {
		return defined.get(key);
	}

	/**
	 * 获取常量表
	 * @return
	 */
	public ConstantTable getConstTable() {
		return consts;
	}

	public byte[] getActionBytes() {
		return actionBytes;
	}

	public void setActionBytes(byte[] actionBytes) {
		this.actionBytes = actionBytes;
	}

	public String getDramaCode() {
		return dramaCode;
	}

	public void setDramaCode(String dramaCode) {
		if (this.dramaCode != null) {
			throw new IllegalStateException("不能重复设置 dramaCode");
		}
		this.dramaCode = dramaCode;
	}

	public String getDramaName() {
		return dramaName;
	}

	public void setDramaName(String dramaName) {
		if (this.dramaName != null) {
			throw new IllegalStateException("不能重复设置 dramaName");
		}
		this.dramaName = dramaName;
	}

}
