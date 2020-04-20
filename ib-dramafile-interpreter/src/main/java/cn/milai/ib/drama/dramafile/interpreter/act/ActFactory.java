package cn.milai.ib.drama.dramafile.interpreter.act;

import cn.milai.ib.drama.dramafile.act.ActType;
import cn.milai.ib.drama.dramafile.interpreter.act.ex.ActNotExistsException;

/**
 * 动作工厂类
 * 2019.12.14
 * @author milai
 */
public class ActFactory {

	/**
	 * 构造 code 对应的 Act
	 * @param code
	 */
	public static Act create(int code) {
		switch (ActType.findByCode(code)) {
			case ADD : {
				return new AddAct();
			}
			case SLEEP : {
				return new SleepAct();
			}
			case DIALOG : {
				return new DialogAct();
			}
			case BGM : {
				return new BGMAct();
			}
			case BGI : {
				return new BGIAct();
			}
			case IMG : {
				return new IMGAct();
			}
			case AUDIO : {
				return new AudioAct();
			}
			case LDC : {
				return new LDCAct();
			}
			default: {
				throw new ActNotExistsException(code);
			}
		}
	}

}
