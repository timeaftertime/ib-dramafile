package cn.milai.ib.compiler;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

	private static final String OUTPUT_EXT = ".drama";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length < 1) {
			System.err.println("请指定输入文件");
			return;
		}
		String inputFile = args[0].trim();
		String outputFile = getOutputFileName(args);
		byte[] bytes = SimpleCompiler.compile(new FileInputStream(inputFile));
		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile));
		output.write(bytes);
		output.close();
	}

	private static String getOutputFileName(String[] args) {
		if (args.length >= 2) {
			return args[1].endsWith(OUTPUT_EXT) ? args[1] : args[1] + OUTPUT_EXT;
		}
		return "ib" + OUTPUT_EXT;
	}

}
