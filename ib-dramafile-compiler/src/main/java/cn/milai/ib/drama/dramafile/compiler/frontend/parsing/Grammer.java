package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.Assert;

import cn.milai.common.base.Chars;
import cn.milai.common.collection.Creator;
import cn.milai.common.collection.Filter;
import cn.milai.common.collection.Mapping;
import cn.milai.ib.drama.dramafile.compiler.ex.IBCompilerException;

/**
 * 语法分析的语法
 * @author milai
 * @date 2020.02.14
 */
public class Grammer {

	public static final char UNDERLINE = '_';

	private NonTerminalSymbol startSymbol;
	private Set<NonTerminalSymbol> nonTerminals;
	private Set<TerminalSymbol> terminals;

	private Map<String, Symbol> codeToSymbols = new HashMap<>();

	/**
	 * 非终结符的 FOLLOW 集合，可以为 [ 终结符、EOF ]
	 */
	private Map<NonTerminalSymbol, Set<Symbol>> follows = new HashMap<>();

	/**
	 * [ 终结符、非终结符、EOF、EPSILON ] 的 FIRST 集合，可以为 [ 终结符、EOF、EPSILON ]
	 */
	private Map<Symbol, Set<Symbol>> firsts = new HashMap<>();

	/**
	 * 展开式的 SELECT 集合，即当待展开符号为该产生式左式，输入符号为哪些时可以选择当前产生式，可以为 [ 终结符、EPSILON ]
	 */
	private Map<Production, Set<Symbol>> selects = new HashMap<>();

	private Grammer(Collection<NonTerminalSymbol> nonTerminals, Collection<TerminalSymbol> terminals) {
		initSymbols(nonTerminals, terminals);
		eliminateLeftRecursion();
		extractCommonLeft();
		buildSets();
		// TODO 校验语法合法性
	}

	private void initSymbols(Collection<NonTerminalSymbol> nonTerminals, Collection<TerminalSymbol> terminals) {
		this.nonTerminals = new HashSet<>(nonTerminals);
		this.terminals = new HashSet<>(terminals);
		for (NonTerminalSymbol s : nonTerminals) {
			codeToSymbols.put(s.getCode(), s);
		}
		for (TerminalSymbol s : terminals) {
			codeToSymbols.put(s.getCode(), s);
		}
		initStartSymbol();
	}

