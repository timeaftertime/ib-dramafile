package cn.milai.ib.drama.dramafile.compiler.backend;

import java.util.List;

import com.google.common.collect.Lists;

import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * Methods 语法树
 * @author milai
 * @date 2020.04.16
 */
public abstract class Methods {

	private Methods() {}

	public static List<Method> parse(Node methodsNode) {
		List<Method> methods = Lists.newArrayList();
		parseMethods(methods, methodsNode);
		return methods;
	}

	private static void parseMethods(List<Method> methods, Node methodsNode) {
		// Methods -> ϵ
		if (methodsNode.getChildren().isEmpty()) {
			return;
		}
		List<Node> children = methodsNode.getChildren();
		// Methods -> TYPE_VOID IDENTIFIER ( ParamDeclares ) { Stmds } Methods
		methods.add(
			new Method(
				children.get(1).getOrigin(),
				parseDescriptor(methodsNode),
				Stmds.parse(children.get(6))
			)
		);
		parseMethods(methods, children.get(8));
	}

	private static String parseDescriptor(Node methodsNode) {
		List<Node> children = methodsNode.getChildren();
		Node returnType = children.get(0);
		Node paramDeclares = children.get(3);
		StringBuilder sb = new StringBuilder();
		sb.append(parseParamDeclares(paramDeclares));
		sb.append(canonicalOf(returnType));
		return sb.toString();
	}

	private static String parseParamDeclares(Node paramDeclaresNode) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		parseParamDeclares(sb, paramDeclaresNode);
		sb.append(")");
		return sb.toString();
	}

	private static void parseParamDeclares(StringBuilder sb, Node paramDeclaresNode) {
		// ParamDeclares -> ϵ
		if (paramDeclaresNode.getChildren().isEmpty()) {
			return;
		}
		// ParamDeclares -> FirstParamDeclare NonFirstParamDeclare
		List<Node> children = paramDeclaresNode.getChildren();
		sb.append(parseFirstParamDeclare(children.get(0)));
		Node nonFirst = children.get(1);
		while (!nonFirst.getChildren().isEmpty()) {
			// NonFirstParamDeclare -> , FirstParamDeclare NonFirstParamDeclare
			sb.append(parseFirstParamDeclare(nonFirst.getChildren().get(1)));
			nonFirst = nonFirst.getChildren().get(2);
		}
		// NonFirstParamDeclare -> ϵ
	}

	private static String parseFirstParamDeclare(Node firstParamDeclare) {
		return canonicalOf(firstParamDeclare.getChildren().get(0));
	}

	private static String canonicalOf(Node typeNode) {
		return ValueType.of(typeNode.getOrigin()).getCanonical();
	}

}
