package cn.milai.ib.compiler;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;

import org.junit.Test;

public class SimpleCompilerTest {

	private final byte[] testSimpleCompilerTxt = {
			// new
			0x1,
			// cn.milai.ib.obj.character.plane.WelcomePlane
			0x0, 0x2c, 0x63, 0x6e, 0x2e, 0x6d, 0x69, 0x6c, 0x61, 0x69, 0x2e, 0x69, 0x62, 0x2e, 0x6f, 0x62, 0x6a, 0x2e, 0x63, 0x68, 0x61, 0x72, 0x61,
			0x63, 0x74, 0x65, 0x72, 0x2e, 0x70, 0x6c, 0x61, 0x6e, 0x65, 0x2e, 0x57, 0x65, 0x6c, 0x63, 0x6f, 0x6d, 0x65, 0x50, 0x6c, 0x61, 0x6e, 0x65,
			// 0.5
			0x3f, 0x0, 0x0, 0x0,
			// -0.2
			-0x42, 0x4c, -0x34, -0x33,
			// new
			0x1,
			// cn.milai.ib.obj.character.plane.WelcomePlane
			0x0, 0x2c, 0x63, 0x6e, 0x2e, 0x6d, 0x69, 0x6c, 0x61, 0x69, 0x2e, 0x69, 0x62, 0x2e, 0x6f, 0x62, 0x6a, 0x2e, 0x63, 0x68, 0x61, 0x72, 0x61,
			0x63, 0x74, 0x65, 0x72, 0x2e, 0x70, 0x6c, 0x61, 0x6e, 0x65, 0x2e, 0x57, 0x65, 0x6c, 0x63, 0x6f, 0x6d, 0x65, 0x50, 0x6c, 0x61, 0x6e, 0x65,
			// 0.4
			0x3e, -0x34, -0x34, -0x33,
			// 0
			0x0, 0x0, 0x0, 0x0,
			// sleep
			0x2,
			// 10
			0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xa, };

	@Test
	public void testCompiler() throws IOException {
		byte[] bytes = SimpleCompiler.compile(SimpleCompilerTest.class.getResource("/testSimpleCompiler.txt").openStream());
		assertArrayEquals(testSimpleCompilerTxt, bytes);
	}
}
