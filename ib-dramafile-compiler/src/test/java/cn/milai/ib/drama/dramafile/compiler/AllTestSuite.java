package cn.milai.ib.drama.dramafile.compiler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import cn.milai.ib.drama.dramafile.compiler.frontend.FrontendTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ IBCompilerTest.class, SimpleCompilerTest.class, FrontendTestSuite.class })
public class AllTestSuite {

}
