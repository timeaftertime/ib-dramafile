package cn.milai.ib.compiler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import cn.milai.ib.compiler.frontend.FrontendTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ SimpleCompilerTest.class, FrontendTestSuite.class })
public class IBCompilerTestSuite {

}
