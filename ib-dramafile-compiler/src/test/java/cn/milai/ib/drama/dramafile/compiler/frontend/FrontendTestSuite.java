package cn.milai.ib.drama.dramafile.compiler.frontend;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import cn.milai.ib.drama.dramafile.compiler.frontend.lex.LexTestSuite;
import cn.milai.ib.drama.dramafile.compiler.frontend.parsing.ParsingTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ LexTestSuite.class, ParsingTestSuite.class, StringUtilTest.class })
public class FrontendTestSuite {

}
