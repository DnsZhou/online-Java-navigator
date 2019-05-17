package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

import com.github.javaparser.ast.body.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;

public class CompilationUnitDecl {

    public final TypeDecl[] typeDecls;

    public CompilationUnitDecl(List<TypeDeclaration> typeDeclarations) {

        List<TypeDecl> tempTypeDecls = new ArrayList<>(typeDeclarations.size());
        for(TypeDeclaration typeDeclaration : typeDeclarations) {
            TypeDecl typeDecl = new TypeDecl(typeDeclaration);
            tempTypeDecls.add(typeDecl);
        }
        this.typeDecls = tempTypeDecls.toArray(new TypeDecl[0]);
    }
}
