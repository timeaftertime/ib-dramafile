package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 语法产生式转换器
 * @author milai
 * @date 2020.04.16
 */
public class GrammerConvertor {

	private static final String CFG = "CFG";

	private static final Charset ENCODING = StandardCharsets.UTF_8;

	private static final String GRAMMER_DEFINITION = "/grammer.txt";

	private static final String OUTPUT_FILE = "grammer.tmp.txt";

	public static void main(String[] args) throws IOException {
		NonTerminalSymbol cfg = readCFG();
		Set<Symbol> visited = new HashSet<>(Arrays.asList(cfg));
		try (OutputStream out = getOutputStream()) {
			writeNode(visited, out, cfg);
		}
	}

	private static void writeNode(Set<Symbol> visited, OutputStream out, NonTerminalSymbol s) throws IOException {
		Queue<NonTerminalSymbol> q = new ConcurrentLinkedQueue<>();
		for (Production p : s.getProductions()) {
			StringBuilder sb = new StringBuilder(s.getCode() + " ->");
			if (p.isEpsilon()) {
				sb.append(" " + Symbol.EPSILON.getCode());
			} else {
				for (Symbol r : p.getRights()) {
					sb.append(" " + r.getCode());
					if (r.isNonTerminal() && visited.add(r)) {
						q.add((NonTerminalSymbol) r);
					}
				}
			}
			sb.append('\n');
			out.write(sb.toString().getBytes(ENCODING));
		}
		out.write("\n".getBytes(ENCODING));
		while (!q.isEmpty()) {
			writeNode(visited, out, q.poll());
		}
	}

	private static NonTerminalSymbol readCFG() {
		return GrammerReader.parseGrammer(
			GrammerConvertor.class.getResourceAsStream(GRAMMER_DEFINITION)
		)
			.getNonTerminals().stream().filter(s -> s.getCode().equals(CFG)).findFirst().get();
	}

	private static OutputStream getOutputStream() throws FileNotFoundException {
		String path = GrammerConvertor.class.getResource("/").getFile();
		return new BufferedOutputStream(new FileOutputStream(path + OUTPUT_FILE));
	}

}
