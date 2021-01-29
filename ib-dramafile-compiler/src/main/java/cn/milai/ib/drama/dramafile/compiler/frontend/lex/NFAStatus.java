package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
	 * 能接受的字符，以及对应的下一个状态
	 */
	private Map<CharAcceptor, NFAStatus> nexts = Maps.newHashMap();

	private List<NFAStatus> epsilonNexts = Lists.newArrayList();

	private String token = null;

	public NFAStatus() {
		id = auto_increment_id++;
	}

	public int getId() {
		return id;
	}

	/**
	 * 添加一条接受字符集为 acceptor 、通往状态 status 的出边
	 * @param edge
	 */
	public void addNext(CharAcceptor acceptor, NFAStatus status) {
		nexts.put(acceptor, status);
	}

	/**
	 * 添加一条接受空串 、通往状态 status 的出边
	 * @param edge
	 */
	public void addEpsilonNext(NFAStatus status) {
		epsilonNexts.add(status);
	}

	/**
	 * 获取当前状态接受空串可以到达的所有状态
	 * @return
	 */
	public List<NFAStatus> getEpsilonNexts() {
		return Lists.newArrayList(epsilonNexts);
	}

	/**
	 * 获取当前状态接受指定字符到达的状态
	 * 若不能接受指定字符，将返回 null
	 * @param ch
	 * @return
	 */
	public NFAStatus nextOf(char ch) {
		for (CharAcceptor acceptor : nexts.keySet()) {
			if (acceptor.accept(ch)) {
				return nexts.get(acceptor);
			}
		}
		return null;
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

	@Override
	public String toString() {
		return "" + id;
	}

}
