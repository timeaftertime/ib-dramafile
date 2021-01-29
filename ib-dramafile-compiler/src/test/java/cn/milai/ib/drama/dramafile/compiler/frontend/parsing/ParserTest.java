package cn.milai.ib.drama.dramafile.compiler.frontend.parsing;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import cn.milai.ib.drama.dramafile.compiler.frontend.lex.CharInput;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.Lexer;
import cn.milai.ib.drama.dramafile.compiler.frontend.lex.TokenDef;

/**
 * 解析器测试类
 * @author milai
 * @date 2020.02.25
 */
public class ParserTest {

	private Lexer lexer = new Lexer(
		Arrays.stream(TokenType.values())
			.map(t -> new TokenDef(t.getRE(), t.getCode()))
			.collect(Collectors.toSet())
	);

	@Test
	public void testParseIf() {
		/**
		 *                                          CFG
		 *                                             ↓
		 *                                           Stmd
		 *    +----------------------- +---------------------+
		 *     ↓             ↓           ↓          ↓          ↓           ↓          ↓
		 *     IF            (         Expr       )          {         Stmd      }
		 *                                 ↓                                   ↓
		 *                                ID                  +--------+---------+
		 *                                                      ↓         ↓          ↓        ↓
		 *                                                      ID       (           )        ;
		 */
		String FILE = "/parsing/testParseIf.txt";
		Grammer grammer = GrammerReader.parseGrammer(ParserTest.class.getResourceAsStream(FILE));
		Node root = new Parser(grammer).parse(lexer.lex(new CharInput("if(isTest) {   doSomethingTest() ;   }")));
		List<Node> rootChildren = root.getChildren();
		assertEquals(1, rootChildren.size());
		Node cfg = rootChildren.get(0);
		List<Node> cfgChildren = cfg.getChildren();
		assertEquals(7, cfgChildren.size());
		Node expr = cfgChildren.get(2);
		Node stmd = cfgChildren.get(5);
		List<Node> exprChildren = expr.getChildren();
		assertEquals(1, exprChildren.size());
		Node id1 = exprChildren.get(0);
		assertEquals("isTest", id1.getToken().getOrigin());
		List<Node> stmdChildren = stmd.getChildren();
		assertEquals(4, stmdChildren.size());
		assertEquals("doSomethingTest", stmdChildren.get(0).getToken().getOrigin());
		assertEquals(TokenType.BRACKET_LEFT, stmdChildren.get(1).getToken().getType());
		assertEquals(TokenType.BRACKET_RIGHT, stmdChildren.get(2).getToken().getType());
		assertEquals(TokenType.STMD_END, stmdChildren.get(3).getToken().getType());
	}

