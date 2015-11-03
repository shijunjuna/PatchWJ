package diff;
/**
 * 1、读取diff文件
 * 2、将old new patch三个文件合并
 * 3、生成新的diff文件，包含新的行号，以及对应的方法名类名，
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ONCombine {
	private static List<Diff> diffONs;//原始版本
	private static List<Diff> diffOPs;
	private static List<Diff> LastDiffONs = new ArrayList<Diff>();//处理后版本
	private static List<Diff> LastDiffOPs = new ArrayList<Diff>();
	private static String newOrPatch;//当前是new还是patch的修改
	private static String classname;//当前所属类的名字
	private static String methodname;//当前所属方法的名字	
	static int count = 0;
	
	public static void main(String[] args) throws IOException {
		fileList("D:\\data\\old");
	}
	public static void fileList(String strPath) throws IOException {
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				fileList(files[i].getAbsolutePath());
			} else {
				String basePath = files[i].getAbsolutePath();
				if (basePath.endsWith(".java")) {
					System.out.println("all file path : " + basePath);
					String newPath = basePath.replace("old", "new");
					String patchPath = basePath.replace("old", "patch");
					File newfile =new File(newPath);
					File patchfile =new File(patchPath);
					if(newfile.exists() && patchfile.exists()){//合并三个
						String diffPath1 = "D:\\diffResult\\new\\"+basePath.substring(12,basePath.length()-5);
						String diffPath2 = "D:\\diffResult\\patch\\"+basePath.substring(12,basePath.length()-5);
						System.out.println("diffPath1:"+diffPath1);
						System.out.println("diffPath2:"+diffPath2);
						doMerge(diffPath1, diffPath2, basePath, basePath.replace("old", "merge"), 
								diffPath1.replace("diffResult", "lastdiffResult"), diffPath2.replace("diffResult", "lastdiffResult"));
					}else if(!newfile.exists() && patchfile.exists()){//合并两个
						String diffPath2 = "D:\\diffResult\\patch\\"+basePath.substring(12,basePath.length()-5);
						System.out.println("diffPath2:"+diffPath2);
						doMerge("", diffPath2, basePath, basePath.replace("old", "merge"), 
								"", diffPath2.replace("diffResult", "lastdiffResult"));
					}else if(newfile.exists() && !patchfile.exists()){//合并两个
						String diffPath1 = "D:\\diffResult\\new\\"+basePath.substring(12,basePath.length()-5);
						System.out.println("diffPath1:"+diffPath1);
						doMerge(diffPath1, "", basePath, basePath.replace("old", "merge"), 
								diffPath1.replace("diffResult", "lastdiffResult"), "");
					}else{}
					clear();
					count++;
				}
			}
		}
	}
	public static void clear(){
		diffONs.clear();
		diffOPs.clear();
		LastDiffONs.clear();
		LastDiffOPs.clear();
	}
	/**
	 * 调用
	 */
	public static void doMerge(String diffPath1, String diffPath2, String basePath, String mergePath,
			String lastDiffPath1, String lastDiffPath2) throws IOException {
		// 将diff文件的内容提取出来
		if(!"".endsWith(diffPath1))
			diffONs = ReadDiff.createDiff(diffPath1);
		else 
			diffONs = new ArrayList<Diff>();
		if(!"".endsWith(diffPath2))
			diffOPs = ReadDiff.createDiff(diffPath2);
		else 
			diffOPs = new ArrayList<Diff>();
		mergerThree(basePath, mergePath);
		System.out.println("================================");
		writeDiff(lastDiffPath1, lastDiffPath2);
	}
	/**
	 * 将更新后的diff写入文件中
	 */
	public static void writeDiff(String lastDiffPath1, String lastDiffPath2) throws IOException{
		if(!"".equals(lastDiffPath1)){
			File file = new File(lastDiffPath1.substring(0,lastDiffPath1.lastIndexOf("\\")));
			if(!file.exists())
				file.mkdirs();
			BufferedWriter ONdiffWriter = new BufferedWriter(new FileWriter(new File(lastDiffPath1)));
			for(int i = 0;i< LastDiffONs.size();i++){
				Diff diff = LastDiffONs.get(i);
				ONdiffWriter.write(diff.toString()+"\n");
			}
			ONdiffWriter.flush();
			ONdiffWriter.close();
		}
		if(!"".equals(lastDiffPath2)){
			File file = new File(lastDiffPath2.substring(0,lastDiffPath2.lastIndexOf("\\")));
			if(!file.exists())
				file.mkdirs();
			BufferedWriter OPdiffWriter = new BufferedWriter(new FileWriter(new File(lastDiffPath2)));
			for(int i = 0;i< LastDiffOPs.size();i++){
				Diff diff = LastDiffOPs.get(i);
				OPdiffWriter.write(diff.toString()+"\n");
			}
			OPdiffWriter.flush();
			OPdiffWriter.close();
		}
	
	}
	/**
	 * 将两个modified文件merge到base version
	 * 将change对应的行号加入了diff文件中
	 */
	public static void mergerThree(String basePath, String ONPPath) throws IOException{
		BufferedReader baseRreader = new BufferedReader(new FileReader(new File(basePath)));
		File file = new File(ONPPath.substring(0,ONPPath.lastIndexOf("\\")));
		if(!file.exists())
			file.mkdirs();
		BufferedWriter ONPwriter = new BufferedWriter(new FileWriter(ONPPath));
		String baseLine = baseRreader.readLine();
		//行号的偏移量
		int index = 0;//base version 的行标
		int newIndex = 0;//merge version 的行标
		int i = 0,j = 0;
		while(i <= diffONs.size() && j <= diffOPs.size()){
			//先找到diffONs和diffOPs中base部分，baseLine较小的
			Diff temDiffLower;
			if(i < diffONs.size() && j < diffOPs.size()){
				Diff diffON = diffONs.get(i);
				Diff diffOP = diffOPs.get(j);
				if(diffON.getBaseLineNum()[0] < diffOP.getBaseLineNum()[0]){
					temDiffLower = diffON;
					i ++;
					newOrPatch = "new";
				}else{
					temDiffLower = diffOP;
					j ++;
					newOrPatch = "patch";
				}
			}else if(i == diffONs.size() && j < diffOPs.size()){
				temDiffLower = diffOPs.get(j);
				newOrPatch = "patch";
				j ++;
			}else if(i < diffONs.size() && j == diffOPs.size()){
				temDiffLower = diffONs.get(i);
				newOrPatch = "new";
				i ++;
			}else{
				break;
			}
			// TODO 一开始就因该默认类名为文件名
			//write old
			while(baseLine != null && index < Math.max(temDiffLower.getBaseLineNum()[0], temDiffLower.getBaseLineNum()[1])) {
				//System.out.println(baseLine);
				help(baseLine);
				ONPwriter.write(baseLine+"\n");
				baseLine = baseRreader.readLine();
				index ++;
				newIndex ++;
			}
			//creat new BaseLineNum
			int[] tempBase = new int[2];
			if(temDiffLower.getBaseLineNum()[1] == -1){
				tempBase[0] = newIndex;
				tempBase[1] = -1;
				System.out.println(newOrPatch+"==========1="+newIndex+"["+temDiffLower.type+"]");
			}else{
				tempBase[0] = newIndex - temDiffLower.getBaseLineNum()[1] + temDiffLower.getBaseLineNum()[0];
				tempBase[1] = newIndex;
				System.out.println(newOrPatch+"==========2="+(newIndex - temDiffLower.getBaseLineNum()[1] + temDiffLower.getBaseLineNum()[0]) + "," + newIndex +"["+ temDiffLower.type+"]");
			}
			
			//write modified lines
			if(temDiffLower.getaLines().size()>0){
				for(int n = 0; n < temDiffLower.getaLines().size(); n ++){
					help(baseLine);
					System.out.println(temDiffLower.getaLines().get(n)+"//***("+temDiffLower.type+")modified by "+newOrPatch+"***");
					ONPwriter.write(temDiffLower.getaLines().get(n)+"//***("+temDiffLower.type+")modified by "+newOrPatch+"***"+"\n");
					newIndex ++;
				}
			}
			//creat new modiLineNum
			int[] tempModi = new int[2];
			if(temDiffLower.getModiLineNum()[1] == -1){
				tempModi[0] = newIndex;
				tempModi[1] = -1;
				System.out.println(newOrPatch+"==========3="+newIndex);
			}else{
				tempModi[0] = newIndex - temDiffLower.getModiLineNum()[1] + temDiffLower.getModiLineNum()[0];
				tempModi[1] = newIndex;
				System.out.println(newOrPatch+"==========4="+(newIndex - temDiffLower.getModiLineNum()[1] + temDiffLower.getModiLineNum()[0]) + "," + newIndex);
			}
			//生成新的diff并加入list中
			Diff TempDiff = new Diff(tempBase, tempModi, temDiffLower.type, 
					temDiffLower.dLines, temDiffLower.aLines, classname, methodname);
			if(newOrPatch.equals("new"))
				LastDiffONs.add(TempDiff);
			else
				LastDiffOPs.add(TempDiff);
		}
		//write the rest of old 
		while(baseLine != null) {
			help(baseLine);
			//System.out.println(baseLine+"&&");
			ONPwriter.write(baseLine + "\n");
			baseLine = baseRreader.readLine();
			newIndex ++;
		}		
		baseRreader.close();
		ONPwriter.flush();
		ONPwriter.close();
	}
	
	/**
	 * 判断当前类名和方法名
	 */
	public static void help(String baseLine){
		if(!(baseLine.contains("*") || baseLine.contains("//") || baseLine.contains("if") || baseLine.contains("for")
				|| baseLine.contains("return") || baseLine.contains(".") || baseLine.contains("=") || baseLine.contains("new")
				|| baseLine.contains(";") || baseLine.contains("while"))){
			if(baseLine.contains("class")){
				//找到class 后面的那个类名
				if(baseLine.contains("{"))
					baseLine = baseLine.substring(0, baseLine.indexOf("{")).trim();
				baseLine = baseLine.replaceAll(" +"," ");
				String[] s = baseLine.trim().split(" ");
				for(int i = 0; i < s.length; i ++){
					if(s[i].equals("class") && i + 1 < s.length)
						classname = s[i+1];
				}
			}else{
				Pattern p =  Pattern.compile("(private |public |protected )?(void|.*) .*\\(.*\\).*");//复杂匹配
				Matcher m = p.matcher(baseLine);
				if(m.matches()){
					if(baseLine.contains("{"))
						baseLine = baseLine.substring(0, baseLine.indexOf("{"));
					methodname = baseLine.trim();
				}
			}
		}
	}
	
	/**
	 * 将一个modified文件merge到base version
	 */
	public static void mergeTwo(String diffpath ,String oldpath,String ONpath,String comment) throws IOException{
		BufferedReader difRreader = new BufferedReader(new FileReader(new File(diffpath)));
		BufferedReader oldRreader = new BufferedReader(new FileReader(new File(oldpath)));//base version
		BufferedWriter ONwriter = new BufferedWriter(new FileWriter(new File(ONpath)));//modify version
		String difLine = difRreader.readLine();
		String oldLine = oldRreader.readLine();
		int index = 1;
		while(difLine != null) {
			if(difLine.matches("[0-9]*[a-zA-z0-9,]*")){
				String[] tem = difLine.split("[a-zA-z]");
				String[] oldLineNum = tem[0].split(",");
				//write old
				while(oldLine != null && index<=Integer.parseInt(oldLineNum[oldLineNum.length-1])) {
					ONwriter.write(oldLine+"\n");
					oldLine = oldRreader.readLine();
					index ++;
				}
				//write modified lines
				while((difLine = difRreader.readLine()) != null) {
					if((difLine.startsWith("<"))){
						System.out.println(difLine);
					}else if(difLine.startsWith(">")){
						difLine = difLine.substring(1, difLine.length());
						ONwriter.write(difLine+comment+"\n");
						System.out.println(difLine);
					}else if(difLine.startsWith("-")){
					}else
						break;
				}
			}else
				difLine = difRreader.readLine();
		}
		//write the rest of old 
		while(oldLine != null) {
			ONwriter.write(oldLine+"\n");
			oldLine = oldRreader.readLine();
		}
		difRreader.close();
		oldRreader.close();
		ONwriter.flush();
		ONwriter.close();
	}
	

	/**
	 * 将两个modified文件merge到base version
	 */
	public static void rewrite(String basePath, String ONPPath) throws IOException{
		BufferedReader baseRreader = new BufferedReader(new FileReader(new File(basePath)));
		BufferedWriter ONPwriter = new BufferedWriter(new FileWriter(new File(ONPPath)));
		String baseLine = baseRreader.readLine();
		//行号的偏移量
		int index = 0;//base version 的行标
		int newIndex = 0;//merge version 的行标
		int i = 0,j = 0;
		while(i <= diffONs.size() && j <= diffOPs.size()){
			//先找到diffONs和diffOPs中base部分，baseLine较小的
			Diff temDiffLower;
			if(i < diffONs.size() && j < diffOPs.size()){
				Diff diffON = diffONs.get(i);
				Diff diffOP = diffOPs.get(j);
				if(diffON.getBaseLineNum()[0] < diffOP.getBaseLineNum()[0]){
					temDiffLower = diffON;
					i ++;
					newOrPatch = "new";
				}else{
					temDiffLower = diffOP;
					j ++;
					newOrPatch = "patch";
				}
			}else if(i == diffONs.size() && j < diffOPs.size()){
				temDiffLower = diffOPs.get(j);
				newOrPatch = "patch";
				j ++;
			}else if(i < diffONs.size() && j == diffOPs.size()){
				temDiffLower = diffONs.get(i);
				newOrPatch = "new";
				i ++;
			}else{
				break;
			}
			//write old
			while(baseLine != null && index < Math.max(temDiffLower.getBaseLineNum()[0], temDiffLower.getBaseLineNum()[1])) {
				System.out.println(baseLine);
				ONPwriter.write(baseLine+"\n");
				baseLine = baseRreader.readLine();
				index ++;
				newIndex ++;
			}
			//creat new BaseLineNum
			if(temDiffLower.getBaseLineNum()[1] == -1){
				System.out.println(newOrPatch+"==========="+newIndex+"["+temDiffLower.type+"]");
			}else{
				System.out.println(newOrPatch+"==========="+(newIndex - temDiffLower.getBaseLineNum()[1] + temDiffLower.getBaseLineNum()[0]) + "," + newIndex +"["+ temDiffLower.type+"]");
			}
			
			//write modified lines
			if(temDiffLower.getaLines().size()>0){
				for(int n = 0; n < temDiffLower.getaLines().size(); n ++){
					System.out.println(temDiffLower.getaLines().get(n)+"//***modified by "+newOrPatch+"***");
					ONPwriter.write(temDiffLower.getaLines().get(n)+"//***modified by "+newOrPatch+"***"+"\n");
					newIndex ++;
				}
			}
			//creat new modiLineNum
			if(temDiffLower.getModiLineNum()[1] == -1){
				System.out.println(newOrPatch+"==========="+newIndex);
			}else{
				System.out.println(newOrPatch+"==========="+(newIndex - temDiffLower.getModiLineNum()[1] + temDiffLower.getModiLineNum()[0]) + "," + newIndex);
			}
		}
		//write the rest of old 
		while(baseLine != null) {
			System.out.println(baseLine+"&&");
			ONPwriter.write(baseLine + "\n");
			baseLine = baseRreader.readLine();
			newIndex ++;
		}		
		baseRreader.close();
		ONPwriter.flush();
		ONPwriter.close();
	}

}
