package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

import java.util.ArrayList;
import java.util.Arrays;

public class PackageInfo {

    public CompilationUnitDecl[] compilationUnitDecls;

    @Override
    public String toString() {
        return "PackageInfo{" +
                "compilationUnitDecls=" + Arrays.toString(compilationUnitDecls) +
                '}';
    }

    public void add(CompilationUnitDecl compilationUnitDecl) {
        if (this.compilationUnitDecls == null) {
            this.compilationUnitDecls = new CompilationUnitDecl[1];
            this.compilationUnitDecls[0] = compilationUnitDecl;
        } else {
            ArrayList<CompilationUnitDecl> arrayList = new ArrayList<>(Arrays.asList(this.compilationUnitDecls));
            arrayList.add(compilationUnitDecl);
            this.compilationUnitDecls = arrayList.toArray(new CompilationUnitDecl[arrayList.size()]);
        }
    }
}
