package uk.ac.ncl.cs.tongzhou.navigator;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;

import static uk.ac.ncl.cs.tongzhou.navigator.Util.subStringLastDot;

public class Resolver {
    static final ObjectMapper objectMapper = new ObjectMapper();

    public String resolve(String groupId, String artifactId, String version,
                          String compilationUnit, String navigateFrom, String navigateTo, List<String> classpathGAVs) throws IOException {
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

        /*TODO: Solve @interface type not found in .json file/
        /*====Rule2: target type is an nested type of current type*/
        if (navFromTypeDecls != null && !navFromTypeDecls.isEmpty()) {
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
        }


        /* Find the index file with the type name;*/
        List<String> gavsContainingMatchingCUs = findGAVsContaining(navigateTo);

        if (gavsContainingMatchingCUs != null && !gavsContainingMatchingCUs.isEmpty()) {
            /*Todo: ask what if current package is not defined in classPath?*/
            Set<String> candidateSet = new HashSet<>(gavsContainingMatchingCUs);
            Set<String> candidateSetWithClassPath = new HashSet<>(candidateSet);
            if (classpathGAVs != null && !classpathGAVs.isEmpty()) {
                Set<String> classpathSet = new HashSet<>(classpathGAVs);
                candidateSetWithClassPath = filterCandidatesByClasspath(candidateSetWithClassPath, classpathSet);
            }

            /*Todo: fix X.X type for specific single type import*/
            /*====Rule 3: The specific single type imported Type*/
            if (navFromImports != null && !navFromImports.isEmpty()) {
                List<String> importStringList = navFromImports.stream().map(importDecl -> importDecl.name).collect(Collectors.toList());
                Set<String> importFilteredCandidates = filterCandidatesByImports(candidateSetWithClassPath, importStringList);
                for (String gavCu : importFilteredCandidates) {
                    GavCu candidate = new GavCu(gavCu);
                    return makePath(candidate);
                }
            }

            /*====Rule 4: same package Types, get current package and use it to filter the index candidates*/
            Set<String> result = new HashSet<>(candidateSetWithClassPath);
            result.removeIf(candidate -> !substringPkgName(candidate).equals(navFromPkg));
            if (!result.isEmpty()) {
                return makePath(result.iterator().next());
            }

            /*====Rule 5: on demand import, aka wildcard * import*/
            if (navFromImports != null && !navFromImports.isEmpty()) {
                List<String> tryImportDecls = navFromImports.stream().filter(importDecl -> importDecl.name.contains("*")).map(importDecl
                        -> importDecl.name.replace("*", navigateTo)).collect(Collectors.toList());
                Set<String> ondemandImportFilteredCandidates = filterCandidatesByImports(candidateSetWithClassPath, tryImportDecls);
                for (String gavCu : ondemandImportFilteredCandidates) {
                    GavCu candidate = new GavCu(gavCu);
                    return makePath(candidate);
                }
            }

            /*====Rule 6: Default imported Type: java.lang*/
            String langResult = candidateSet.stream().filter(candidate -> substringPkgName(candidate).equals("java.lang")).findAny().orElse(null);
            if (langResult != null) {
                return makePath(langResult);
            }
        }
        /*Todo: think about X.X nested type for Rule 6*/
        /*Rule 7: Directly referred Type eg: com.google.testClass test = new com.google.testClass()*/
        String directRefTypeName = navigateTo.substring(navigateTo.lastIndexOf(".") + 1, navigateTo.length());
        List<String> gavsContainingMatchingCUsForDR = findGAVsContaining(directRefTypeName);
        if (gavsContainingMatchingCUsForDR != null && !gavsContainingMatchingCUsForDR.isEmpty()) {
            Set<String> candidateSet = new HashSet<>(gavsContainingMatchingCUsForDR);
            candidateSet.removeIf(candidate -> !substringTypegName(candidate).equals(navigateTo));
            if (candidateSet.size() > 0) {
                return makePath(candidateSet.iterator().next());
            }
        }

        //Todo: return readable message showing that the Type is not found in the system
        return null;
    }

    private String substringPkgName(String gavCuString) {
        String typeName = substringTypegName(gavCuString);
        return typeName.substring(0, typeName.lastIndexOf("."));
    }

    private String substringTypegName(String gavCuString) {
        return gavCuString.substring(gavCuString.lastIndexOf(":") + 1, gavCuString.length());
    }

    private Set<String> filterCandidatesByClasspath(final Set<String> candidateSet, final Set<String> classpathSet) {
        Set<String> resultCandidates = new HashSet<>(candidateSet);
        resultCandidates.removeIf(candidate -> !classpathSet.contains(candidate.substring(0, candidate.lastIndexOf(':'))));
        return resultCandidates;
    }

//    private Set<String> filterCandidatesByImports(final Set<String> candidateSet, final List<ImportDecl> imports) {
//        Set<String> resultCandidates = new HashSet<>(candidateSet);
//        resultCandidates.removeIf(candidate -> !imports.stream().anyMatch(value -> value.name.equals(candidate.substring(candidate.lastIndexOf(':') + 1, candidate.length()))));
//        return resultCandidates;
//    }

    private Set<String> filterCandidatesByImports(final Set<String> candidateSet, final List<String> imports) {
        Set<String> resultCandidates = new HashSet<>(candidateSet);
        resultCandidates.removeIf(candidate -> !imports.stream().anyMatch(value -> value.equals(candidate.substring(candidate.lastIndexOf(':') + 1, candidate.length()))));
        return resultCandidates;
    }

    public String resolve(String groupId, String artifactId, String version, String compilationUnit, String navFrom, String navTo) throws IOException {
        return resolve(groupId, artifactId, version, compilationUnit, navFrom, navTo, null);
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
        return makePath(new GavCu(gavCu));
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
