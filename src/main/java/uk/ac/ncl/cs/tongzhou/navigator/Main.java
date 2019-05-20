package uk.ac.ncl.cs.tongzhou.navigator;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.io.File;
import java.util.Date;
import java.util.Optional;

/**
 * s
 *
 * @author Tong Zhou b8027512@ncl.ac.uk
 * @created 21:12 25-03-2019
 */

public class Main<T> {
    static String SLASH_TAG = File.separator;

    public static void main(String[] args) throws Exception {
        System.out.println("Process Started at " + new Date());
        RepositoryWalker.processRepository();
        System.out.println("Process Finished at " + new Date());

//        String testCode = "public class Test { char c = '\u005c''; }";
//        testParseFromString(testCode);

    }

    private static void testParseFromString(String code) throws Exception {
        StaticJavaParser.getConfiguration().setPreprocessUnicodeEscapes(true);
        CompilationUnit compilationUnit = StaticJavaParser.parse(code);
        Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("Test");
        System.out.println(classA.toString());

    }
}
