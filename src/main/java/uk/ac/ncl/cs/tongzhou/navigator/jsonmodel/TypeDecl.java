package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

import com.github.javaparser.ast.body.TypeDeclaration;

public class TypeDecl {

    public String name;

    public TypeDecl() {
    }

    public TypeDecl(final TypeDeclaration typeDeclaration) {
        this.name = typeDeclaration.getNameAsString();
    }

    public TypeDecl(final String typeDeclaration) {
        this.name = typeDeclaration;
    }

    @Override
    public String toString() {
        return "TypeDecl{" +
                "name='" + name + '\'' +
                '}';
    }
}