package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

import com.github.javaparser.ast.ImportDeclaration;

public class ImportDecl {
    public final String name;

    public ImportDecl(final ImportDeclaration importDeclaration) {
        this.name = importDeclaration.getNameAsString();
    }
}
