package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.milai.common.collection.Creator;
import cn.milai.common.collection.Filter;

/**
 * 用于输出最终生成的语法推导式的工具类
 * @author milai
 * @date 2020.04.16
 */
public class GrammerConvertor {

	public static List<String> convert(InputStream input) throws IOException {
		Set<NonTerminalSymbol> nonTerminals = GrammerReader.parseGrammer(input).getNonTerminals();
		NonTerminalSymbol cfg = Filter.first(nonTerminals, s -> s.getCode().equals(Keywords.CFG)).get();
		Set<Symbol> visited = Creator.hashSet(cfg);
		List<String> output = new ArrayList<>();
		writeNode(visited, output, cfg);
		return output;
	}

	private static void writeNode(Set<Symbol> visited, List<String> out, NonTerminalSymbol s) throws IOException {
		Queue<NonTerminalSymbol> q = new ConcurrentLinkedQueue<>();
		for (Production p : s.getProductions()) {
			StringBuilder sb = new StringBuilder(s + " " + Keywords.PRODUCTION);
			if (p.isEpsilon()) {
				sb.append(" " + Symbol.EPSILON);
			} else {
				for (Symbol r : p.getRights()) {
					sb.append(" " + r.getCode());
					if (r.isNonTerminal() && visited.add(r)) {
						q.add((NonTerminalSymbol) r);
					}
				}
			}
			out.add(sb.toString() + System.lineSeparator());
		}
		out.add(System.lineSeparator());
		while (!q.isEmpty()) {
			writeNode(visited, out, q.poll());
		}
	}

}
