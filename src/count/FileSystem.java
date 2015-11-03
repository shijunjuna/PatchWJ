package count;

import java.io.File;

import java.util.ArrayList;

public class FileSystem {

	private static ArrayList filelist = new ArrayList();
	static int count = 0;

	public static void main(String[] args) {

		refreshFileList("E:\\WorkSpace2015_9_23_Wala_Mars\\WALA");
		System.out.println(count);

	}

	public static void refreshFileList(String strPath) {
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				refreshFileList(files[i].getAbsolutePath());
			}
			else
			{
				String strFileName = files[i].getAbsolutePath();
				System.out.println("---" + strFileName);
				if (strFileName.endsWith(".java")) 
					count++;
				filelist.add(files[i].getAbsolutePath());
			}
		}
	}
}
