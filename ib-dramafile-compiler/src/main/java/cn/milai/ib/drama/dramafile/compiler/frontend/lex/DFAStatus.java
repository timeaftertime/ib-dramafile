package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 词法分析过程中 DFA 的状态
 * @author milai
 * @date 2020.02.09
 */
public class DFAStatus {

	/**
	 * 边 -> 通往的状态
	 */
	private Map<Character, DFAStatus> edges = new HashMap<>();

	/**
	 * 表示接受状态时所接受的 Token 的 code
	 */
	private Set<String> tokens = new HashSet<>();

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

	/**
	 * 当前状态是否为接收状态
	 * @return
	 */
	public boolean isAccept() { return tokens.size() > 0; }

	/**
	 * 当前状态时接受状态是返回所接受的 Token 的 code
	 * @return
	 */
	public Set<String> tokens() {
		if (!isAccept()) {
			throw new UnsupportedOperationException("当前状态不是接受状态");
		}
		return tokens;
	}

	/**
	 * 设置当前状态为接受状态且添加 tokenCode 到所接受 Token 列表
	 * @param token
	 */
	public void addToken(String token) {
		tokens.add(token);
	}

	/**
	 * 设置当前状态为接受状态且添加 tokenCodes 到所接受 Token 列表
	 * @param tokenCode
	 */
	public void addTokens(Set<String> tokens) {
		this.tokens.addAll(tokens);
	}

	@Override
	public String toString() {
		return edges.keySet().toString();
	}

}