	@Test
	public void testParseStmds() {
		/**
		 * 由于构造语法时中间非终结符的生成，符号名与实际有些不同，但结构是一样的
		 *                  CFG
		 *                    ↓
		 *                Stmds1
		 *   +---------+-----------------+
		 *   ↓                                             ↓
		 *   Stmd1                            Stmds2
		 *   +-----+---+--+---+             +------------------+
		 *   ↓         ↓     ↓     ↓     ↓              ↓                              ↓
		 *  while  (   Expr  )  Block         Stmd4                  Stmds6
		 *                     ↓           ↓            +-+-+-+                +--------------------------+
		 *                    ID  +---+            ↓  ↓   ↓  ↓               Stmd5                                 Stmds7
		 *                          ↓      ↓           ID  (    )   ;                 ↓                                        +----------+
		 *                          {  Stmds3                                      ;                                        Stmd6      Stmd'
		 *                                 +----------------+                           +-------------------+                ↓
		 *                              Stmds4                    ↓                          ↓      ↓      ↓       ↓      ↓                ϵ
		 *                        +------+-------+           }                          if     (     Expr    )    Block
		 *                    Stmd2           Stmds5                                                   ↓               ↓
		 *               +----+---+             +------------+                                     ID           Stmd
		 *                ↓    ↓   ↓    ↓          Stmd3           Stmd'                                                ↓
		 *               ID   (    )    ;    +----+------+         ↓                                                      ;
		 *                                      ↓     ↓      ↓    ↓         ϵ
		 *                                      ID    (      )     ;   
		 */
		String FILE = "/parsing/testParseStmds.txt";
		Grammer grammer = GrammerReader.parseGrammer(ParserTest.class.getResourceAsStream(FILE));
		Node root = new Parser(grammer).parse(
			lexer.lex(
				new CharInput(
					"while(isTest) {  test1();test2();   }    doSomething();  ; if(isProd) ;"
				)
			)
		);
		List<Node> rootChildren = root.getChildren();
		assertEquals(1, rootChildren.size());
		Node stmds1 = rootChildren.get(0);
		List<Node> stmds1Children = stmds1.getChildren();
		assertEquals(2, stmds1Children.size());
		{
			Node stmd1 = stmds1Children.get(0);
			List<Node> stmd1Children = stmd1.getChildren();
			assertEquals(5, stmd1Children.size());
			{
				Node expr = stmd1Children.get(2);
				assertEquals(1, expr.getChildren().size());
				assertEquals("isTest", expr.getChildren().get(0).getToken().getOrigin());
			}
			{
				Node block = stmd1Children.get(4);
				List<Node> blockChildren = block.getChildren();
				assertEquals(2, blockChildren.size());
				assertEquals(TokenType.BLOCK_LEFT, blockChildren.get(0).getToken().getType());
				{
					Node stmds3 = blockChildren.get(1);
					List<Node> stmds3Children = stmds3.getChildren();
					assertEquals(2, stmds3Children.size());
					{
						Node stmds4 = stmds3Children.get(0);
						List<Node> stmds4Children = stmds4.getChildren();
						assertEquals(2, stmds4Children.size());
						{
							Node stmd2 = stmds4Children.get(0);
							assertEquals(4, stmd2.getChildren().size());
							assertEquals("test1", stmd2.getChildren().get(0).getToken().getOrigin());
						}
						{
							Node stmds5 = stmds4Children.get(1);
							List<Node> stmds5Children = stmds5.getChildren();
							assertEquals(2, stmds5Children.size());
							{
								Node stmd3 = stmds5Children.get(0);
								assertEquals(4, stmd3.getChildren().size());
								assertEquals("test2", stmd3.getChildren().get(0).getToken().getOrigin());
							}
							assertEquals(0, stmds5Children.get(1).getChildren().size());
						}
					}
					assertEquals(TokenType.BLOCK_RIGHT, stmds3Children.get(1).getToken().getType());
				}
			}
		}
		{
			Node stmds2 = stmds1Children.get(1);
			List<Node> stmds2Children = stmds2.getChildren();
			assertEquals(2, stmds2Children.size());
			{
				Node stmd4 = stmds2Children.get(0);
				assertEquals(4, stmd4.getChildren().size());
				assertEquals("doSomething", stmd4.getChildren().get(0).getToken().getOrigin());
			}
			{
				Node stmds6 = stmds2Children.get(1);
				List<Node> stmds6Children = stmds6.getChildren();
				assertEquals(2, stmds6Children.size());
				{
					Node stmd5 = stmds6Children.get(0);
					assertEquals(1, stmd5.getChildren().size());
					assertEquals(TokenType.STMD_END, stmd5.getChildren().get(0).getToken().getType());
				}
				{
					Node stmd7 = stmds6Children.get(1);
					List<Node> stmd7Children = stmd7.getChildren();
					assertEquals(2, stmd7Children.size());
					{
						Node stmd6 = stmd7Children.get(0);
						List<Node> stmd6Children = stmd6.getChildren();
						assertEquals(5, stmd6Children.size());
						{
							Node expr = stmd6Children.get(2);
							assertEquals(1, expr.getChildren().size());
							assertEquals("isProd", expr.getChildren().get(0).getToken().getOrigin());
						}
						{
							Node block = stmd6Children.get(4);
							assertEquals(1, block.getChildren().size());
							assertEquals(1, block.getChildren().get(0).getChildren().size());
						}
					}
					assertEquals(0, stmd7Children.get(1).getChildren().size());
				}
			}
		}
	}

}
