package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompilationUnitDecl {

    public TypeDecl[] typeDecls;
    public ImportDecl[] importDecls;
    public PackageDecl packageDecl;

    public CompilationUnitDecl() {
    }

    public CompilationUnitDecl(List<String> typeDeclarations, List<ImportDeclaration> importDeclarations, PackageDeclaration packageDeclaration) {

        List<TypeDecl> tempTypeDecls = new ArrayList<>(typeDeclarations.size());
        for (String typeDeclaration : typeDeclarations) {
            TypeDecl typeDecl = new TypeDecl(typeDeclaration);
            tempTypeDecls.add(typeDecl);
        }
        this.typeDecls = tempTypeDecls.toArray(new TypeDecl[0]);

        List<ImportDecl> tempImportDecls = new ArrayList<>(importDeclarations.size());
        for (ImportDeclaration importDeclaration : importDeclarations) {
            ImportDecl importDecl = new ImportDecl(importDeclaration);
            tempImportDecls.add(importDecl);
        }
        this.importDecls = tempImportDecls.toArray(new ImportDecl[0]);

        this.packageDecl = new PackageDecl(packageDeclaration);
    }

    @Override
    public String toString() {
        return "CompilationUnitDecl{" +
                "typeDecls=" + Arrays.toString(typeDecls) +
                ", importDecls=" + Arrays.toString(importDecls) +
                ", packageDecl=" + packageDecl +
                '}';
    }
}
