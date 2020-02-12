package cn.milai.ib.compiler.frontend.lex;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import cn.milai.ib.compiler.ex.IBCompilerException;
import cn.milai.ib.compiler.frontend.parsing.Token;
import cn.milai.ib.compiler.frontend.parsing.TokenInput;
import cn.milai.ib.compiler.frontend.parsing.TokenType;

/**
 * 词法解析器
 * @author milai
 * @date 2020.02.11
 */
public class Lexer {

	private DFAStatus start;

	public Lexer(Set<LexToken> tokens) {
		start = DFABuilder.newDFA(NFABuilder.newNFA(tokens));
	}

	/**
	 * 将字符串序列解析为 Token 序列
	 * @param input
	 * @return
	 */
	public TokenInput lex(CharInput input) {
		List<Token> tokens = Lists.newArrayList();
		while (input.hasNext()) {
			tokens.add(nextToken(input));
		}
		return new TokenInput(tokens.toArray(new Token[0]));
	}

	public Token nextToken(CharInput input) {
		DFAStatus now = start;
		Stack<Token> accepted = new Stack<>();
		StringBuilder lexeme = new StringBuilder();
		while (input.hasNext()) {
			DFAStatus next = now.next(input.getNext());
			if (next == null) {
				if (accepted.isEmpty()) {
					throw new IBCompilerException(String.format("匹配失败，已经输入字符：%s", lexeme.toString()));
				}
				return rollbacklastAcceptedToken(input, lexeme.toString(), accepted);
			}
			lexeme.append(input.getNext());
			if (next.isAccept()) {
				accepted.push(createToken(lexeme.toString(), next));
			}
			now = next;
			input.next();
		}
		return rollbacklastAcceptedToken(input, lexeme.toString(), accepted);
	}

	/**
	 * 回滚到最后的接受状态，并返回当时匹配的 Token
	 * @param lexeme 
	 * @param input 
	 * @param accepted
	 * @return
	 */
	private static Token rollbacklastAcceptedToken(CharInput input, String lexeme, Stack<Token> accepted) {
		if (accepted.isEmpty()) {
			throw new IBCompilerException("匹配失败");
		}
		Token token = accepted.pop();
		input.seek(token.getOrigin().length() - lexeme.length());
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
			Collections.sort(types, (t1, t2) -> {
				return t1.getOrder() - t2.getOrder();
			});
		}
		return new Token(lexeme, types.get(0));
	}

}
