package cn.milai.ib.compiler.frontend.lex;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

/**
 * 词法分析过程中 DFA 的状态
 * @author milai
 * @date 2020.02.09
 */
public class DFAStatus {

	private Map<Character, DFAStatus> edges = Maps.newHashMap();

	/**
	 * 设置通过某个字符通往的状态
	 * @param ch
	 * @param s
	 */
	public void putEdge(char ch, DFAStatus s) {
		edges.put(ch, s);
	}

	public Set<Character> accepts() {
		return edges.keySet();
	}

	/**
	 * 通过字符 ch 到达的状态，若不存在该状态，返回 null
	 * @param ch
	 * @return
	 */
	public DFAStatus next(char ch) {
		return edges.get(ch);
	}

	@Override
	public String toString() {
		return edges.keySet().toString();
	}

}
