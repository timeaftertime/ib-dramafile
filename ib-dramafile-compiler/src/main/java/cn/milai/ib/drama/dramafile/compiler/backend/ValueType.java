package cn.milai.ib.drama.dramafile.compiler.backend;

import java.util.Map;

import com.google.common.collect.Maps;

import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.TokenType;

/**
 * 值（表达式、常量、变量等）的类型
 * @author milai
 * @date 2020.03.03
 */
public class ValueType {

	private String name;
	private String canonical;

	private static Map<String, ValueType> types = Maps.newHashMap();

	public static final ValueType VOID = of("void");
	public static final ValueType INT = of("int");
	public static final ValueType FLOAT = of("float");
	public static final ValueType STR = of("str");
	public static final ValueType IMG = of("img");
	public static final ValueType AUDIO = of("audio");

	private ValueType() {
	}

	/**
	 * 获取类型名
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	/**
	 * 获取最简表示的字符串
	 * @return
	 */
	public String getCanonical() {
		return canonical;
	}

	/**
	 * 获取指定 Token 类型对应的值类型
	 * @param type
	 * @return
	 */
	public static ValueType ofToken(TokenType type) {
		switch (type) {
			case INT :
			case FLOAT :
			case STR :
				return of(type.getCode());
			default:
				throw new IllegalArgumentException("不支持的 Token 类型：" + type);
		}
	}

	public static ValueType of(String typeName) {
		if (types.containsKey(typeName)) {
			return types.get(typeName);
		}
		return types.computeIfAbsent(typeName, name -> {
			String canonical;
			switch (name) {
				case "void" : {
					canonical = "V";
					break;
				}
				case "int" : {
					canonical = "I";
					break;
				}
				case "float" : {
					canonical = "F";
					break;
				}
				case "str" : {
					canonical = "S";
					break;
				}
				case "img" : {
					canonical = "P";
					break;
				}
				case "audio" : {
					canonical = "A";
					break;
				}
				default: {
					throw new IllegalArgumentException("暂不支持的类型：" + name);
				}
			}
			ValueType valueType = new ValueType();
			valueType.name = name;
			valueType.canonical = canonical;
			return valueType;
		});
	}
}
