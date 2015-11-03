package diff;
import java.util.List;

/**
 * diff
 * @author wangjun
 *
 */
public class Diff {
	public int[] baseLineNum;
	public int[] modiLineNum;
	public String type;
	public List<String> dLines;//< lines deleted
	public List<String> aLines;//> lines added
	public String classname;//当前所属类名
	public String methodname;//当前所属的方法名称
	
	public Diff(){}
	public Diff(int[] baseLineNum, int[] modiLineNum, String type,
			List<String> dLines, List<String> aLines, String classname,
			String methodname) {
		super();
		this.baseLineNum = baseLineNum;
		this.modiLineNum = modiLineNum;
		this.type = type;
		this.dLines = dLines;
		this.aLines = aLines;
		this.classname = classname;
		this.methodname = methodname;
	}
	public int[] getBaseLineNum() {
		return baseLineNum;
	}
	public void setBaseLineNum(int[] baseLineNum) {
		this.baseLineNum = baseLineNum;
	}
	public int[] getModiLineNum() {
		return modiLineNum;
	}
	public void setModiLineNum(int[] modiLineNum) {
		this.modiLineNum = modiLineNum;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getdLines() {
		return dLines;
	}
	public void setdLines(List<String> dLines) {
		this.dLines = dLines;
	}
	public List<String> getaLines() {
		return aLines;
	}
	public void setaLines(List<String> aLines) {
		this.aLines = aLines;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getMethodname() {
		return methodname;
	}
	public void setMethodname(String classname) {
		this.methodname = classname;
	}
	
	@Override
	public final String toString() {
		String sd = "";
		for(int i=0;i<dLines.size();i++){
			sd += "\n" + "<" + dLines.get(i);
		}
		String sa = "";
		for(int i=0;i<aLines.size();i++){
			sa += "\n" + ">" + aLines.get(i);
		}
		return 	baseLineNum[0] + "," + baseLineNum[1] + type + modiLineNum[0] + "," + modiLineNum[1] + "\n" +
				"classname & methodname:" +  "\n" +
				classname + "\n" +
				methodname +
				sd +
				sa;
	}  
	
}
