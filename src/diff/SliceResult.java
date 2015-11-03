package diff;

import java.util.Collection;

import com.ibm.wala.classLoader.IMethod;
/**
 * 每个diff语句进行切片，所产生的结果。
 * @author thu
 *
 */
public class SliceResult {
	public Collection<MyStatement> sliceUnique;
	public IMethod method;
	public int lineNumber;
	
	public SliceResult(Collection<MyStatement> sliceUnique, IMethod method, int lineNumber) {
		super();
		this.sliceUnique = sliceUnique;
		this.method = method;
		this.lineNumber = lineNumber;
	}
	public Collection<MyStatement> getSliceUnique() {
		return sliceUnique;
	}
	public void setSliceUnique(Collection<MyStatement> sliceUnique) {
		this.sliceUnique = sliceUnique;
	}
	public IMethod getMethod() {
		return method;
	}
	public void setMethod(IMethod method) {
		this.method = method;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	@Override
	public final String toString() {
		String s = "";
		s += "seed lineNumber = " + lineNumber + "," + method + "\n";
		for (MyStatement ms : sliceUnique) {
			s += ms + "\n";
		}
		return s;
	}

}
