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
        var startTime = new Date();
        System.out.println("Process Started at " + startTime);
        RepositoryWalker.processRepository();
        var endTime = new Date();
        System.out.println("Process Finished at " + endTime);
        System.out.println("Took " + ((double) (endTime.getTime() - startTime.getTime()) / 1000) + " seconds.");
    }
}
