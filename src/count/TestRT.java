package count;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;

public class TestRT {
	public final static int END_MARK = 0;

	/**
	 * ʹ��Runtime�����exec����������cmd���
	 */
	public static void main(String[] args) {
		Runtime rt = Runtime.getRuntime();
		try {		
			Process pr = rt.exec("BCompare.exe  @E:\\WorkSpace2015_9_23_Wala_Kepler������\\WJTest\\diff.txt E:\\data\\old\\src\\Clause.java E:\\data\\new\\src\\Clause.java E:Clause.patch"); //����cmd����
			BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String s = br.readLine();
			String temp = "" ;
			while(null != s ){
				if(!"".equals(s.trim()))  temp = s;
				System.out.println(s);
				s = br.readLine();
			}
			br.close();
			//���µ�ǰ�̵߳ȴ��������Ҫ��һֱҪ�ȵ��ɸ� Process �����ʾ�Ľ����Ѿ���ֹ��
			pr.waitFor(); 
			//�� Process �����ʾ���ӽ��̵ĳ���ֵ�����ݹ�����ֵ 0 ��ʾ������ֹ��
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
