package diff;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * ����beyond compare����Ϊÿ��Java�ļ�����diff�ļ�
 * @author thu
 *
 */
public class CreateDiff {
	
	static int count = 0;
	
	/**
	 * ʹ��Runtime�����exec����������cmd���
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
					//���base�汾��Ӧ���ļ������ڣ���˵����ɾ���ˣ���ô���Ǻ���
					//��ʱ��û�п���base�汾�����ڶ�new����patch�д��ڣ��¼ӵģ�,���ָĶ�����Ҳ����
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
			//����cmd����
			Process pr = rt.exec(cmd); 
			BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String s = br.readLine();
			while(null != s ){
				if(!"".equals(s.trim()))
					System.out.println(s);
				s = br.readLine();
			}
			br.close();
			//���µ�ǰ�̵߳ȴ��������Ҫ��һֱҪ�ȵ��ɸ� Process �����ʾ�Ľ����Ѿ���ֹ��
			pr.waitFor(); 
			//�� Process �����ʾ���ӽ��̵ĳ���ֵ�����ݹ�����ֵ 0 ��ʾ������ֹ��
			if (0 == pr.exitValue()) {
				System.out.println("diff����ִ�гɹ���");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
