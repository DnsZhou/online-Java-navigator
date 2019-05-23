package uk.ac.ncl.cs.tongzhou.navigator.learnning;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SolverTest {
    private static final String FILE_PATH = "src/main/resources/someproject/me/tomassetti/Agenda.java";

    static class TypeCalculatorVisitor extends VoidVisitorAdapter<JavaParserFacade> {
        @Override
        public void visit(ReturnStmt n, JavaParserFacade javaParserFacade) {
            super.visit(n, javaParserFacade);
            System.out.println(n.toString() + " has type " + javaParserFacade.getTypeOfThisIn(n));
        }

        @Override
        public void visit(MethodCallExpr n, JavaParserFacade javaParserFacade) {
            super.visit(n, javaParserFacade);
            System.out.println(n.toString() + " has type " + javaParserFacade.getType(n).describe());
            if (javaParserFacade.getType(n).isReferenceType()) {
                for (ResolvedReferenceType ancestor : javaParserFacade.getType(n).asReferenceType().getAllAncestors()) {
                    System.out.println("Ancestor " + ancestor.describe());
                }
            }
        }

        @Override
        public void visit(ExpressionStmt n, JavaParserFacade javaParserFacade) {
            super.visit(n, javaParserFacade);
//            ResolvedType resolvedType = n.calculateResolvedType();
//            System.out.println(n.toString() + " is a: " + resolvedType);
        }
    }

    public static void testSolver() throws FileNotFoundException {
        TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver(), new JavaParserTypeSolver(new File("src/main/resources/someproject")));
        ParseResult<CompilationUnit> result = new JavaParser().parse(new FileInputStream(new File(FILE_PATH)));

        CompilationUnit agendaCu = result.getResult().get();
        agendaCu.accept(new TypeCalculatorVisitor(), JavaParserFacade.get(typeSolver));
//        ClassOrInterfaceDeclaration res = agendaCu.getClassByName("Person").get();

    }
}
