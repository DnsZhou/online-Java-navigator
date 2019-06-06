package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

import java.util.Arrays;

public class GAVIndex {

    public CompilationUnitDecl[] compilationUnitDecls;

    @Override
    public String toString() {
        return "GAVIndex{" +
                "compilationUnitDecls=" + Arrays.toString(compilationUnitDecls) +
                '}';
    }
}
