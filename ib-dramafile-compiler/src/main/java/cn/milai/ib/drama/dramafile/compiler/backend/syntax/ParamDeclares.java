package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * 参数声明列表
 * @author milai
 * @date 2020.03.03
 */
public class ParamDeclares {

	public ParamDeclares(List<Node> children) {
	}

	public List<String> getTypeCanonicals() {
		// 暂时不支持参数类型声明
		return Lists.newArrayList();
	}

}
