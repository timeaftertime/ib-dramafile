package cn.milai.ib.drama.dramafile.compiler.util;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

/**
 * 集合工具类
 * @author milai
 * @date 2020.02.18
 */
public abstract class CollectionUtil {

	private CollectionUtil() {

	}

	/**
	 * 将 input 中每个元素使用 mapper 转换，返回所有结果组成的 list
	 * @param <I>
	 * @param <O>
	 * @param input
	 * @param mapper
	 * @return
	 */
	public static <I, O> List<O> extract(List<I> input, Function<I, O> mapper) {
		return input.stream().map(mapper).collect(Collectors.toList());
	}

	/**
	 * 返回一个新 set ，其中包含 input 中所有满足 predicate 的元素
	 * @param <T>
	 * @param input
	 * @param predicate
	 * @return
	 */
	public static <T> Set<T> filter(Set<T> input, Predicate<T> predicate) {
		return input.stream().filter(predicate).collect(Collectors.toSet());
	}

	/**
	 * 返回一个新 set ，其中包含 input 中所有不满足 predicate 的元素
	 * @param <T>
	 * @param input
	 * @param predicate
	 * @return
	 */
	public static <T> Set<T> unfilter(Set<T> input, Predicate<T> predicate) {
		return input.stream().filter(i -> !predicate.test(i)).collect(Collectors.toSet());
	}

	/**
	 * 返回一个新 list ，其中包含所有 list1 和 list2 的元素（不去重）
	 * @param <T>
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static <T> List<T> union(List<? extends T> list1, List<? extends T> list2) {
		List<T> list = Lists.newArrayList();
		list.addAll(list1);
		list.addAll(list2);
		return list;
	}
}
