package uk.ac.ncl.cs.tongzhou.navigator;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.Date;
import java.util.Optional;

/**
 * s
 *
 * @author Tong Zhou b8027512@ncl.ac.uk
 * @created 21:12 25-03-2019
 */

public class Main<T> {
    public static void main(String[] args) throws Exception {
//        String testCode = "public class Test { char c = '\u005c''; }";
//        testParseFromString(testCode);

        System.out.println("Start parsing the full repository on " + new Date());
        RepositoryWalker.main(args);
        System.out.println("Finish parsing the full repository on " + new Date());
    }

    private static void testParseFromString(String code) throws Exception {
        StaticJavaParser.getConfiguration().setPreprocessUnicodeEscapes(true);
        CompilationUnit compilationUnit = StaticJavaParser.parse(code);
        Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("Test");
        System.out.println(classA.toString());

    }
}
