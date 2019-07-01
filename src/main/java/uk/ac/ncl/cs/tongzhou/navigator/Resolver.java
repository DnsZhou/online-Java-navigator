package uk.ac.ncl.cs.tongzhou.navigator;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;

import static uk.ac.ncl.cs.tongzhou.navigator.Util.subStringLastDot;

public class Resolver {
    static final ObjectMapper objectMapper = new ObjectMapper();

    public String resolve(String groupId, String artifactId, String version,
                          String compilationUnit, String navigateTo, List<String> classpathGAVs) throws IOException {
        GavCu navigateFromGavCu = new GavCu(groupId, artifactId, version, compilationUnit);
        GavCu resultGavCu = null;

        /*====Rule 1: Current Type itself*/
        if (navigateTo.equals(subStringLastDot(navigateFromGavCu.cuName))) {
            return makePath(navigateFromGavCu);
        }

        /*Get information of current Type from GavCu*/
        CompilationUnitDecl cuDecl = getCompilationUnitDecl(navigateFromGavCu);
        String packageString = getPackage(cuDecl);
        List<ImportDecl> imports = getImports(cuDecl);
        List<TypeDecl> typeDecls = getTypeDecls(cuDecl);

        /*====Rule2: target type is an internal type of current type*/
        for (TypeDecl typeDecl : typeDecls) {
            /*case 1: quote internal class without the name of its father class, eg: InternalClass class; */
            if (typeDecl.name.replace(navigateFromGavCu.cuName + ".", "").equals(navigateTo)) {
                return makePath(navigateFromGavCu);
            }

            /*case 2: quote internal class with the name of its father class, eg: ThisClass.InternalClass class; */
            if (typeDecl.name.equals(packageString + "." + navigateTo)) {
                return makePath(navigateFromGavCu);
            }
        }


        /* Find the index file with the type name;*/
        List<String> gavsContainingMatchingCUs = findGAVsContaining(navigateTo);

        if (gavsContainingMatchingCUs == null)
            //Todo: return readable message showing that the Type is not found in the system
            return null;

        /*Todo: ask what if current package is not defined in classPath?*/
        Set<String> candidateSet = new HashSet<>(gavsContainingMatchingCUs);
        if (classpathGAVs != null && classpathGAVs.size() != 0) {
            Set<String> classpathSet = new HashSet<>(classpathGAVs);
            filterCandidatesByClasspath(candidateSet, classpathSet);
        }

//        Todo: solve the different priority of Rule 4 and Rule 5
        /*Rule 4: The specific imported CU, Rule 5: same package CU*/
        if (imports != null && imports.size() != 0) {
//            filterCandidatesByImports(candidateSet, imports);
        }

        for (String gavCu : candidateSet) {

            String gav = gavCu.substring(0, gavCu.lastIndexOf(":"));
            //Get all type declarations from the String line in index
            List<TypeDecl> declaredTypes = getAllDeclaredTypes(gav);

            for (TypeDecl typeDecl : declaredTypes) {
                if (typeDecl.name.substring(typeDecl.name.lastIndexOf(".") + 1, typeDecl.name.length()).equals(navigateTo)) {
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

    private Set<String> filterCandidatesByImports(Set<String> candidateSet, List<ImportDecl> imports) {
        candidateSet.removeIf(candidate -> !imports.stream().anyMatch(value -> value.name.equals(candidate.substring(candidate.lastIndexOf(':') + 1, candidate.length()))));
        return candidateSet;
    }

    public String resolve(String groupId, String artifactId, String version, String compilationUnit, String to) throws IOException {
        return resolve(groupId, artifactId, version, compilationUnit, to, null);
    }

    String makePath(GavCu gavCu) {
        String groupId = gavCu.group;
        String artifactId = gavCu.artifact;
        String version = gavCu.version;
        String[] cuInfo = gavCu.cuName.split("[.]");
        String pathResult = "";

        pathResult = pathResult.concat(RepositoryWalker.outputHtmlRootDir + SLASH + groupId.replace(".", SLASH) + SLASH + artifactId + SLASH
                + version + SLASH + artifactId + "-" + version);
        for (String info : cuInfo) {
            pathResult = pathResult.concat(SLASH).concat(info);
        }
        pathResult = pathResult.concat(".html");

        return pathResult;
    }

    private String makePath(String gav, String type) {
        GavCu resultGavCu = new GavCu();
        String[] tokens = gav.split(":");
        resultGavCu.group = tokens[0];
        resultGavCu.artifact = tokens[1];
        resultGavCu.version = tokens[2];
        resultGavCu.cuName = type;
        return makePath(resultGavCu);
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
        String[] pathTokens = gav.split(":");

        String group = pathTokens[pathTokens.length - 3];
        String artifact = pathTokens[pathTokens.length - 2];
        String version = pathTokens[pathTokens.length - 1];

        File file = new File(RepositoryWalker.outputJsonRootDir + SLASH
                + group.replace(".", SLASH) + SLASH + artifact + SLASH + version + SLASH
                + artifact + "-" + version + SLASH + "package.json");
        PackageInfo packageInfo = objectMapper.readValue(file, PackageInfo.class);

        List<TypeDecl> results = new ArrayList<>();
        for (CompilationUnitDecl compilationUnitDecl : packageInfo.compilationUnitDecls) {
            for (TypeDecl typeDecl : compilationUnitDecl.typeDecls) {
                results.add(typeDecl);
            }
        }

        return results;
    }

    /*this is the JSON model of the json file for every type*/
    private CompilationUnitDecl getCompilationUnitDecl(GavCu gavCu) throws IOException {
        File file = new File(RepositoryWalker.outputJsonRootDir + SLASH + gavCu.group.replace(".", SLASH) + SLASH
                + gavCu.artifact + SLASH + gavCu.version + SLASH + gavCu.artifact + "-" + gavCu.version + SLASH
                + gavCu.cuName.replace(".", SLASH) + ".json");
        return objectMapper.readValue(file, CompilationUnitDecl.class);

    }

    private String getPackage(CompilationUnitDecl cuDecl) {

        if (cuDecl.packageDecl != null) {
            return cuDecl.packageDecl.name;
        }
        return null;
    }

    private List<ImportDecl> getImports(CompilationUnitDecl cuDecl) {
        if (cuDecl.importDecls != null && cuDecl.importDecls.length > 0) {
            List<ImportDecl> importDeclList = new ArrayList<>(Arrays.asList(cuDecl.importDecls));

            //        Todo: solve the different priority of Rule 4 and Rule 5
            /*Add current package as a default import*/
            importDeclList.add(new ImportDecl(cuDecl.packageDecl.name));
            return importDeclList;
        }
        return null;
    }

    private List<TypeDecl> getTypeDecls(CompilationUnitDecl cuDecl) {

        if (cuDecl.typeDecls != null && cuDecl.typeDecls.length > 0) {
            List<TypeDecl> typeDeclList = new ArrayList<>(Arrays.asList(cuDecl.typeDecls));
            return typeDeclList;
        }
        return null;
    }
}
