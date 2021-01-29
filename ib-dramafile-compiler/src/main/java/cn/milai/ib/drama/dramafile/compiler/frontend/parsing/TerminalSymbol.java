package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

/**
 * 终结符
 * @author milai
 * @date 2020.02.14
 */
public class TerminalSymbol extends Symbol {

	public TerminalSymbol(String code) {
		super(code);
	}

	@Override
	public boolean isNonTerminal() {
		return false;
	}
}
