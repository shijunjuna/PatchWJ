package count;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JavaCodeLines {
	private static final String PROJECT_DIR = "E:\\WorkSpace2015_9_23_Wala_Mars\\WALA";
	private static int fileNum = 0;
	private static int lineNum = 0;

	private static void listNext(File dir) {
		File[] files = dir.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				listNext(files[i]);
			} else {
				// System.out.println(fs[i].getAbsolutePath());
				try {
					if (files[i].getName().endsWith(".java")) {
						fileNum++;
						BufferedReader br = new BufferedReader(new FileReader(files[i]));
						while (br.readLine() != null) {
							lineNum++;
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		File root = new File(PROJECT_DIR);
		listNext(root);
		System.out.println("Java files number: " + fileNum);
		System.out.println("Java code lines: " + lineNum);
	}

}
