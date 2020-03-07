package cn.milai.ib.drama.dramafile.compiler.backend;

import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * 简单条件判断进行翻译的后端实现，临时方案
 * @author milai
 * @date 2020.02.28
 */
public class SimpleTranslator {

	/**
	 * 将抽象语法树转换为字节数组
	 * @param cfgNode 抽象语法树根节点
	 * @return
	 */
	public CompilerData translate(Node cfgNode) {
		CompilerData data = new CompilerData();
		List<Node> children = cfgNode.getChildren();
		data.setImports(parseImports(children.get(0)));
		data.setDramaCode(parseString(children.get(2)));
		data.setDramaName(parseDramaName(children.get(3)));
		data.setMethods(parseMethods(children.get(5)));
		return data;
	}

	/**
	 * 解析 Methods 语法树
	 * @param methodsNode
	 * @return
	 */
	private List<Method> parseMethods(Node methodsNode) {
		List<Method> methods = Lists.newArrayList();
		List<Node> children = methodsNode.getChildren();
		if (children.isEmpty()) {
			return methods;
		}
		methods.add(new Method(children.get(0)));
		methods.addAll(parseMethods(children.get(1)));
		return methods;
	}

	/**
	 * 解析 dramaName 语法树，返回声明的 dramaName 字符串
	 * 若没有声明 dramaName 返回 null
	 * @param dramaNameNode
	 * @return
	 */
	private String parseDramaName(Node dramaNameNode) {
		if (dramaNameNode.getChildren().isEmpty()) {
			return null;
		}
		return parseString(dramaNameNode.getChildren().get(1));
	}

	/**
	 * 解析 Imports 语法树
	 * @param importsNode
	 * @return
	 */
	private Map<String, String> parseImports(Node importsNode) {
		Map<String, String> imports = Maps.newHashMap();
		List<Node> importNodes = importsNode.getChildren();
		if (importNodes.isEmpty()) {
			return imports;
		}
		for (Node importNode : importNodes) {
			parseImport(imports, importNode);
		}
		return imports;
	}

	/**
	 * 解析 import 语句
	 * @param imports
	 * @param importNode
	 */
	private void parseImport(Map<String, String> imports, Node importNode) {
		List<Node> children = importNode.getChildren();
		String character = parseIdentity(children.get(1));
		String alias = parseAlias(children.get(2));
		if (StringUtils.isEmpty(alias)) {
			alias = getCanonicalName(character);
		}
		imports.put(alias, character);
	}

	/**
	 * 获取全类名最简名称
	 * @param character
	 * @return
	 */
	private String getCanonicalName(String character) {
		return character.substring(character.lastIndexOf('.') + 1);
	}

	/**
	 * 解析 Alias 语法树
	 * @param aliasNode
	 * @return
	 */
	private String parseAlias(Node aliasNode) {
		return parseString(aliasNode);
	}

	/**
	 * 解析 Identity 语法树
	 * @param identityNode
	 * @return
	 */
	private static String parseIdentity(Node identityNode) {
		return parseString(identityNode);
	}

	/**
	 * 获取 node 语法树匹配到的原始字符串
	 * @param node
	 * @return
	 */
	private static String parseString(Node node) {
		if (!node.getSymbol().isNonTerminal()) {
			return node.getToken().getOrigin();
		}
		StringBuilder sb = new StringBuilder();
		for (Node child : node.getChildren()) {
			sb.append(parseString(child));
		}
		return sb.toString();
	}

}
