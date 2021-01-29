package cn.milai.ib.drama.dramafile.interpreter.act;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cn.milai.ib.IBObject;
import cn.milai.ib.container.DramaContainer;
import cn.milai.ib.drama.dramafile.act.ActType;
import cn.milai.ib.drama.dramafile.interpreter.act.ex.IllegalOperandsException;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Clip;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Frame;
import cn.milai.ib.drama.dramafile.interpreter.runtime.OperandsStack;

/**
 * 添加对象的动作
 * 2019.12.16
 * @author milai
 */
public class AddAct extends AbstractAct {

	/**
	 * 需要生成角色的全类名， utf8 常量
	 */
	private int characteClassIndex;

	/**
	 * 构造函数参数描述符序号
	 */
	private int descriptorIndex;

	@Override
	protected void action(Frame frame, DramaContainer container) throws Exception {
		OperandsStack operands = frame.getOperands();
		Clip clip = frame.getClip();
		String className = clip.getUTF8Const(characteClassIndex);
		String descriptor = clip.getUTF8Const(descriptorIndex);
		container.addObject(createInstance(operands, className, descriptor, container));
	}

	private IBObject createInstance(OperandsStack operands, String className, String descriptor,
		DramaContainer container)
		throws Exception {
		Class<?> clazz = Class.forName(className);
		if (!IBObject.class.isAssignableFrom(clazz)) {
			throw new IllegalOperandsException(
				this,
				String.format(
					"ADD 指令的参数必须为 %s 子类的全类名, characterClass = %s", IBObject.class.getName(), clazz
						.getName()
				)
			);
		}
		int paramCnt = countsParam(descriptor);
		Object[] params = new Object[paramCnt];
		for (int i = paramCnt - 1; i >= 0; i--) {
			params[i] = operands.pop();
		}
		Constructor<?>[] cs = clazz.getConstructors();
		for (Constructor<?> c : cs) {
			IBObject obj = createIfFit(params, container, c);
			if (obj != null) {
				return obj;
			}
		}
		throw new IllegalOperandsException(
			this, String.format(
				"找不到指定类型构造方法：class = %s, descriptor = %s", clazz
					.getName(), descriptor
			)
		);
	}

	/**
	 * 尝试使用指定构造方法构造实例，若参数匹配，则返回构造的实例，否则返回 null
	 * 其中 Container 类型参数可出现在任意位置 
	 * @param params
	 * @param container
	 * @param c
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private IBObject createIfFit(Object[] params, DramaContainer container, Constructor<?> c)
		throws InstantiationException,
		IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?>[] types = c.getParameterTypes();
		// 实际参数列表应该比 paramCnt 多一个 Container
		if (types.length != params.length + 1) {
			return null;
		}
		Object[] args = new Object[params.length + 1];
		int inputIndex = 0;
		int typeIndex = 0;
		while (typeIndex < types.length) {
			if (types[typeIndex].isAssignableFrom(DramaContainer.class)) {
				args[typeIndex++] = container;
				continue;
			}
			if (inputIndex >= params.length) {
				return null;
			}
			if (!fit(params[inputIndex], types[typeIndex])) {
				return null;
			}
			args[typeIndex++] = params[inputIndex++];
		}
		if (inputIndex < params.length) {
			return null;
		}
		return (IBObject) c.newInstance(args);
	}

	private boolean fit(Object obj, Class<?> clazz) {
		try {
			if (obj.getClass() == Integer.class && clazz == int.class) {
				return true;
			}
			if (obj.getClass() == Float.class && clazz == float.class) {
				return true;
			}
			clazz.cast(obj);
		} catch (ClassCastException e) {
			return false;
		}
		return true;
	}

	/**
	 * 计算描述符所表示的参数列表个参数个数
	 * @param descriptor
	 * @return
	 */
	private int countsParam(String descriptor) {
		int cnt = 0;
		for (int i = 0; i < descriptor.length(); i++) {
			switch (descriptor.charAt(i)) {
				case 'I' :
				case 'F' :
				case 'S' :
				case 'P' :
				case 'A' :
					cnt++;
					continue;
				default: {
					throw new UnsupportedOperationException("暂未实现");
				}
			}
		}
		return cnt;
	}

	@Override
	protected void readOperands(ByteReader reader) throws IOException {
		characteClassIndex = reader.readUint16();
		descriptorIndex = reader.readUint16();
	}

	@Override
	public ActType getCode() {
		return ActType.ADD;
	}

}
