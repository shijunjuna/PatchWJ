package diff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadDiff {
	
	public static void main(String[] args) throws IOException {
		
		List<Diff> diffONs = createDiff("lastDiffON.txt");
		System.out.println("==========================");
		for(int i = 0;i< diffONs.size();i++){
			Diff diff = diffONs.get(i);
			System.out.println(diff);
		}
		
	}
	
	/**
	 * 将diff文件的内容提取出来
	 */
	public static List<Diff> createDiff(String diffpath) throws IOException{
		List<Diff> diffs = new ArrayList<Diff>();
		BufferedReader difRreader = new BufferedReader(new FileReader(new File(diffpath)));
		String difLine = difRreader.readLine();
		while(difLine != null && !difLine.equals("")) {
			if(difLine.matches("[0-9]*[a-zA-z0-9,-]*")){
				//type
				Diff diff = new Diff();
				if(difLine.contains("a"))
					diff.setType("a");
				else if(difLine.contains("c"))
					diff.setType("c");
				else if(difLine.contains("d"))
					diff.setType("d");
				//start end
				String[] tem = difLine.split("[a-zA-z]");
				String[] oldLineNum = tem[0].split(",");
				String[] newLineNum = tem[1].split(",");
				int[] temp1 = {-1,-1};
				for(int i =0;i<oldLineNum.length;i++){
					temp1[i] = Integer.parseInt(oldLineNum[i]);
					//System.out.print(oldLineNum[i]);
				}
				diff.setBaseLineNum(temp1);
				int[] temp2 = {-1,-1};
				for(int i =0;i<newLineNum.length;i++){
					temp2[i] = Integer.parseInt(newLineNum[i]);
					//System.out.print(newLineNum[i]+" ");
				}
				diff.setModiLineNum(temp2);
				//类名  方法名
				difLine = difRreader.readLine();
				if(difLine.contains("classname")){
					diff.setClassname(difRreader.readLine());
					diff.setMethodname(difRreader.readLine());
					difLine = difRreader.readLine();
				}
				//dLines(<)   aLines(>)
				List<String> dLines = new ArrayList<String>();
				List<String> aLines = new ArrayList<String>();
				while(difLine != null) {
					if((difLine.startsWith("<"))){
						difLine = difLine.substring(1, difLine.length());
						dLines.add(difLine);
						//System.out.println(difLine);
						difLine = difRreader.readLine();
					}else if(difLine.startsWith(">")){
						difLine = difLine.substring(1, difLine.length());
						aLines.add(difLine);
						//System.out.println(difLine);
						difLine = difRreader.readLine();
					}else if(difLine.startsWith("-")){
						difLine = difRreader.readLine();
					}
					else break;
				}
				diff.setdLines(dLines);
				diff.setaLines(aLines);
				diffs.add(diff);
			}else
				difLine = difRreader.readLine();
		}
		difRreader.close();
		return diffs;
	}
	
}
