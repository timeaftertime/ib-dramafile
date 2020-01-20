package cn.milai.ib.compiler;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

	/**
	 * Infinity Battle Drama
	 */
	private static final String INPUT_EXT = ".ibd";

	private static final String OUTPUT_EXT = ".drama";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length < 1) {
			System.err.println("请指定输入文件");
			return;
		}
		String inputFile = args[0].trim();
		if (!isIBDFile(inputFile)) {
			System.err.println("输入文件必须为 " + INPUT_EXT + " 文件");
			return;
		}
		String outputFile = getOutputFileName(inputFile);
		byte[] bytes = SimpleCompiler.compile(new FileInputStream(inputFile));
		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile));
		output.write(bytes);
		output.close();
	}

	private static boolean isIBDFile(String fileName) {
		return fileName.endsWith(INPUT_EXT);
	}

	private static String getOutputFileName(String inputFileName) {
		return inputFileName.replace(INPUT_EXT, OUTPUT_EXT);
	}

}
