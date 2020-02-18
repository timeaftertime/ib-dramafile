package cn.milai.ib.compiler.frontend;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import cn.milai.ib.compiler.frontend.lex.LexTestSuite;
import cn.milai.ib.compiler.frontend.parsing.ParsingTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ LexTestSuite.class, ParsingTestSuite.class })
public class FrontendTestSuite {

}
