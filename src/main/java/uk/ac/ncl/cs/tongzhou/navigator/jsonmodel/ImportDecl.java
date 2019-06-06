package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

import com.github.javaparser.ast.ImportDeclaration;

public class ImportDecl {
    public String name;

    public ImportDecl() {
    }

    public ImportDecl(final String name) {
        this.name = name;
    }

    public ImportDecl(final ImportDeclaration importDeclaration) {
        this.name = importDeclaration.getNameAsString();
    }

    @Override
    public String toString() {
        return "ImportDecl{" +
                "name='" + name + '\'' +
                '}';
    }
}
