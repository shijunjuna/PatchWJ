package diff;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 利用beyond compare工具为每个Java文件生成diff文件
 * @author thu
 *
 */
public class CreateDiff {
	
	static int count = 0;
	
	/**
	 * 使用Runtime对象的exec方法，运行cmd命令。
	 */
	public static void main(String[] args) {
		fileList("D:\\data\\old");
		System.out.println("file number : " + count);
	}
	
	
	public static void fileList(String strPath) {
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				fileList(files[i].getAbsolutePath());
			} else {
				String strFileName = files[i].getAbsolutePath();
				if (strFileName.endsWith(".java")) {
					System.out.println("all file path : " + strFileName);
					String newPath = strFileName.replace("old", "new");
					String patchPath = strFileName.replace("old", "patch");
					File newfile =new File(newPath);
					//如果base版本对应的文件不存在，那说明被删掉了，那么我们忽略
					//此时并没有考虑base版本不存在而new或者patch中存在（新加的）,这种改动我们也忽略
					if(newfile.exists()){
						String cmdNew = "BCompare.exe /silent  @D:\\difftool\\diff.txt ";
						cmdNew += strFileName + " " + newPath + " ";
						cmdNew += "D:\\diffResult\\new\\"+strFileName.substring(12,strFileName.length()-5);
						System.out.println(cmdNew);
						doDiff(cmdNew);
					}
					File patchfile =new File(patchPath);
					if(patchfile.exists()){
						String cmdNew = "BCompare.exe /silent  @D:\\difftool\\diff.txt ";
						cmdNew += strFileName + " " + patchPath + " ";
						cmdNew += "D:\\diffResult\\patch\\"+strFileName.substring(12,strFileName.length()-5);
						System.out.println(cmdNew);
						doDiff(cmdNew);
					}
					count++;
				}
			}
		}
	}
	
	public static void doDiff(String cmd){
		Runtime rt = Runtime.getRuntime();
		try {		
			//运行cmd命令
			Process pr = rt.exec(cmd); 
			BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String s = br.readLine();
			while(null != s ){
				if(!"".equals(s.trim()))
					System.out.println(s);
				s = br.readLine();
			}
			br.close();
			//导致当前线程等待，如果必要，一直要等到由该 Process 对象表示的进程已经终止。
			pr.waitFor(); 
			//此 Process 对象表示的子进程的出口值。根据惯例，值 0 表示正常终止。
			if (0 == pr.exitValue()) {
				System.out.println("diff命令执行成功！");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
