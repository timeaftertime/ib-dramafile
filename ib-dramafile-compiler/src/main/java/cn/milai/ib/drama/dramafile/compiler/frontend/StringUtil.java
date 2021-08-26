package cn.milai.ib.drama.dramafile.compiler.frontend;

/**
 * 字符串工具类
 * @author milai
 * @date 2021.08.23
 */
public class StringUtil {

	private StringUtil() {
	}

	/**
	 * 将指定字符串转换为 {@link Character} 数组
	 * @param s
	 * @return
	 */
	public static Character[] toCharacterArray(String s) {
		char[] chs = s.toCharArray();
		Character[] res = new Character[chs.length];
		for (int i = 0; i < chs.length; i++) {
			res[i] = chs[i];
		}
		return res;
	}
}
