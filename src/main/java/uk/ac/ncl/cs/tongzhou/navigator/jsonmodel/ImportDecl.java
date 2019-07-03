package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

import com.github.javaparser.ast.ImportDeclaration;

public class ImportDecl implements Cloneable {
    public String name;

    public ImportDecl() {
    }

    public ImportDecl(final String name) {
        this.name = name;
    }

    public ImportDecl(final ImportDeclaration importDeclaration) {

//        this.name = importDeclaration.toString().replaceAll("\n|\r|;", "");
        this.name = importDeclaration.getNameAsString();
        if (importDeclaration.isAsterisk()) {
            this.name = this.name.concat(".*");
        }
    }


    @Override
    public String toString() {
        return "ImportDecl{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new ImportDecl(this.name);
    }
}
