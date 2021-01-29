package cn.milai.ib.drama.dramafile.compiler.backend;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cn.milai.ib.drama.dramafile.compiler.ex.IBCompilerException;

/**
 * 编译器后端用到的中间数据结构
 * @author milai
 * @date 2020.02.28
 */
public class CompilerData {

	/**
	 * import 语句导入的角色 Class 名
	 */
	private Map<String, String> imports;

	/**
	 * 剧本 code
	 */
	private String dramaCode;

	/**
	 * 剧本名
	 */
	private String dramaName;

	/**
	 * 方法列表
	 */
	private List<Method> methods;

	public Map<String, String> getImports() {
		return imports;
	}

	public void setImports(Map<String, String> imports) {
		this.imports = imports;
	}

	public String getDramaCode() {
		if (StringUtils.isEmpty(dramaCode)) {
			// 语法检测之后不可能发生
			throw new IBCompilerException("未定义 dramaCode");
		}
		return dramaCode;
	}

	public void setDramaCode(String dramaCode) {
		this.dramaCode = dramaCode;
	}

	/**
	 * 获取剧本名，若未定义，将返回 dramaCode
	 * @return
	 */
	public String getDramaName() {
		if (dramaName == null) {
			return getDramaCode();
		}
		return dramaName;
	}

	public void setDramaName(String dramaName) {
		this.dramaName = dramaName;
	}

	public List<Method> getMethods() {
		return methods;
	}

	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}

}
