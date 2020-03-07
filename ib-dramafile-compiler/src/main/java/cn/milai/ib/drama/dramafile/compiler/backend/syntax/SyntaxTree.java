package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import java.util.Map;

import cn.milai.ib.drama.dramafile.compiler.ConstantTable;

/**
 * 可以转换为字节数组的语法树
 * @author milai
 * @date 2020.03.01
 */
public interface SyntaxTree {

	/**
	 * 使用给定别名表和常量表将当前语法树转换为字节数组
	 * @param table
	 * @return
	 */
	byte[] toBytes(Map<String, String> alias, ConstantTable table);

}
