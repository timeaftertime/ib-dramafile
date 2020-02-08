package cn.milai.ib.compiler.frontend.lex;

import java.util.List;

import com.google.common.collect.Lists;

public class Input<T> {

	protected int index;
	protected List<T> elements;

	public Input(T[] array) {
		this.index = 0;
		this.elements = Lists.newArrayList();
		for (T e : array) {
			this.elements.add(e);
		}
	}

	/**
	 * 是否还有元素
	 * @return
	 */
	public boolean hasNext() {
		return index < elements.size();
	}

	/**
	 * 获取下一个元素并将指针后移一个单位
	 * @return
	 */
	public T next() {
		return elements.get(index++);
	}

	/**
	 * 查看下一个元素
	 * @return
	 */
	public T getNext() {
		return elements.get(index);
	}

	/**
	 * 获取之前一个元素
	 * @return
	 */
	public T getPre() {
		return elements.get(index - 1);
	}
}
