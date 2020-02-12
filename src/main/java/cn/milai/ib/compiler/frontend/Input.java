package cn.milai.ib.compiler.frontend;

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
	 * 使读指针移动 offset 个元素
	 * offset 为正数时将跳过指定个元素，offset 为负数时将回溯指定个元素
	 * 超出范围将抛出 
	 * @param es
	 */
	public void seek(int offset) {
		int newIndex = index + offset;
		if (newIndex < 0 || newIndex > elements.size()) {
			throw new IndexOutOfBoundsException(String.format("size = %d, newIndex = %d", elements.size(), newIndex));
		}
		index = newIndex;
	}

	/**
	 * 将剩余的元素转换为数组返回，不改变读指针位置
	 * @param array
	 * @return
	 */
	public T[] toArray(T[] array) {
		return elements.subList(index, elements.size()).toArray(array);
	}

}
