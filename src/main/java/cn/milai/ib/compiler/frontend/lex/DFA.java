package cn.milai.ib.compiler.frontend.lex;

/**
 * 确定有限自动机
 * @author milai
 * @date 2020.02.09
 */
public class DFA {

	private DFAStatus start;

	public DFA(DFAStatus start) {
		this.start = start;
	}

	public DFAStatus getStart() {
		return start;
	}

}
