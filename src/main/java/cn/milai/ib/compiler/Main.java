package cn.milai.ib.compiler;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length < 1) {
			System.err.println("请指定输入文件");
			return;
		}
		String inputFile = args[0];
		String outputFile = "ib.drama";
		if (args.length > 1) {
			outputFile = args[1];
		}
		byte[] bytes = SimpleCompiler.compile(new FileInputStream(inputFile));
		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile));
		output.write(bytes);
		output.close();
	}
}
