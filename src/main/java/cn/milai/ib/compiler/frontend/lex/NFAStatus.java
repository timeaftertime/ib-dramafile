package cn.milai.ib.compiler.frontend.lex;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

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
	private List<Edge> nexts = Lists.newArrayList();

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
		nexts.add(new Edge(inputSet, status));
	}

	/**
	 * 添加一条接受空串 、通往状态 status 的出边
	 * @param edge
	 */
	public void addEdge(NFAStatus status) {
		nexts.add(new Edge(status));
	}

	/**
	 * 获取状态所有出边的列表
	 * @return
	 */
	public List<Edge> getEdges() {
		return Lists.newArrayList(nexts);
	}

	@Override
	public String toString() {
		return "" + id;
	}

}