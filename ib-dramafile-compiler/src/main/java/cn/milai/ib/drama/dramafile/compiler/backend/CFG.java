package cn.milai.ib.drama.dramafile.compiler.backend;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

import cn.milai.common.util.Strings;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * 语法树根节点
 * @author milai
 * @date 2020.04.16
 */
public abstract class CFG {

	private CFG() {

	}

	public static CompilerData parse(Node cfgNode) {
		CompilerData data = new CompilerData();
		// CFG -> Imports IDENTIFIER DramaName BLOCK_LEFT Methods BLOCK_RIGHT
		List<Node> children = cfgNode.getChildren();
		data.setImports(parseImports(children.get(0)));
		data.setDramaCode(parseDramaCode(children.get(1)));
		data.setDramaName(parseDramaName(children.get(2), data.getDramaCode()));
		data.setMethods(Methods.parse(children.get(4)));
		return data;
	}

	private static String parseDramaName(Node dramaNameNode, String dramaCode) {
		String name = Strings.slice(dramaNameNode.getOrigin(), 2, -2);
		return StringUtils.isEmpty(name) ? dramaCode : name;
	}

	private static String parseDramaCode(Node dramaCodeNode) {
		return dramaCodeNode.getOrigin();
	}

	private static Map<String, String> parseImports(Node importsNode) {
		Map<String, String> imports = Maps.newHashMap();
		parseImports(imports, importsNode);
		return imports;
	}

	private static void parseImports(Map<String, String> imports, Node importsNode) {
		// Imports -> ϵ
		if (importsNode.getChildren().isEmpty()) {
			return;
		}
		// Imports -> IMPORT Identity Alias STMD_END Imports
		List<Node> children = importsNode.getChildren();
		String value = children.get(1).getOrigin();
		String key = children.get(2).getOrigin();
		if (StringUtils.isEmpty(key)) {
			key = value.substring(value.lastIndexOf('.') + 1);
		}
		imports.put(key, value);
		parseImports(imports, children.get(4));
	}

}
