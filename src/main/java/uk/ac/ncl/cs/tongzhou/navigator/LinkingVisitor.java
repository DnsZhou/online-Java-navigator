package uk.ac.ncl.cs.tongzhou.navigator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.LinkObject;

import java.util.ArrayList;
import java.util.List;

public class LinkingVisitor extends VoidVisitorAdapter<JavaSymbolSolver> {

    public static final DataKey<String> LINK_STYLE = new DataKey<>() {
    };
    public static final DataKey<String> LINK_ID = new DataKey<>() {
    }; // for origins
    public static final DataKey<LinkObject> LINK_TO = new DataKey<>() {
    }; // for targets

    private final List<TypeDeclaration> typeDeclarations = new ArrayList<>();
    private final List<ImportDeclaration> importDeclarations = new ArrayList<>();

    private final List<String> declaredTypes = new ArrayList<>();
    private final List<String> declaredImports = new ArrayList<>();

    public List<TypeDeclaration> getTypeDeclarations() {
        return typeDeclarations;
    }

    public List<ImportDeclaration> getImportDeclarations() {
        return importDeclarations;
    }

    public List<String> getDeclaredTypes() {
        return declaredTypes;
    }

    public List<String> getDeclaredImports() {
        return declaredImports;
    }


    @Override
    public void visit(ClassOrInterfaceDeclaration node, JavaSymbolSolver javaSymbolSolver) {

        typeDeclarations.add(node);

        ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration = javaSymbolSolver.resolveDeclaration(node, ResolvedReferenceTypeDeclaration.class);
        String name = resolvedReferenceTypeDeclaration.getQualifiedName();

        node.setData(LINK_ID, name);
        node.setData(LINK_STYLE, "ClassOrInterfaceDeclaration");

        this.declaredTypes.add(name);

        super.visit(node, javaSymbolSolver);
    }

    @Override
    public void visit(AnnotationDeclaration node, JavaSymbolSolver javaSymbolSolver) {
        typeDeclarations.add(node);
        ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration = javaSymbolSolver.resolveDeclaration(node, ResolvedReferenceTypeDeclaration.class);
        String name = resolvedReferenceTypeDeclaration.getQualifiedName();

        node.setData(LINK_ID, name);
        node.setData(LINK_STYLE, "AnnotationDeclaration");

        this.declaredTypes.add(name);

        super.visit(node, javaSymbolSolver);

    }

    @Override
    public void visit(EnumDeclaration node, JavaSymbolSolver javaSymbolSolver) {
        typeDeclarations.add(node);
        ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration = javaSymbolSolver.resolveDeclaration(node, ResolvedReferenceTypeDeclaration.class);
        String name = resolvedReferenceTypeDeclaration.getQualifiedName();

        node.setData(LINK_ID, name);
        node.setData(LINK_STYLE, "EnumDeclaration");

        this.declaredTypes.add(name);

        super.visit(node, javaSymbolSolver);
    }

    @Override
    public void visit(ImportDeclaration node, JavaSymbolSolver javaSymbolSolver) {
        importDeclarations.add(node);
        declaredImports.add(node.getNameAsString());
    }

    @Override
    public void visit(ClassOrInterfaceType node, JavaSymbolSolver javaSymbolSolver) {

        SimpleName simpleName = node.getName();
        String fullName = node.getTokenRange().get().toString();
        if (fullName.contains("<")) {
            fullName = simpleName.toString();
        }
        String navFrom = findRelativePath(node);
        LinkObject linkObject = new LinkObject(navFrom, fullName);
        simpleName.setData(LINK_TO, linkObject);
        simpleName.setData(LINK_STYLE, "ClassOrInterfaceType");

    }

    private String findRelativePath(Node node) {
        if (node instanceof CompilationUnit) {
            return null;
        } else if (node instanceof ClassOrInterfaceDeclaration) {
            String childNodeResult = findRelativePath(node.getParentNode().get());
            if (childNodeResult != null) {
                return childNodeResult + "." + ((ClassOrInterfaceDeclaration) node).getName();
            } else {
                return ((ClassOrInterfaceDeclaration) node).getName().toString();
            }
        } else if (node instanceof AnnotationDeclaration) {
            String childNodeResult = findRelativePath(node.getParentNode().get());
            if (childNodeResult != null) {
                return childNodeResult + "." + ((AnnotationDeclaration) node).getName();
            } else {
                return ((AnnotationDeclaration) node).getName().toString();
            }
        } else if (node instanceof EnumDeclaration) {
            String childNodeResult = findRelativePath(node.getParentNode().get());
            if (childNodeResult != null) {
                return childNodeResult + "." + ((EnumDeclaration) node).getName();
            } else {
                return ((EnumDeclaration) node).getName().toString();
            }
        } else {
            return findRelativePath(node.getParentNode().get());
        }
    }
}

