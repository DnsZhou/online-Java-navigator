package uk.ac.ncl.cs.tongzhou.navigator.learnning;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.io.IOException;

public class AnalysisSymbol {
private String fileName;
	public static void testAnalysisSymbol(String fileName) throws IOException {

		String path = "src/main/resources/";

		// Set up a minimal type solver that only looks at the classes used to run this
		// sample.
		CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
		combinedTypeSolver.add(new ReflectionTypeSolver());

		// Configure JavaParser to use type resolution
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
		StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

		// Parse some code
		CompilationUnit cu = StaticJavaParser.parse(new File(path + fileName));
//		CompilationUnit cu = StaticJavaParser.parse("class X { int a = 0; int x() { a+=1; return 1 + 1.0 - 5; };  }");
		// Find all the calculations with two sides:
		cu.findAll(AssignExpr.class).forEach(be -> {
			// Find out what type it has:
			ResolvedType resolvedType = be.calculateResolvedType();

			// Show that it's "double" in every case:
			System.out.println(be.toString() + " is a: " + resolvedType);
		});
	}

	class testClass extends AnalysisSymbol{
		private void testMethod(){
			super.fileName = null;
		}
	}
}
