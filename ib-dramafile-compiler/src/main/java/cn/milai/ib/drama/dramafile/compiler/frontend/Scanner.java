package cn.milai.ib.drama.dramafile.compiler.frontend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 扫描器
 * @author milai
 * @date 2021.08.18
 */
public abstract class Scanner<T> {

	private int index;
	protected List<T> elements;

	public Scanner(T[] es) {
		this(Arrays.asList((T[]) es));
	}

	public Scanner(Collection<T> elements) {
		this.index = 0;
		this.elements = new ArrayList<>(elements);
	}

	/**
	 * 是否还有元素
	 * @return
	 */
	public boolean hasMore() {
		return index < elements.size();
	}

	/**
	 * 将指针后移一个单位并返回移动前的位置的元素
	 * @return
	 * @throws IndexOutOfBoundsException 若移动前已经是最后一个元素
	 */
	public T next() {
		return elements.get(index++);
	}

	/**
	 * 尝试获取当前元素，若已经没有元素将返回 null
	 * @return
	 */
	public T now() {
		return index >= elements.size() ? null : elements.get(index);
	}

	/**
	 * 使读指针移动 offset 个元素。
	 * offset 为正数时将跳过指定个元素，offset 为负数时将回溯指定个元素。
	 * @param offset
	 * @throws IndexOutOfBoundsException 若移动后指针超出范围
	 */
	public void seek(int offset) throws IndexOutOfBoundsException {
		int newIndex = index + offset;
		if (newIndex < 0 || newIndex > elements.size()) {
			throw new IndexOutOfBoundsException(String.format("size = %d, newIndex = %d", elements.size(), newIndex));
		}
		index = newIndex;
	}

	@Override
	public String toString() {
		return "scanner[now=" + now() + ", index=" + index + "]";
	}

}
