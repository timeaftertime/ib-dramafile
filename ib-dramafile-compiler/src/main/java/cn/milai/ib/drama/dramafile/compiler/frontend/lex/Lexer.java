package cn.milai.ib.drama.dramafile.compiler.frontend.lex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cn.milai.ib.drama.dramafile.compiler.ex.IBCompilerException;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.Token;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.TokenScanner;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.TokenType;

/**
 * 词法解析器
 * @author milai
 * @date 2020.02.11
 */
public class Lexer {

	private DFAStatus start;

	public Lexer(Set<TokenDefinition> tokens) {
		start = DFABuilder.newDFA(NFABuilder.newNFA(tokens));
	}

	/**
	 * 将字符串序列解析为 Token 序列
	 * @param scanner
	 * @return
	 */
	public TokenScanner lex(CharScanner scanner) {
		List<Token> tokens = new ArrayList<>();
		while (scanner.hasMore()) {
			tokens.add(nextToken(scanner));
		}
		return new TokenScanner(tokens);
	}

	public Token nextToken(CharScanner scanner) {
		DFAStatus now = start;
		Stack<Token> accepted = new Stack<>();
		StringBuilder lexeme = new StringBuilder();
		while (scanner.hasMore()) {
			DFAStatus next = now.next(scanner.now());
			if (next == null) {
				if (accepted.isEmpty()) {
					throw new IBCompilerException(String.format("匹配失败，已经输入字符：%s", lexeme.toString()));
				}
				return rollbacklastAcceptedToken(scanner, lexeme.toString(), accepted);
			}
			lexeme.append(scanner.now());
			if (next.isAccept()) {
				accepted.push(createToken(lexeme.toString(), next));
			}
			now = next;
			scanner.next();
		}
		return rollbacklastAcceptedToken(scanner, lexeme.toString(), accepted);
	}

	/**
	 * 回滚到最后的接受状态，并返回当时匹配的 Token
	 * @param lexeme 
	 * @param scanner 
	 * @param accepted
	 * @return
	 */
	private static Token rollbacklastAcceptedToken(CharScanner scanner, String lexeme, Stack<Token> accepted) {
		if (accepted.isEmpty()) {
			throw new IBCompilerException("匹配失败");
		}
		Token token = accepted.pop();
		scanner.seek(token.getOrigin().length() - lexeme.length());
		return token;
	}

	/**
	 * 获取 lexeme 在 status 状态匹配到的 Token
	 * @param lexeme
	 * @param status
	 * @return
	 */
	private static Token createToken(String lexeme, DFAStatus status) {
		Set<String> tokens = status.tokens();
		if (tokens.size() < 2) {
			String token = tokens.toArray(new String[1])[0];
			return new Token(lexeme, TokenType.of(token));
		}
		List<TokenType> types = tokens.stream()
			.map(TokenType::of)
			.filter(t -> Pattern.matches(t.getRE(), lexeme))
			.collect(Collectors.toList());
		if (types.isEmpty()) {
			throw new IBCompilerException(String.format("未知错误，找不到 %s 匹配的 Token 类型", lexeme));
		}
		if (types.size() > 1) {
			Collections.sort(
				types, (t1, t2) -> {
					return t1.getOrder() - t2.getOrder();
				}
			);
		}
		return new Token(lexeme, types.get(0));
	}

}
