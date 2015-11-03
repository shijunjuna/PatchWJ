package diff;

import com.ibm.wala.classLoader.IMethod;
/**
 * 每个diff语句进行切片，所产生的结果。每个结果对应多条MyStatement，
 * 指定了源代码中的某行语句。
 * @author thu
 *
 */
public class MyStatement {

	public int lineNumber;
	public IMethod method;
	
	public MyStatement(int lineNumber, IMethod method) {
		super();
		this.lineNumber = lineNumber;
		this.method = method;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	public IMethod getMethod() {
		return method;
	}
	public void setMethod(IMethod method) {
		this.method = method;
	}
	
	@Override
	public final String toString() {
		return "Source line number = " + lineNumber + "," + method;
	} 
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MyStatement) {
			MyStatement other = (MyStatement) obj;
			return lineNumber == other.lineNumber && method.getReference().equals(other.method.getReference());
		}
		return false;
	}
	
	@Override  
	public int hashCode() {  
		return lineNumber + method.getReference().hashCode();  
	}
}