	/**
	 * 提取公共左因子式
	 */
	private void extractCommonLeft() {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (NonTerminalSymbol nonTerminal : copyOfNonTerminals()) {
				Stream<Production> noEpsilons = Filter.stream(nonTerminal.getProductions(), p -> !p.isEpsilon());
				for (List<Production> hasCommon : Mapping.map(noEpsilons, p -> p.getRights().get(0)).values()) {
					if (hasCommon.size() <= 1) {
						continue;
					}
					List<Symbol> prefix = maxCommonPrefixOf(hasCommon);
					NonTerminalSymbol newNonTerminal = newNonTerminal(nonTerminal.getCode());
					List<Symbol> rights = new ArrayList<>(prefix);
					rights.add(newNonTerminal);
					nonTerminal.addProduction(rights);
					for (Production p : hasCommon) {
						nonTerminal.removeProduction(p);
						if (prefix.size() == p.getRights().size()) {
							newNonTerminal.addEpsilonProduction();
						} else {
							newNonTerminal.addProduction(p.getRights().subList(prefix.size(), p.getRights().size()));
						}
					}
					changed = true;
				}
			}
		}
	}

	/**
	 * 产生式右式的最长公共前缀
	 * @param productions
	 * @return
	 */
	private List<Symbol> maxCommonPrefixOf(List<Production> productions) {
		List<Symbol> prefix = new ArrayList<>();
		for (int i = 0;; i++) {
			Symbol s = symbolAt(productions.get(0), i);
			if (s == null) {
				return prefix;
			}
			for (int j = 1; j < productions.size(); j++) {
				if (symbolAt(productions.get(j), i) != s) {
					return prefix;
				}
			}
			prefix.add(s);
		}
	}

	private Symbol symbolAt(Production p, int rightIndex) {
		if (p.getRights().size() <= rightIndex) {
			return null;
		}
		return p.getRights().get(rightIndex);
	}

	private void eliminateLeftRecursion() {
		convertIndirectToDirect();
		eliminateDirect();
	}

	/**
	 * 消除直接左递归。
	 * A -> Aa1|Aa2|...|b1|b2
	 *           ↓
	 * A -> b1A'|b2A'
	 * A' -> a1A'|a2A'|...|ϵ
	 * @throws IllegalStateException
	 */
	private void eliminateDirect() {
		for (NonTerminalSymbol now : copyOfNonTerminals()) {
			List<Production> notCursions = new ArrayList<>();
			List<Production> leftCursions = new ArrayList<>();
			for (Production p : now.getProductions()) {
				if (p.isEpsilon() || p.getRights().get(0) != now) {
					notCursions.add(p);
				} else {
					leftCursions.add(p);
				}
			}
			if (!leftCursions.isEmpty()) {
				now.clearProductions();
				NonTerminalSymbol newSymbol = newNonTerminal(now.getCode());
				for (Production p : notCursions) {
					List<Symbol> newRights = new ArrayList<>(p.isEpsilon() ? Collections.emptyList() : p.getRights());
					newRights.add(newSymbol);
					now.addProduction(newRights);
				}
				for (Production p : leftCursions) {
					List<Symbol> newRights = new ArrayList<>();
					newRights.addAll(p.getRights().subList(1, p.getRights().size()));
					newRights.add(newSymbol);
					newSymbol.addProduction(newRights);
				}
				newSymbol.addEpsilonProduction();
			}
		}
	}

	/**
	 * 间接左递归转换为直接左递归
	 */
	private void convertIndirectToDirect() {
		List<NonTerminalSymbol> symbols = new ArrayList<>(nonTerminals);
		for (int i = 0; i < symbols.size(); i++) {
			NonTerminalSymbol s1 = symbols.get(i);
			for (int j = 0; j < i; j++) {
				NonTerminalSymbol s2 = symbols.get(j);
				for (Production p1 : s1.getProductions()) {
					List<Symbol> rights = p1.getRights();
					// 若存在一个推导式 s1 -> s2X，(X 为可空任意符号串)，则替换为 s1 -> ${s2所有推导式}X
					if (rights.get(0) == s2) {
						s1.removeProduction(p1);
						List<Symbol> p1Rights = rights.subList(1, rights.size());
						for (Production p2 : s2.getProductions()) {
							List<Symbol> newRights = new ArrayList<>();
							if (!p2.isEpsilon()) {
								newRights.addAll(p2.getRights());
							}
							newRights.addAll(p1Rights);
							s1.addProduction(newRights);
						}
					}
				}
			}
		}
	}

	/**
	 * 设置文法的开始符号
	 */
	private void initStartSymbol() {
		for (NonTerminalSymbol symbol : copyOfNonTerminals()) {
			if (symbol.getCode().equals(Keywords.CFG)) {
				startSymbol = symbol;
				for (NonTerminalSymbol s : copyOfNonTerminals()) {
					for (Production p : s.getProductions()) {
						if (p.getRights().contains(startSymbol)) {
							throw new IBCompilerException("语法开始符号不允许出现在产生式右式：" + p);
						}
					}
				}
				return;
			}
		}
		throw new IBCompilerException("找不到语法开始符号: " + Keywords.CFG);
	}

	private void buildSets() {
		buildFirsts();
		buildFollows();
		buildSelects();
	}

	private void buildSelects() {
		for (NonTerminalSymbol symbol : copyOfNonTerminals()) {
			for (Production p : symbol.getProductions()) {
				Set<Symbol> first = firstOf(p.getRights());
				if (first.contains(Symbol.EPSILON)) {
					first.addAll(follows.get(p.getLeft()));
				}
				selects.put(p, first);
			}
		}
	}

	private void buildFollows() {
		copyOfNonTerminals().forEach(s -> follows.put(s, new HashSet<>()));
		follows.get(startSymbol).add(Symbol.EOF);
		boolean changed = true;
		while (changed) {
			changed = false;
			for (NonTerminalSymbol left : copyOfNonTerminals()) {
				for (Production p : left.getProductions()) {
					Set<Symbol> tails = copyFollowOf(left);
					if (p.isEpsilon()) {
						continue;
					}
					List<Symbol> rights = p.getRights();
					for (int i = rights.size() - 1; i >= 0; i--) {
						Symbol now = rights.get(i);
						if (!now.isNonTerminal()) {
							// 由于忽略了空产生式，这里的 now 一定是终结符
							tails = copyFirstOf(now);
							continue;
						}
						changed |= follows.get(now).addAll(tails);
						if (firsts.get(now).contains(Symbol.EPSILON)) {
							tails.addAll(Filter.nset(firsts.get(now), Symbol::isEpsilon));
						} else {
							tails = copyFirstOf(now);
						}
					}
				}
			}
		}
	}

	private void buildFirsts() {
		nonTerminals.forEach(s -> firsts.put(s, Creator.hashSet()));
		terminals.forEach(s -> firsts.put(s, Creator.asSet(s)));
		firsts.put(Symbol.EOF, Creator.asSet(Symbol.EOF));
		firsts.put(Symbol.EPSILON, Creator.asSet(Symbol.EPSILON));
		boolean changed = true;
		while (changed) {
			changed = false;
			for (NonTerminalSymbol left : nonTerminals) {
				for (Production p : left.getProductions()) {
					changed |= firsts.get(left).addAll(firstOf(p.getRights()));
				}
			}
		}
	}

	private Set<Symbol> firstOf(List<Symbol> rights) {
		Set<Symbol> first = new HashSet<>();
		Symbol pre = Symbol.EPSILON;
		for (Symbol now : rights) {
			first.addAll(Filter.nset(firsts.get(now), Symbol::isEpsilon));
			if (!firsts.get(now).contains(Symbol.EPSILON)) {
				break;
			}
			pre = now;
		}
		if (pre == rights.get(rights.size() - 1)) {
			first.add(Symbol.EPSILON);
		}
		return first;
	}

	/**
	 * 以 code 为前缀，创建一个新的非终结符
	 * @param code
	 * @return
	 */
	private NonTerminalSymbol newNonTerminal(String code) {
		List<String> codes = nonTerminals.stream().map(Symbol::getCode).collect(Collectors.toList());
		while (codes.contains(code)) {
			code = code + "\'";
		}
		NonTerminalSymbol symbol = new NonTerminalSymbol(code);
		nonTerminals.add(symbol);
		codeToSymbols.put(code, symbol);
		return symbol;
	}

	public NonTerminalSymbol getStartSymbol() { return startSymbol; }

	private Set<NonTerminalSymbol> copyOfNonTerminals() {
		return new HashSet<>(nonTerminals);
	}

	public Set<NonTerminalSymbol> getNonTerminals() { return Collections.unmodifiableSet(nonTerminals); }

	/**
	 * 获取指定 code 对应符号的 FIRST 集合的不可变副本
	 * @param code
	 * @return
	 */
	public Set<Symbol> getFirst(String code) {
		return Collections.unmodifiableSet(firsts.get(symbolOf(code)));
	}

	/**
	 * 获取指定 code 对应符号的 FOLLOW 集合
	 * @param code
	 * @return
	 */
	public Set<Symbol> getFollow(String code) {
		return Collections.unmodifiableSet(follows.get(symbolOf(code)));
	}

	/**
	 * 获取指定 code 对应符号的 FIRST 集合的副本
	 * @param code
	 * @return
	 */
	private Set<Symbol> copyFirstOf(Symbol s) {
		return new HashSet<>(firsts.get(s));
	}

	/**
	 * 获取指定 code 对应符号的 FOLLOW 集合
	 * @param code
	 * @return
	 */
	private Set<Symbol> copyFollowOf(Symbol s) {
		return new HashSet<>(follows.get(s));
	}

	public Set<Symbol> getSelect(Production p) {
		return Collections.unmodifiableSet(selects.get(p));
	}

	/**
	 * 获取 code 对应的符号实例
	 * @param code
	 * @return
	 * @throws IBCompilerException
	 */
	public Symbol symbolOf(String code) throws IBCompilerException {
		return codeToSymbols.get(code);
	}

	public static class Builder {

		private Map<String, NonTerminalSymbol> nonTerminals = new HashMap<>();

		private Map<String, TerminalSymbol> terminals = new HashMap<>();

		/**
		 * 获取 code 为指定值的非终结符，若不存在，返回创建并保存的符号
		 * @param code
		 * @return
		 */
		private NonTerminalSymbol nonTerminalOf(String code) {
			Assert.hasLength(code, "code 不能为空");
			Assert.isTrue(!Symbol.isEpsilon(code), code + " 不能作为非终结符");
			Assert.isTrue(!isRegisteredTerminal(code), String.format("%s 已定义为终结符，不能作为非终结符", code));
			return nonTerminals.computeIfAbsent(code, c -> new NonTerminalSymbol(c));
		}

		/**
		 * 获取 code 为指定值的终结符，若不存在，返回创建并保存的符号
		 * @param code
		 * @return
		 */
		private TerminalSymbol terminalOf(String code) {
			Assert.hasLength(code, "code 不能为空");
			Assert.isTrue(!Symbol.isEpsilon(code), code + " 不能作为终结符");
			Assert.isTrue(isRegisteredTerminal(code), String.format("未定义终结符: %s", code));
			return terminals.computeIfAbsent(code, c -> new TerminalSymbol(c));
		}

		/**
		 * 创建或获取已存在的 code 对应的符号实例
		 * @param code
		 * @return
		 */
		private Symbol symbolOf(String code) {
			return isRegisteredTerminal(code) ? terminalOf(code) : nonTerminalOf(code);
		}

		/**
		 * 添加一个产生式
		 * @param leftCode 左非终结符的 code
		 * @param rightCodes 右产生式 code 的数组
		 */
		public void addProduction(String leftCode, String[] rightCodes) {
			checkSymbolCode(leftCode);
			NonTerminalSymbol left = nonTerminalOf(leftCode);
			if (rightCodes.length == 1 && rightCodes[0].equals(Symbol.EPSILON.getCode())) {
				left.addEpsilonProduction();
				return;
			}
			for (String code : rightCodes) {
				checkSymbolCode(code);
			}
			left.addProduction(Mapping.list(rightCodes, this::symbolOf));
		}

		public void checkSymbolCode(String code) {
			for (char ch : code.toCharArray()) {
				Assert.isTrue(Chars.isLetter(ch) || ch == UNDERLINE, "语法定义中的符号只允许使用字母和下划线: " + code);
			}
		}

		public Grammer build() {
			return new Grammer(nonTerminals.values(), terminals.values());
		}
	}

	private static boolean isRegisteredTerminal(String code) {
		return TokenType.findByCode(code) != null;
	}

}
