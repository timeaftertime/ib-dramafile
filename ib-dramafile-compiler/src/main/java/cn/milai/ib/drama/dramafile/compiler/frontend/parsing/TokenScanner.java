package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

import java.util.Collection;

import cn.milai.ib.drama.dramafile.compiler.frontend.Scanner;

/**
 * {@link Token} 的 {@link Scanner}
 * @author milai
 */
public class TokenScanner extends Scanner<Token> {

	public TokenScanner(Collection<Token> tokens) {
		super(tokens);
	}

}
