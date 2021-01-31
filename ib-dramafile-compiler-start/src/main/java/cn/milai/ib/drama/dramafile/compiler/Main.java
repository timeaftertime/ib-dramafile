package cn.milai.ib.drama.dramafile.compiler;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 命令行启动类
 * @author milai
 */
public class Main {

	private static final String INPUT_EXT = ".drama";
	private static final String OUTPUT_EXT = ".cdrama";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String inputFile = getInputFileName(args);
		String outputFile = getOutputFileName(inputFile);
		byte[] bytes = SimpleCompiler.compile(new FileInputStream(inputFile));
		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile));
		output.write(bytes);
		output.close();
	}

	private static String getInputFileName(String[] args) {
		if (args.length < 1) {
			throw new IllegalArgumentException("未指定输入文件");
		}
		String inputFileName = args[0];
		inputFileName = inputFileName.trim();
		if (!inputFileName.endsWith(INPUT_EXT)) {
			inputFileName = inputFileName + INPUT_EXT;
		}
		return inputFileName;
	}

	private static String getOutputFileName(String inputFileName) {
		String outputFileName = inputFileName.substring(0, inputFileName.length() - INPUT_EXT.length());
		return outputFileName + OUTPUT_EXT;
	}

}
