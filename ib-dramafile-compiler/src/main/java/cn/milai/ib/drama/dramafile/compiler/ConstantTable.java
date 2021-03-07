package cn.milai.ib.drama.dramafile.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.milai.common.base.Collects;
import cn.milai.ib.drama.dramafile.compiler.constant.Constant;
import cn.milai.ib.drama.dramafile.compiler.constant.FloatConstant;
import cn.milai.ib.drama.dramafile.compiler.constant.IntConstant;
import cn.milai.ib.drama.dramafile.compiler.constant.LongConstant;
import cn.milai.ib.drama.dramafile.compiler.constant.UTF8Constant;
import cn.milai.ib.drama.dramafile.compiler.ex.ConstantTableOverflow;

/**
 * 常量表，用于保存已经发现的常量
 * 2020.01.01
 * @author milai
 */
public class ConstantTable {

	/**
	 * 常量表大小最大为 2 个字节无符号整数能表示的范围
	 */
	private static int MAX_SIZE = (1 << 16) - 1;

	/**
	 * 加入一个 null 使得序号从 1 开始
	 */
	private List<Constant<?>> consts = Collects.add(Collections.emptyList(), (Constant<?>) null);

	private List<IntConstant> ints = new ArrayList<>();
	private List<LongConstant> longs = new ArrayList<>();
	private List<FloatConstant> floats = new ArrayList<>();
	private List<UTF8Constant> utf8s = new ArrayList<>();

	/**
	 * 获取常量表中的常量
	 * @return
	 */
	public Constant<?>[] getConstants() { return consts.toArray(new Constant<?>[0]); }

	/**
	 * 获取一个 int 常量的序号
	 * @param value
	 */
	public int int32Index(int value) {
		IntConstant c = (IntConstant) find(ints, value);
		if (c != null) {
			return consts.indexOf(c);
		}
		IntConstant constant = new IntConstant(value);
		ints.add(constant);
		return addConst(constant);
	}

	/**
	 * 获取一个 long 类型常量的序号
	 * @param value
	 * @return
	 */
	public int longIndex(long value) {
		LongConstant c = (LongConstant) find(longs, value);
		if (c != null) {
			return consts.indexOf(c);
		}
		LongConstant constant = new LongConstant(value);
		longs.add(constant);
		return addConst(constant);
	}

	/**
	 * 获取一个 4 个字节 float 常量的序号
	 * @param value
	 */
	public int floatIndex(float value) {
		FloatConstant c = (FloatConstant) find(floats, value);
		if (c != null) {
			return consts.indexOf(c);
		}
		FloatConstant constant = new FloatConstant(value);
		floats.add(constant);
		return addConst(constant);
	}

	/**
	 * 获取一个 MUTF8 字符串常量的序号
	 * @param value
	 */
	public int utf8Index(String value) {
		UTF8Constant c = (UTF8Constant) find(utf8s, value);
		if (c != null) {
			return consts.indexOf(c);
		}
		UTF8Constant constant = new UTF8Constant(value);
		utf8s.add(constant);
		return addConst(constant);
	}

	private <V> Constant<V> find(List<? extends Constant<V>> consts, V value) {
		for (Constant<V> c : consts) {
			if (c.getValue().equals(value)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * 添加 c 到常量表并返回其序号
	 * @param c
	 * @return
	 */
	private int addConst(Constant<?> c) {
		if (consts.size() - 1 >= MAX_SIZE) {
			throw new ConstantTableOverflow();
		}
		consts.add(c);
		return consts.indexOf(c);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < consts.size(); i++) {
			Constant<?> c = consts.get(i);
			sb.append(i + " [" + c + "]\n");
		}
		return sb.toString();
	}

}
