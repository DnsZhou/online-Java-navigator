package uk.ac.ncl.cs.tongzhou.navigator;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.CompilationUnitDecl;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.GAVIndex;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.ImportDecl;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.TypeDecl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Resolver {
    static String SLASH_TAG = File.separator;
    static final ObjectMapper objectMapper = new ObjectMapper();

    public String resolve(String groupId, String artifactId, String version,
                          String compilationUnit, String to, List<String> classpathGAVs) throws IOException {

        /* Find the index file with the type name;*/
        List<String> gavsContainingMatchingCUs = findGAVsContaining(to);

        if (gavsContainingMatchingCUs == null)
            //Todo: return readable message showing that the Type is not found in the system
            return null;

        Set<String> candidateSet = new HashSet<>(gavsContainingMatchingCUs);
        if (classpathGAVs != null) {
            Set<String> classpathSet = new HashSet<>(classpathGAVs);
            filterCandidatesByClasspath(candidateSet, classpathSet);
        }

        List<ImportDecl> imports = getImports(groupId, artifactId, version, compilationUnit);
        //TODO: filter candidates against imports

        for (String gavCu : candidateSet) {

            String gav = gavCu.substring(0, gavCu.lastIndexOf(":"));
            //Get all type declarations from the String line in index
            List<TypeDecl> declaredTypes = getAllDeclaredTypes(gav);

            System.out.println(declaredTypes);

            for (TypeDecl typeDecl : declaredTypes) {
                if (typeDecl.name.substring(typeDecl.name.lastIndexOf(".") + 1, typeDecl.name.length()).equals(to)) {
                    return makePath(gav, typeDecl.name);
                }
            }
        }

        return null;
    }

    private Set<String> filterCandidatesByClasspath(Set<String> candidateSet, Set<String> classpathSet) {
        candidateSet.removeIf(candidate -> !classpathSet.contains(candidate.substring(0, candidate.lastIndexOf(':'))));
        return candidateSet;
    }

    public String resolve(String groupId, String artifactId, String version, String compilationUnit, String to) throws IOException {
        return resolve(groupId, artifactId, version, compilationUnit, to, null);
    }

    private String makePath(String gav, String type) {

        String[] tokens = gav.split(":");
        String groupId = tokens[0];
        String artifactId = tokens[1];
        String version = tokens[2];
        String[] cuInfo = type.split("[.]");
        String pathResult = "";

        //Todo: correct it to right location
        pathResult = pathResult.concat(RepositoryWalker.outputHtmlRootDir + SLASH_TAG + groupId + SLASH_TAG + artifactId + SLASH_TAG
                + version + SLASH_TAG + artifactId + "-" + version);
        for (String info : cuInfo) {
            pathResult = pathResult.concat(SLASH_TAG).concat(info);
        }
        pathResult = pathResult.concat(".html");

        return pathResult;
    }


    //One single property file pre type name
    private List<String> findGAVsContaining(String typename) throws IOException {

        File file = new File(RepositoryWalker.outputIndexRootDir, typename);
        if (!file.exists())
            return null;
        List<String> gavs = Files.readAllLines(file.toPath());
        return gavs;
    }

    /**
     * Function for same package import
     */
    public List<TypeDecl> getAllDeclaredTypes(String gav) throws IOException {

        File file = new File(RepositoryWalker.outputJsonRootDir + SLASH_TAG + gav.replaceAll(":", SLASH_TAG + SLASH_TAG) + SLASH_TAG + "package.json");
        GAVIndex gavIndex = objectMapper.readValue(file, GAVIndex.class);

        List<TypeDecl> results = new ArrayList<>();
        for (CompilationUnitDecl compilationUnitDecl : gavIndex.compilationUnitDecls) {
            for (TypeDecl typeDecl : compilationUnitDecl.typeDecls) {
                results.add(typeDecl);
            }
        }

        return results;
    }


    private List<ImportDecl> getImports(String groupId, String artifactId, String version, String compilationUnit) throws IOException {

        File file = new File(RepositoryWalker.outputJsonRootDir + SLASH_TAG + groupId + SLASH_TAG
                + artifactId + SLASH_TAG + version + SLASH_TAG + artifactId + "-" + version + SLASH_TAG
                + compilationUnit.replace(".", SLASH_TAG) + ".json");
        CompilationUnitDecl compilationUnitDecl = objectMapper.readValue(file, CompilationUnitDecl.class);

        List<ImportDecl> importDeclList = new ArrayList<>(Arrays.asList(compilationUnitDecl.importDecls));

        importDeclList.add(new ImportDecl(compilationUnit.substring(0, compilationUnit.lastIndexOf('.'))));

        return importDeclList;
    }


}
