package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import cn.milai.ib.drama.dramafile.compiler.frontend.lex.acceptor.CharAcceptor;

/**
 * {@link NFAPair} 节点
 * @author milai
 * @date 2020.02.04
 */
public class NFAStatus {

	/**
	 * 出边
	 */
	private Map<CharAcceptor, NFAStatus> nexts = new HashMap<>();

	private List<NFAStatus> epsilonNexts = new ArrayList<>();

	private String token = null;

	/**
	 * 添加一条接受字符集为 {@code acceptor} 、通往状态 {@code status} 的出边
	 * @param edge
	 */
	public void addNext(CharAcceptor acceptor, NFAStatus status) {
		nexts.put(acceptor, status);
	}

	/**
	 * 添加一条接受空串 、通往指定 {@link NFAStatus} 的出边
	 * @param edge
	 */
	public void addEpsilonNext(NFAStatus status) {
		epsilonNexts.add(status);
	}

	/**
	 * 获取当前状态接受空串可以到达的所有状态
	 * @return
	 */
	public List<NFAStatus> getEpsilonNexts() { return new ArrayList<>(epsilonNexts); }

	/**
	 * 获取当前状态接受指定字符到达的状态。
	 * 若不能接受指定字符，将返回 null
	 * @param ch
	 * @return
	 */
	public NFAStatus getNext(char ch) {
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
	public boolean isAccept() { return token != null; }

	/**
	 * 当前状态为接受状态时返回接受的 Token 的 code
	 * @return
	 */
	public String token() {
		Assert.isTrue(isAccept(), "当前状态不是接收状态");
		return token;
	}

	/**
	 * 设置当前状态为接收状态且接收的 Token 类型 code 为 tokenCode
	 * @param token
	 */
	public void setToken(String token) { this.token = token; }

	@Override
	public String toString() {
		return nexts.toString();
	}

}
