package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

import com.github.javaparser.ast.PackageDeclaration;

public class PackageDecl {
    public final String name;

    public PackageDecl(final PackageDeclaration packageDeclaration) {
        this.name = packageDeclaration.getNameAsString();
    }
}
