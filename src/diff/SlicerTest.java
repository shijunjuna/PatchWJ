/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package diff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;

import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.core.tests.callGraph.CallGraphTestUtil;
import com.ibm.wala.core.tests.util.TestConstants;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Slicer;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.strings.Atom;

public class SlicerTest {

	private static AnalysisScope cachedScope;
	private static Statement statement;
	private static Collection<MyStatement> sliceUnique;
	private static CGNode node;
	private Collection<SliceResult> sliceResultsNew = HashSetFactory.make();
	private Collection<SliceResult> sliceResultsPatch = HashSetFactory.make();
	

	private static AnalysisScope findOrCreateAnalysisScope() throws IOException {
		if (cachedScope == null) {
			System.out.println("#wj# AnalysisScope");
			cachedScope = CallGraphTestUtil
					.makeJ2SEAnalysisScope(TestConstants.HELLO, "Java60RegressionExclusions.txt");
		}
		return cachedScope;
	}

	private static IClassHierarchy cachedCHA;

	private static IClassHierarchy findOrCreateCHA(AnalysisScope scope) throws ClassHierarchyException {
		if (cachedCHA == null) {
			System.out.println("#wj# ClassHierarchy");
			cachedCHA = ClassHierarchy.make(scope);
		}
		return cachedCHA;
	}

	@AfterClass
	public static void afterClass() {
		cachedCHA = null;
		cachedScope = null;
	}
	/**
	 * IR ir = node.getIR();循环IR指令找到指定的行号
	 */
	@Test
	public void testSlice1() throws ClassHierarchyException, IllegalArgumentException, CancelException, IOException,
			InvalidClassFileException, NullPointerException {
		AnalysisScope scope = findOrCreateAnalysisScope();
		IClassHierarchy cha = findOrCreateCHA(scope);

		Iterable<Entrypoint> entrypoints = com.ibm.wala.ipa.callgraph.impl.Util.makeAnyEntrypoints(scope, cha,
				TestConstants.HELLO_MAIN, "addVar", "public void addVar(final int var)");
		AnalysisOptions options = CallGraphTestUtil.makeAnalysisOptions(scope, entrypoints);

		CallGraphBuilder builder = Util.makeZeroOneCFABuilder(options, new AnalysisCache(), cha, scope);
		CallGraph cg = builder.makeCallGraph(options, null);

		CGNode node = findMethod(cg, "addVar");
		// Statement s = null;
		IR ir = node.getIR();
		for (int i = 0; i < ir.getInstructions().length; i++) {
			int bcIndex = ((ShrikeBTMethod) node.getMethod()).getBytecodeIndex(i);
			if (node.getMethod().getLineNumber(bcIndex) == 48) {
				// System.out.println("i===="+i);
				Statement s = new NormalStatement(node, i);
				s = new NormalStatement(node, i);
				if (s.getNode().getIR().getInstructions()[i] != null) {
					// System.err.println("InstructionIndex"+i+"   Statement: " + s);
					if (s.getKind() == Statement.Kind.NORMAL) { // ignore special kinds of statements
						int instructionIndex = ((NormalStatement) s).getInstructionIndex();
						bcIndex = ((ShrikeBTMethod) s.getNode().getMethod()).getBytecodeIndex(instructionIndex);
						int src_line_number = s.getNode().getMethod().getLineNumber(bcIndex);
						System.err.println ( "seed Source line number = " + src_line_number );
						// compute a data slice
						//Collection<Statement> slice = 
								Slicer.computeForwardSlice(s, cg, builder.getPointerAnalysis(), DataDependenceOptions.FULL,ControlDependenceOptions.FULL);
								//dumpSlice(slice);
					}
				}
			}
		}
	}

	@Test
	public void testSlice() throws IOException, ClassHierarchyException, IllegalArgumentException, InvalidClassFileException, CancelException{
		AnalysisScope scope = findOrCreateAnalysisScope();
		IClassHierarchy cha = findOrCreateCHA(scope);
		
		fileList("D:\\lastdiffResult\\new", scope, cha, sliceResultsNew);
		fileList("D:\\lastdiffResult\\patch", scope, cha, sliceResultsPatch);
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		isConflict(sliceResultsNew ,sliceResultsPatch);
	}
	
	/**
	 * 进行切片
	 */
	public void doSlicing(AnalysisScope scope, IClassHierarchy cha, String className, String sdescriptor, int lineNumber) throws IllegalArgumentException, InvalidClassFileException, CancelException, IOException, ClassHierarchyException{
		Iterable<Entrypoint> entrypoints = com.ibm.wala.ipa.callgraph.impl.Util.makeAnyEntrypoints(scope, cha,
				className, getMethodName(sdescriptor), sdescriptor);
		AnalysisOptions options = CallGraphTestUtil.makeAnalysisOptions(scope, entrypoints);
	
		CallGraphBuilder builder = Util.makeZeroOneCFABuilder(options, new AnalysisCache(), cha, scope);
		CallGraph cg = builder.makeCallGraph(options, null);
		
		node = findMethod(cg, getMethodName(sdescriptor));
		
		List<Integer> bcLineNumberList = node.getMethod().getBCNumber(lineNumber);
		for (int i = 0; i < bcLineNumberList.size(); i++) {
			List<Integer> instructionsList = ((ShrikeBTMethod) node.getMethod()).getInstructionIndex(bcLineNumberList.get(i));
			if (instructionsList != null) {
				for (int j = 0; j < instructionsList.size(); j++) {
					statement = new NormalStatement(node, instructionsList.get(j));
					if (statement.getNode().getIR().getInstructions()[instructionsList.get(j)] != null) {
						if (statement.getKind() == Statement.Kind.NORMAL) { // ignore special kinds of statements
							// compute a data slice
							long a = System.currentTimeMillis();
							Collection<Statement> slice = Slicer.computeForwardSlice(statement, cg, builder.getPointerAnalysis(), DataDependenceOptions.FULL,
									ControlDependenceOptions.FULL);
							System.out.println("time:"+(System.currentTimeMillis()-a));
							dumpSliceToFile(slice, "sliceResult");
						}
					}
				}
			}
		}
	}

