package uk.ac.ncl.cs.tongzhou.navigator;

import java.util.Date;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

/**
 * s
 *
 * @author Tong Zhou b8027512@ncl.ac.uk
 * @created 21:12 25-03-2019
 */

public class Main<T> {
    public static void main(String[] args) throws Exception {
//		String fileName1 = "Club.java";
//		String fileName2 = "TestJava.java";
//		String fileName3 = "Bar.java";
//		String testCode = "public class Person {" + "int number;" + "int numberOne = 1;" + "public void testFunction(){"
//				+ "int i = 1;" + "}" + "};" + "public class B { }";
//
//		testParseFromString(testCode);
//		ParseFromFile.testReadAndParseFromeFile(fileName1);
//		ModifyFile.testModifyFile(fileName2);
//		AnalysisSymbol.testAnalysisSymbol(fileName1);
        System.out.println("Start parsing the full repository on "+new Date());
        RepositoryWalker.main(args);
        System.out.println("Finish parsing the full repository on "+new Date());
    }

    private static void testParseFromString(String code) throws Exception {
        CompilationUnit compilationUnit = StaticJavaParser.parse("class A { }");
        CompilationUnit compilationUnit2 = StaticJavaParser.parse(code);
        Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("A");
        System.out.println(classA.toString());
        Optional<ClassOrInterfaceDeclaration> classPerson = compilationUnit2.getClassByName("Person");
        System.out.println(classPerson.toString());

    }
}
