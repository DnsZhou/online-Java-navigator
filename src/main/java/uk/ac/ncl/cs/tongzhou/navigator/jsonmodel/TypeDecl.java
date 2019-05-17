package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

import com.github.javaparser.ast.body.TypeDeclaration;

public class TypeDecl {

    public final String name;

    public TypeDecl(final TypeDeclaration typeDeclaration) {
        this.name = typeDeclaration.getNameAsString();
    }
}