	/**
	 * 判断是否有冲突存在
	 */
	public void isConflict(Collection<SliceResult> sliceResultsNew, Collection<SliceResult> sliceResultsPatch){
		for(SliceResult sliceResultNew : sliceResultsNew){
			Collection<MyStatement> sliceUniqueNew = sliceResultNew.getSliceUnique();
			for(MyStatement myStatementNew : sliceUniqueNew){
				for(SliceResult sliceResultPatch : sliceResultsPatch){
					Collection<MyStatement> sliceUniquePatch = sliceResultPatch.getSliceUnique();
					for(MyStatement myStatementPatch : sliceUniquePatch){
						if(myStatementNew.equals(myStatementPatch)){
							System.out.println("Conflict:"+myStatementNew+"==="+myStatementPatch);
						}
					}
				}
			}
		}
	}
	
	public void fileList(String strPath, AnalysisScope scope, IClassHierarchy cha, Collection<SliceResult> sliceResults) throws IOException, IllegalArgumentException, ClassHierarchyException, InvalidClassFileException, CancelException {
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		if (files == null)
			return ;
		for (int m = 0; m < files.length; m++) {
			if (files[m].isDirectory()) {
				fileList(files[m].getAbsolutePath(), scope, cha, sliceResults);
			} else {
				String strFileName = files[m].getAbsolutePath();
				List<Diff> diffONs = ReadDiff.createDiff(strFileName);
				System.out.println(strFileName);
				for(int i = 0;i< diffONs.size();i++){
					Diff diff = diffONs.get(i);
					System.out.println("进行切片：==========="+diff.getMethodname());
					for(int k = diff.getBaseLineNum()[0]; k <= Math.max(diff.getModiLineNum()[0], diff.getModiLineNum()[1]); k ++){
						System.out.println("切片行号：==========="+k);
						try{
							sliceUnique = HashSetFactory.make();
							//something like : doSlicing(scope, cha, "public void removeVar(final int var) ", 40);
							// TODO 类名需要改进
							doSlicing(scope, cha, getClassName(strFileName,diff.getClassname()),diff.getMethodname(), k);
							SliceResult sliceResult = new SliceResult(sliceUnique, node.getMethod(), k);
							sliceResults.add(sliceResult);
							System.out.println(sliceResult);
						}catch(NullPointerException e){
							System.out.println("the LineNUmber " + k + " can not act slicing!");
						}
					}
				}
			}
		}
	}
	
	@Test
	public void testSliceAlone() throws IOException, ClassHierarchyException, IllegalArgumentException, InvalidClassFileException, CancelException{
		AnalysisScope scope = findOrCreateAnalysisScope();
		IClassHierarchy cha = findOrCreateCHA(scope);
		
		doSlicing(scope, cha, TestConstants.HELLO_MAIN, "public void addVar(final int var)", 48);
	}
	
	/**
	 * 获取方法名称
	 * somthing like:public void removeVar(final int var)
	 * return : removeVar
	 */
	public String getMethodName(String s) {
		String prefix = s.substring(0, s.indexOf("("));
		String[] temp = prefix.split(" ");
		String methodname = temp[temp.length-1];
		return methodname;
	}
	/**
	 * 获取包含路径的类名
	 * something like: D:\lastdiffResult\new\src\stats\ArrayDigest
	 * return : Lstats/ArrayDigest
	 */
	public String getClassName(String path ,String name) {
		String temp = "L"+path.substring(path.indexOf("src")+4,path.length());
		temp = temp.replaceAll("\\\\", "/");
		if(!path.endsWith(name))
			temp += "$"+name;
		return temp;
	}
	
	
	public static void dumpSlice(Collection<Statement> slice, BufferedWriter w) throws IOException {
		for (Statement s : slice) {
			if (s.getKind() == Statement.Kind.NORMAL) { // ignore special kinds of statements
				int bcIndex, instructionIndex = ((NormalStatement) s).getInstructionIndex();
				try {
					bcIndex = ((ShrikeBTMethod) s.getNode().getMethod()).getBytecodeIndex(instructionIndex);
					try {
						int src_line_number = s.getNode().getMethod().getLineNumber(bcIndex);
						if(src_line_number != -1){
							MyStatement ms = new MyStatement(src_line_number, s.getNode().getMethod());
							//去重
							if(!sliceUnique.contains(ms)){
								sliceUnique.add(ms);
							}
						}
					} catch (Exception e) {
						System.err.println("Bytecode index no good");
						System.err.println(e.getMessage());
					}
				} catch (Exception e) {
					System.err.println("it's probably not a BT method (e.g. it's a fakeroot method)");
					System.err.println(e.getMessage());
				}
			}
		}
	}

	public static void dumpSliceToFile(Collection<Statement> slice, String fileName) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
		dumpSlice(slice, writer);
	}

	public static CGNode findMethod(CallGraph cg, String name) {
		Atom a = Atom.findOrCreateUnicodeAtom(name);
		for (Iterator<? extends CGNode> it = cg.iterator(); it.hasNext();) {
			CGNode n = it.next();
			if (n.getMethod().getName().equals(a)) {
				return n;
			}
		}
		System.err.println("call graph " + cg);
		Assertions.UNREACHABLE("failed to find method " + name);
		return null;
	}
}
