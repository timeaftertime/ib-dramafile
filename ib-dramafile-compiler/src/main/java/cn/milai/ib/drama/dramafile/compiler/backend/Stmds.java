package cn.milai.ib.drama.dramafile.compiler.backend;

import java.util.List;

import com.google.common.collect.Lists;

import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Node;

/**
 * Stmds 语法树
 * @author milai
 * @date 2020.04.16
 */
public abstract class Stmds {

	public static List<Stmd> parse(Node stmdsNode) {
		List<Stmd> stmds = Lists.newArrayList();
		parseStmds(stmds, stmdsNode);
		return stmds;
	}

	private static void parseStmds(List<Stmd> stmds, Node stmdsNode) {
		List<Node> children = stmdsNode.getChildren();
		// Stmds -> ϵ
		if (children.isEmpty()) {
			return;
		}
		// Stmds -> Stmd Stmds
		stmds.add(new Stmd(children.get(0)));
		parseStmds(stmds, children.get(1));
	}
}
