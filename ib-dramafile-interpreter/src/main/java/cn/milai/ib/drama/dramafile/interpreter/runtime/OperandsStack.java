package cn.milai.ib.drama.dramafile.interpreter.runtime;

import java.util.Stack;

/**
 * 操作数栈
 * @author milai
 * @date 2020.04.19
 */
public class OperandsStack {

	private Stack<Object> s = new Stack<>();

	/**
	 * 压入一个元素到栈顶
	 * @param obj
	 */
	public void push(Object obj) {
		s.push(obj);
	}
	
	/**
	 * 弹出栈顶第一个元素
	 * @return
	 */
	public Object pop() {
		return s.pop();
	}
	
	/**
	 * 弹出栈顶第一个元素并强制转换为指定类型
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T pop(Class<T> clazz) {
		return (T) pop();
	}
}
