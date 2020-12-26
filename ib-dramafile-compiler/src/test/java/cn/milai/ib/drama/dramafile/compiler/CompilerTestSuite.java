package cn.milai.ib.drama.dramafile.compiler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import cn.milai.ib.drama.dramafile.compiler.constant.ByteUtilsTest;
import cn.milai.ib.drama.dramafile.compiler.frontend.FrontendTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ ByteUtilsTest.class, SimpleCompilerTest.class, FrontendTestSuite.class })
public class CompilerTestSuite {

}
