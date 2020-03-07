package cn.milai.ib.compiler.frontend.lex;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { NFABuilderTest.class, DFABuilderTest.class, LexerTest.class })
public class LexTestSuite {

}
