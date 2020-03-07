package cn.milai.ib.drama.dramafile.compiler.backend.syntax;

import cn.milai.ib.drama.dramafile.compiler.backend.ValueType;

/**
 * 代表一个值的语法树
 * @author milai
 * @date 2020.03.03
 */
public interface ValueSyntaxTree extends SyntaxTree {

	ValueType getType();
}
