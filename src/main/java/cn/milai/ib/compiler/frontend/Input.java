package cn.milai.ib.compiler.frontend;

import java.util.List;
import java.util.function.Predicate;

import com.google.common.collect.Lists;

public abstract class Input<T> {

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
	 * 尝试获取下一个元素
	 * 若已经没有元素将返回 null
	 */
	public T getNext() {
		if (index >= elements.size()) {
			return null;
		}
		return elements.get(index);
	}

	/**
	 * 使读指针移动 offset 个元素
	 * offset 为正数时将跳过指定个元素，offset 为负数时将回溯指定个元素
	 * 超出范围将抛出 IndexOutOfBoundsException
	 * @param es
	 * @throws IndexOutOfBoundsException
	 */
	public void seek(int offset) throws IndexOutOfBoundsException {
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

	/**
	 * 返回一个新输入序列，其中所有元素来自当前序列且满足给定条件
	 * @param p
	 * @return
	 */
	public abstract Input<T> filter(Predicate<T> p);

	@Override
	public String toString() {
		return "Input[next=" + getNext() + ", index=" + index + "]";
	}

}
