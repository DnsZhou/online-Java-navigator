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

        /*Get information of current Type from GavCu*/
        CompilationUnitDecl cuDecl = getCompilationUnitDecl(navigateFromGavCu);
        String navFromPkg = getPackage(cuDecl);
        List<ImportDecl> navFromImports = getImports(cuDecl);
        List<TypeDecl> navFromTypeDecls = getTypeDecls(cuDecl);

        /*====Rule 1: Current Type itself*/
        if (navigateTo.equals(subStringLastDot(navigateFromGavCu.cuName.replace(navFromPkg + ".", "")))) {
            return makePath(navigateFromGavCu);
        }

        /*====Rule2: target type is an nested type of current type*/
        for (TypeDecl typeDecl : navFromTypeDecls) {
            /*case 1: quote internal class without the name of its father class, eg: InternalClass class; */
            if (typeDecl.name.replace(navigateFromGavCu.cuName + ".", "").equals(navigateTo)) {
                return makePath(navigateFromGavCu);
            }

            /*case 2: quote internal class with the name of its father class, eg: ThisClass.InternalClass class; */
            if (typeDecl.name.equals(navFromPkg + "." + navigateTo)) {
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
            candidateSet = filterCandidatesByClasspath(candidateSet, classpathSet);
        }
        Set<String> importFilteredCandidate = null;

        /*Rule 3: The specific imported CU*/
        if (navFromImports != null && navFromImports.size() != 0) {
            importFilteredCandidate = filterCandidatesByImports(candidateSet, navFromImports);
            for (String gavCu : importFilteredCandidate) {
                GavCu candidate = new GavCu(gavCu);
                return makePath(candidate);
            }
        }

        /*====Rule 4: same package Types, get current package and use it to filter the index candidates*/
        Set<String> result = new HashSet<>(candidateSet);
        result.removeIf(candidate -> !substringPkgName(candidate).equals(navFromPkg));
        if (!result.isEmpty()) {
            return makePath(result.iterator().next());
        }

        return null;
    }

    private String substringPkgName(String gavCuString) {
        String typeName = gavCuString.substring(gavCuString.lastIndexOf(":") + 1, gavCuString.length());
        return typeName.substring(0, typeName.lastIndexOf("."));
    }

    private Set<String> filterCandidatesByClasspath(final Set<String> candidateSet, final Set<String> classpathSet) {
        Set<String> resultCandidates = new HashSet<>(candidateSet);
        resultCandidates.removeIf(candidate -> !classpathSet.contains(candidate.substring(0, candidate.lastIndexOf(':'))));
        return resultCandidates;
    }

    private Set<String> filterCandidatesByImports(final Set<String> candidateSet, final List<ImportDecl> imports) {
        Set<String> resultCandidates = new HashSet<>(candidateSet);
        resultCandidates.removeIf(candidate -> !imports.stream().anyMatch(value -> value.name.equals(candidate.substring(candidate.lastIndexOf(':') + 1, candidate.length()))));
        return resultCandidates;
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

    private String makePath(String gavCu) {
        GavCu resultGavCu = new GavCu();
        String[] tokens = gavCu.split(":");
        resultGavCu.group = tokens[0];
        resultGavCu.artifact = tokens[1];
        resultGavCu.version = tokens[2];
        resultGavCu.cuName = tokens[3];
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
//            importDeclList.add(new ImportDecl(cuDecl.packageDecl.name));
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
