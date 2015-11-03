package diff;

import com.ibm.wala.classLoader.IMethod;
/**
 * ÿ��diff��������Ƭ���������Ľ����ÿ�������Ӧ����MyStatement��
 * ָ����Դ�����е�ĳ����䡣
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
