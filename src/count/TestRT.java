package count;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;

public class TestRT {
	public final static int END_MARK = 0;

	/**
	 * 使用Runtime对象的exec方法，运行cmd命令。
	 */
	public static void main(String[] args) {
		Runtime rt = Runtime.getRuntime();
		try {		
			Process pr = rt.exec("BCompare.exe  @E:\\WorkSpace2015_9_23_Wala_Kepler坏掉了\\WJTest\\diff.txt E:\\data\\old\\src\\Clause.java E:\\data\\new\\src\\Clause.java E:Clause.patch"); //运行cmd命令
			BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String s = br.readLine();
			String temp = "" ;
			while(null != s ){
				if(!"".equals(s.trim()))  temp = s;
				System.out.println(s);
				s = br.readLine();
			}
			br.close();
			//导致当前线程等待，如果必要，一直要等到由该 Process 对象表示的进程已经终止。
			pr.waitFor(); 
			//此 Process 对象表示的子进程的出口值。根据惯例，值 0 表示正常终止。
			if (END_MARK == pr.exitValue()) {
				JOptionPane.showMessageDialog(null, temp );
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
