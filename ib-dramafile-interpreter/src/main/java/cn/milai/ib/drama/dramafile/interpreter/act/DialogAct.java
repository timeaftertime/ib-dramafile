package cn.milai.ib.drama.dramafile.interpreter.act;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

import cn.milai.ib.container.Container;
import cn.milai.ib.container.DramaContainer;
import cn.milai.ib.container.plugin.ui.Image;
import cn.milai.ib.control.Control;
import cn.milai.ib.drama.dramafile.act.ActType;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Clip;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Frame;
import cn.milai.ib.loader.DramaStringLoader;
import cn.milai.ib.loader.ImageLoader;
import cn.milai.ib.util.Waits;

public class DialogAct extends AbstractAct {

	private static final Class<?>[] IIISC = new Class[] {
		Integer.class, Integer.class, Image.class, String.class, Container.class
	};

	/**
	 * 需要生成对话框的全类名， utf8 常量
	 */
	private int characteClassIndex;

	/**
	 * x 坐标相对于容器 width 的百分比， float 类型常量
	 */
	private int xRateIndex;

	/**
	 * y 坐标相对于容器 height 的百分比，float 类型常量
	 */
	private int yRateIndex;

	/**
	 * 说话者全类名的序号，utf8 常量
	 */
	private int speakerClassIndex;

	/**
	 * 需要显示的对话文本的序号
	 */
	private int textIndex;

	@Override
	public ActType getCode() { return ActType.DIALOG; }

	@Override
	protected void action(Frame frame, DramaContainer container) throws Exception {
		Clip clip = frame.getClip();
		String dialogClass = clip.getUTF8Const(characteClassIndex);
		int x = (int) (clip.getFloatConst(xRateIndex) * container.getW());
		int y = (int) (clip.getFloatConst(yRateIndex) * container.getH());
		Image speaker = resolveSpeaker(clip);
		String text = DramaStringLoader.get(clip.getCode(), clip.getUTF8Const(textIndex));
		container.addObject(createInstance(dialogClass, x, y, speaker, text, container));
		// 等待一帧以暂停后续的剧情
		Waits.wait(container, 1L);
	}

	private Image resolveSpeaker(Clip clip) throws ClassNotFoundException {
		if (speakerClassIndex == 0) {
			return null;
		}
		return ImageLoader.load(Class.forName(clip.getUTF8Const(speakerClassIndex)));
	}

	private Control createInstance(String dialogClass, int x, int y, Image speaker, String text,
		Container container) throws Exception {
		return getIIISCConstructot(dialogClass).newInstance(x, y, speaker, text, container);
	}

	/**
	 * 获取 dialogClass 对应类型的 int，int，Image，String，Container 参数的构造方法
	 * @param dialogClass
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private Constructor<Control> getIIISCConstructot(String dialogClass) throws NoSuchMethodException,
		SecurityException, ClassNotFoundException {
		Class<?> clazz = Class.forName(dialogClass);
		for (Constructor<?> c : clazz.getConstructors()) {
			if (isIIISC(c.getGenericParameterTypes())) {
				return (Constructor<Control>) c;
			}
		}
		throw new NoSuchMethodException();
	}

	private static boolean isIIISC(Type[] parameters) {
		if (IIISC.length != parameters.length) {
			return false;
		}
		for (int i = 0; i < IIISC.length; i++) {
			if (IIISC[i].isAssignableFrom(parameters[i].getClass())) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void readOperands(ByteReader reader) throws IOException {
		characteClassIndex = reader.readUint16();
		xRateIndex = reader.readUint16();
		yRateIndex = reader.readUint16();
		speakerClassIndex = reader.readUint16();
		textIndex = reader.readUint16();
	}

}
