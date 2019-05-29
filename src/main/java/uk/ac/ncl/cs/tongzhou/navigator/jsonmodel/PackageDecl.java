package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

import com.github.javaparser.ast.PackageDeclaration;

public class PackageDecl {
    public String name;

    public PackageDecl() {}

    public PackageDecl(final PackageDeclaration packageDeclaration) {
        this.name = packageDeclaration.getNameAsString();
    }
}
