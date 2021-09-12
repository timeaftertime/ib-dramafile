package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import cn.milai.beginning.collection.Filter;
import cn.milai.beginning.collection.Merge;
import cn.milai.ib.drama.dramafile.compiler.ex.IBCompilerException;

/**
 * 语法分析的语法
 * @author milai
 * @date 2020.02.14
 */
public class Grammer {

	/**
	 * 语法开始符号
	 */
	public static final String CFG_CODE = "CFG";

	private NonTerminalSymbol startSymbol;
	private List<NonTerminalSymbol> nonTerminals;
	private List<TerminalSymbol> terminals;

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
		this.nonTerminals = new ArrayList<>(nonTerminals);
		this.terminals = new ArrayList<>(terminals);
		initStartSymbol();
		eliminateLeftRecursion();
		extractCommonLefts();
		buildSets();
		// TODO 校验语法合法性
	}

	/**
	 * 提取公共左因子式
	 */
	private void extractCommonLefts() {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (NonTerminalSymbol nonTerminal : getNonTerminals()) {
				List<List<Production>> hasCommons = nonTerminal.getProductions().stream()
					.filter(p -> !p.isEpsilon())
					.collect(Collectors.groupingBy(p -> p.getRights().get(0)))
					.values().stream()
					.filter(p -> p.size() >= 2)
					.collect(Collectors.toList());
				for (List<Production> productions : hasCommons) {
					List<Symbol> prefix = maxCommonPrefixOf(productions);
					NonTerminalSymbol newNonTerminal = newNonTerminal(nonTerminal.getCode());
					List<Symbol> rights = new ArrayList<>(prefix);
					rights.add(newNonTerminal);
					nonTerminal.addProduction(rights);
					for (Production p : productions) {
						nonTerminal.removeProduction(p);
						if (prefix.size() == p.getRights().size()) {
							newNonTerminal.addEpsilonProduction();
						} else {
							newNonTerminal.addProduction(p.getRights().subList(prefix.size(), p.getRights().size()));
						}
					}
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
		int minSize = productions.stream()
			.map(Production::getRights)
			.map(List::size)
			.min((i1, i2) -> i1 - i2)
			.get();
		for (int i = 0; i < minSize; i++) {
			Symbol s = productions.get(0).getRights().get(i);
			for (int j = 1; j < productions.size(); j++) {
				if (productions.get(j).getRights().get(i) != s) {
					return prefix;
				}
			}
			prefix.add(s);
		}
		return prefix;
	}

	private void eliminateLeftRecursion() {
		convertIndirectToDirect();
		eliminateDirect();
	}

	/**
	 * 消除直接左递归
	 * @throws IllegalStateException
	 */
	private void eliminateDirect() {
		List<NonTerminalSymbol> symbols = getNonTerminals();
		for (NonTerminalSymbol now : symbols) {
			Production notCursion = null;
			for (Production p : now.getProductions()) {
				if (p.getRights().get(0) != now) {
					// 找到任何一个非左递归产生式
					notCursion = p;
					break;
				}
			}
			if (notCursion == null) {
				throw new IllegalStateException(String.format("找不到非左递归的产生式：symbol = %s", now));
			}
			List<Production> leftCursions = new ArrayList<>();
			for (Production p : now.getProductions()) {
				if (p.getRights().get(0) == now) {
					leftCursions.add(p);
				}
			}
			if (!leftCursions.isEmpty()) {
				now.removeProduction(notCursion);
				NonTerminalSymbol newSymbol = newNonTerminal(now.getCode());
				{
					List<Symbol> newRights = new ArrayList<>();
					if (!notCursion.isEpsilon()) {
						newRights.addAll(notCursion.getRights());
					}
					newRights.add(newSymbol);
					now.addProduction(newRights);
				}
				newSymbol.addEpsilonProduction();
				for (Production p : leftCursions) {
					now.removeProduction(p);
					{
						List<Symbol> newRights = new ArrayList<>();
						newRights.addAll(p.getRights().subList(1, p.getRights().size()));
						newRights.add(newSymbol);
						newSymbol.addProduction(newRights);
					}
				}
			}
		}
	}

	/**
	 * 间接左递归转换为直接左递归
	 */
	private void convertIndirectToDirect() {
		List<NonTerminalSymbol> symbols = getNonTerminals();
		for (int i = 0; i < symbols.size(); i++) {
			NonTerminalSymbol s1 = symbols.get(i);
			for (int j = 0; j < i; j++) {
				NonTerminalSymbol s2 = symbols.get(j);
				for (Production p1 : s1.getProductions()) {
					List<Symbol> rights = p1.getRights();
					// 若存在一个推导式 s1 -> s2X，其中 X 为任意符号串（可空）
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
		for (NonTerminalSymbol symbol : getNonTerminals()) {
			if (symbol.getCode().equals(CFG_CODE)) {
				startSymbol = symbol;
				for (NonTerminalSymbol s : getNonTerminals()) {
					for (Production p : s.getProductions()) {
						if (p.getRights().contains(startSymbol)) {
							throw new IBCompilerException("语法开始符号不允许出现在产生式右式：" + p);
						}
					}
				}
				return;
			}
		}
		throw new IBCompilerException("找不到语法开始符号，请确认语言定义是否以 " + CFG_CODE + " 开始");
	}

	private void buildSets() {
		buildFirsts();
		buildFollows();
		buildSelects();
	}

	private void buildSelects() {
		for (NonTerminalSymbol symbol : getNonTerminals()) {
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
		getNonTerminals().forEach(s -> follows.put(s, new HashSet<>()));
		follows.get(startSymbol).add(Symbol.EOF);
		boolean changed = true;
		while (changed) {
			changed = false;
			for (NonTerminalSymbol left : getNonTerminals()) {
				for (Production p : left.getProductions()) {
					Set<Symbol> tails = getFollow(left);
					if (p.isEpsilon()) {
						continue;
					}
					List<Symbol> rights = p.getRights();
					for (int i = rights.size() - 1; i >= 0; i--) {
						Symbol now = rights.get(i);
						if (!now.isNonTerminal()) {
							// 由于忽略了空产生式，这里的 now 一定是终结符
							tails = getFirst(now);
							continue;
						}
						changed |= follows.get(now).addAll(tails);
						if (firsts.get(now).contains(Symbol.EPSILON)) {
							tails.addAll(Filter.nset(firsts.get(now), Symbol::isEpsilon));
						} else {
							tails = getFirst(now);
						}
					}
				}
			}
		}
	}

	private void buildFirsts() {
		getNonTerminals().forEach(s -> firsts.put(s, new HashSet<>()));
		getTerminals().forEach(s -> firsts.put(s, new HashSet<>(Arrays.asList(s))));
		firsts.put(Symbol.EOF, new HashSet<>(Arrays.asList(Symbol.EOF)));
		firsts.put(Symbol.EPSILON, new HashSet<>(Arrays.asList(Symbol.EPSILON)));
		boolean changed = true;
		while (changed) {
			changed = false;
			for (NonTerminalSymbol left : getNonTerminals()) {
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
		return symbol;
	}

	public NonTerminalSymbol getStartSymbol() { return startSymbol; }

	public List<NonTerminalSymbol> getNonTerminals() { return new ArrayList<>(nonTerminals); }

	public List<TerminalSymbol> getTerminals() { return new ArrayList<>(terminals); }

	/**
	 * 获取指定 code 对应符号的 FIRST 集合
	 * @param code
	 * @return
	 */
	public Set<Symbol> getFirst(String code) {
		return getFirst(findSymbol(code));
	}

	/**
	 * 获取指定 code 对应符号的 FOLLOW 集合
	 * @param code
	 * @return
	 */
	public Set<Symbol> getFollow(String code) {
		return getFollow(findSymbol(code));
	}

	/**
	 * 获取指定 code 对应符号的 FIRST 集合
	 * @param code
	 * @return
	 */
	private Set<Symbol> getFirst(Symbol s) {
		return new HashSet<>(firsts.get(s));
	}

	/**
	 * 获取指定 code 对应符号的 FOLLOW 集合
	 * @param code
	 * @return
	 */
	private Set<Symbol> getFollow(Symbol s) {
		return new HashSet<>(follows.get(s));
	}

	public Set<Symbol> getSelect(Production p) {
		return selects.get(p);
	}

	/**
	 * 获取 code 对应的符号实例
	 * @param code
	 * @return
	 * @throws IBCompilerException
	 */
	public Symbol findSymbol(String code) throws IBCompilerException {
		for (Symbol s : getSymbols()) {
			if (s.getCode().equals(code)) {
				return s;
			}
		}
		throw new IBCompilerException(String.format("符号 %s 不存在", code));
	}

	private List<Symbol> getSymbols() { return Merge.list(getNonTerminals(), getTerminals()); }

	public static class Builder {

		private Map<String, NonTerminalSymbol> nonTerminals = new HashMap<>();

		private Map<String, TerminalSymbol> terminals = new HashMap<>();

		/**
		 * 新建并保存的或获取已经存在的、 code 为指定值的非终结符
		 * @param code
		 * @return
		 */
		private NonTerminalSymbol nonTerminalOf(String code) {
			if (StringUtils.isEmpty(code)) {
				throw new IllegalArgumentException("code 不能为空");
			}
			if (Symbol.EPSILON.getCode().equals(code)) {
				throw new IBCompilerException(code + " 不能作为非终结符");
			}
			if (TokenType.findByCode(code) != null) {
				throw new IBCompilerException(String.format("%s 已定义为终结符，不能作为非终结符", code));
			}
			if (!nonTerminals.containsKey(code)) {
				nonTerminals.put(code, new NonTerminalSymbol(code));
			}
			return nonTerminals.get(code);
		}

		/**
		 * 新建并保存的或获取已经存在的、 code 为指定值的终结符
		 * @param code
		 * @return
		 */
		private TerminalSymbol terminalOf(String code) {
			if (StringUtils.isEmpty(code)) {
				throw new IllegalArgumentException("code 不能为空");
			}
			if (Symbol.EPSILON.getCode().equals(code)) {
				throw new IBCompilerException(code + "不能作为终结符");
			}
			if (TokenType.findByCode(code) == null) {
				throw new IBCompilerException(String.format("%s 不是合法的终结符", code));
			}
			if (!terminals.containsKey(code)) {
				terminals.put(code, new TerminalSymbol(code));
			}
			return terminals.get(code);
		}

		/**
		 * 创建或获取已存在的 code 对应的符号实例
		 * @param code
		 * @return
		 */
		private Symbol symbolOf(String code) {
			if (TokenType.findByCode(code) != null) {
				return terminalOf(code);
			}
			return nonTerminalOf(code);
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
			left.addProduction(Arrays.stream(rightCodes).map(this::symbolOf).collect(Collectors.toList()));
		}

		public void checkSymbolCode(String code) {
			for (char ch : code.toCharArray()) {
				if (!CharUtils.isAsciiAlpha(ch) && ch != '_') {
					throw new IllegalArgumentException("语法定义中的符号只允许使用字母和下划线：" + code);
				}
			}
		}

		public Grammer build() {
			return new Grammer(nonTerminals.values(), terminals.values());
		}
	}

}
