package uk.ac.ncl.cs.tongzhou.navigator;

import java.io.FileInputStream;
import java.io.IOException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;

public class ParseFromFile {
	public static void testReadAndParseFromeFile(String fileName) throws IOException {
		SourceRoot sourceRoot = new SourceRoot(
				CodeGenerationUtils.mavenModuleRoot(Main.class).resolve("src/main/resources"));

		// Our sample is in the root of this directory, so no package name.
		CompilationUnit cu = sourceRoot.parse("", fileName);

		// visit and print the methods names
		cu.accept(new MethodVisitor(), null);
	}

	/**
	 * Simple visitor implementation for visiting MethodDeclaration nodes.
	 */
	private static class MethodVisitor extends VoidVisitorAdapter<Void> {
		@Override
		public void visit(MethodDeclaration n, Void arg) {
			/*
			 * here you can access the attributes of the method. this method will be called
			 * for all methods in this CompilationUnit, including inner class methods
			 */
			System.out.println(n.getName());
			super.visit(n, arg);
		}
	}

}
