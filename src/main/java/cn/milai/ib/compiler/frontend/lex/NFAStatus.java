package cn.milai.ib.compiler.frontend.lex;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * 词法分析时的 NFA 中间状态
 * @author milai
 * @date 2020.02.04
 */
public class NFAStatus {

	private static int auto_increment_id = 0;

	/**
	 * 状态唯一标识
	 */
	private int id;

	/**
	 * 接受一个字符集中某个字符能转移到的状态
	 */
	private List<Edge> edges = Lists.newArrayList();

	private String token = null;

	public NFAStatus() {
		id = auto_increment_id++;
	}

	public int getId() {
		return id;
	}

	/**
	 * 添加一条接受字符集为 inputSet 、通往状态 status 的出边
	 * @param edge
	 */
	public void addEdge(Set<Character> inputSet, NFAStatus status) {
		edges.add(new Edge(inputSet, status));
	}

	/**
	 * 添加一条接受空串 、通往状态 status 的出边
	 * @param edge
	 */
	public void addEdge(NFAStatus status) {
		edges.add(new Edge(status));
	}

	/**
	 * 获取状态所有出边的列表
	 * @return
	 */
	public List<Edge> getEdges() {
		return Lists.newArrayList(edges);
	}

	/**
	 * 当前结点是否为接收状态
	 * @return
	 */
	public boolean isAccept() {
		return token != null;
	}

	/**
	 * 当前状态为接受状态时返回接受的 Token 的 code
	 * @return
	 */
	public String token() {
		if (!isAccept()) {
			throw new UnsupportedOperationException("当前状态不是接收状态");
		}
		return token;
	}

	/**
	 * 设置当前状态为接收状态且接收的 Token 类型 code 为 tokenCode
	 * @param token
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * 当前状态能接受的字符集合
	 * @return
	 */
	public Set<Character> accepts() {
		Set<Character> result = Sets.newHashSet();
		for (Edge e : edges) {
			if (e.isEpsilon()) {
				continue;
			}
			result.addAll(e.getAccepts());
		}
		return result;
	}

	@Override
	public String toString() {
		return "" + id;
	}

}
