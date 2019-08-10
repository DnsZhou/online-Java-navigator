package uk.ac.ncl.cs.tongzhou.navigator;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ncl.cs.tongzhou.navigator.awsservice.S3Handler;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.CompilationUnitDecl;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.GavCu;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.ImportDecl;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.TypeDecl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;
import static uk.ac.ncl.cs.tongzhou.navigator.Util.subStringLastDot;

public class Resolver {
    public static String CLASSPATH_NOT_INCLUDED_RESULT = "NOT_INCLUDED_IN_CLASSPATH";

    // TODO change me to dir where the customized classpath file is
    static File customizeClassFile = new File("tmp" + SLASH + "input" + SLASH + "classpath");
    public static boolean RESOLVE_WITH_S3 = false;
    private List<String> currentClasspathGavs = null;

    static final ObjectMapper objectMapper = new ObjectMapper();

    public String resolve(String groupId, String artifactId, String version,
                          String compilationUnit, String navigateFrom, String navigateTo, List<String> classpathGAVs) throws IOException {
        this.currentClasspathGavs = classpathGAVs;
        GavCu navigateFromGavCu = new GavCu(groupId, artifactId, version, compilationUnit);

        /*Get information of current Type from GavCu*/
        CompilationUnitDecl cuDecl = getCompilationUnitDecl(navigateFromGavCu);
        String navFromPkg = getPackage(cuDecl);
        List<ImportDecl> navFromImports = getImports(cuDecl);
        List<TypeDecl> navFromTypeDecls = getTypeDecls(cuDecl);
        String fullNavFromString = compilationUnit.substring(0, compilationUnit.lastIndexOf(".") + 1) + navigateFrom;

        /*====Rule 1: Current Type itself*/
        if (navigateTo.equals(subStringLastDot(navigateFromGavCu.cuName.replace(navFromPkg + ".", "")))) {
            return validateAndMakePath(Arrays.asList(navigateFromGavCu).stream(), null);
        }

        /*====Rule2: target type is an nested type of current type*/
        if (navFromTypeDecls != null && !navFromTypeDecls.isEmpty()) {
            for (TypeDecl typeDecl : navFromTypeDecls) {
                /*case 1: quote internal class without the name of its father class, eg: InternalClass class; */
                if (typeDecl.name.replace(fullNavFromString + ".", "").equals(navigateTo)) {
                    return validateAndMakePath(Arrays.asList(navigateFromGavCu).stream(), null);
                }

                /*case 2: quote internal class with the name of its father class, eg: ThisClass.InternalClass class; */
                if (typeDecl.name.equals(fullNavFromString.substring(0, fullNavFromString.lastIndexOf(".")) + "." + navigateTo)) {
                    return validateAndMakePath(Arrays.asList(navigateFromGavCu).stream(), navigateTo);
                }
            }
        }


        /* Find the index file with the type name;*/
        String navigateToTypeName = navigateTo.substring(navigateTo.lastIndexOf(".") + 1, navigateTo.length());
        List<String> gavsContainingMatchingCUs = findGAVsContaining(navigateToTypeName);

        if (gavsContainingMatchingCUs != null && !gavsContainingMatchingCUs.isEmpty()) {
            Set<String> candidateSet = new HashSet<>(gavsContainingMatchingCUs);
            Set<String> candidateSetWithClassPath = new HashSet<>(candidateSet);
            if (this.currentClasspathGavs != null && !this.currentClasspathGavs.isEmpty()) {
                Set<String> classpathSet = new HashSet<>(this.currentClasspathGavs);
                candidateSetWithClassPath = filterCandidatesByClasspath(candidateSetWithClassPath, classpathSet);
            }

            /*====Rule 3: The specific single type imported Type*/
            if (navFromImports != null && !navFromImports.isEmpty()) {
                List<String> importStringList = navFromImports.stream().map(importDecl -> importDecl.name).collect(Collectors.toList());
                Set<String> importFilteredCandidates = filterCandidatesByImports(candidateSetWithClassPath, importStringList);
                if (importFilteredCandidates.isEmpty()) {
                    /*====Rule 3.1 nested type for specific imported case*/
                    if (navigateTo.contains(".")) {
                        String parentTypeString = navigateTo.substring(0, navigateTo.indexOf("."));
                        List<String> filteredImportForCandidate = importStringList.stream()
                                .filter(imp -> imp.substring(imp.lastIndexOf(".") + 1, imp.length()).equals(parentTypeString))
                                .collect(Collectors.toList());
                        List<String> candidateImportStrings = filteredImportForCandidate.stream()
                                .map(str -> str.substring(0, str.lastIndexOf(".")) + "." + navigateTo)
                                .collect(Collectors.toList());
                        importFilteredCandidates = filterCandidatesByImports(candidateSetWithClassPath, candidateImportStrings);
                    }
                }
                if (importFilteredCandidates != null && importFilteredCandidates.size() != 0) {
                    return validateAndMakePath(importFilteredCandidates.stream().map(candidate -> new GavCu(candidate)), navigateTo);
                }
            }


            /*====Rule 4: same package Types, get current package and use it to filter the index candidates*/
            Set<String> result = new HashSet<>(candidateSetWithClassPath);
//            navigateTo
            result.removeIf(candidate -> substringPkgName(candidate, navigateTo) == null
                    || !substringPkgName(candidate, navigateTo).equals(navFromPkg));
            if (!result.isEmpty()) {
                return validateAndMakePath(result.stream().map(candidate -> new GavCu(candidate)), navigateTo);
            }

            /*Todo: fix X.X type for specific single type import*/
            /*====Rule 5: on demand import, aka wildcard * import*/
            if (navFromImports != null && !navFromImports.isEmpty()) {
                List<String> tryImportDecls = navFromImports.stream().filter(importDecl -> importDecl.name.contains("*")).map(importDecl
                        -> importDecl.name.replace("*", navigateTo)).collect(Collectors.toList());
                Set<String> ondemandImportFilteredCandidates = filterCandidatesByImports(candidateSetWithClassPath, tryImportDecls);
                if (ondemandImportFilteredCandidates != null && ondemandImportFilteredCandidates.size() > 0) {
                    return validateAndMakePath(ondemandImportFilteredCandidates.stream().map(candidate -> new GavCu(candidate)), null);
                }
            }

            /*====Rule 6: Default imported Type: java.lang*/
            List<String> langResult = candidateSet.stream().filter(candidate -> substringPkgName(candidate, navigateTo) != null
                    && substringPkgName(candidate, navigateTo).equals("java.lang")).collect(Collectors.toList());
            if (langResult != null && langResult.size() > 0) {
                return validateAndMakePath(langResult.stream().map(candidate -> new GavCu(candidate)), null);
            }
        }
        /*Todo: fix X.X type for specific single type import*/
        /*Rule 7: Directly referred Type eg: com.google.testClass test = new com.google.testClass()*/
        String directRefTypeName = navigateTo.substring(navigateTo.lastIndexOf(".") + 1, navigateTo.length());
        List<String> gavsContainingMatchingCUsForDR = findGAVsContaining(directRefTypeName);
        if (gavsContainingMatchingCUsForDR != null && !gavsContainingMatchingCUsForDR.isEmpty()) {
            Set<String> candidateSet = new HashSet<>(gavsContainingMatchingCUsForDR);
            candidateSet.removeIf(candidate -> !substringTypeName(candidate).equals(navigateTo));
            if (candidateSet.size() > 0) {
                return validateAndMakePath(candidateSet.stream().map(candidate -> new GavCu(candidate)), null);
            }
        }

        return null;
    }

    private String substringPkgName(String gavCuString, String navigateTo) {
        String typeName = substringTypeName(gavCuString);
        if (!typeName.contains(navigateTo)) {
            return null;
        } else {
            return typeName.replace("." + navigateTo, "");
        }
    }

    private String substringTypeName(String gavCuString) {
        return gavCuString.substring(gavCuString.lastIndexOf(":") + 1, gavCuString.length());
    }

    private Set<String> filterCandidatesByClasspath(final Set<String> candidateSet, final Set<String> classpathSet) {
        Set<String> resultCandidates = new HashSet<>(candidateSet);
        resultCandidates.removeIf(candidate -> !classpathSet.contains(candidate.substring(0, candidate.lastIndexOf(':'))));
        return resultCandidates;
    }

    private boolean validateGavCuInClasspath(GavCu result) {
        if (this.currentClasspathGavs == null || this.currentClasspathGavs.size() == 0) {
            return true;
        } else {
            String candidate = result.group + ":" + result.artifact + ":" + result.version;
            return this.currentClasspathGavs.contains(candidate);
        }
    }

    private boolean validateGavCuInSingleClasspath(GavCu candidate, String classpath) {
        return classpath.equals(candidate.group + ":" + candidate.artifact + ":" + candidate.version);
    }

//    private boolean validateGavCuStringInClasspath(String result) {
//        if (this.currentClasspathGavs == null || this.currentClasspathGavs.size() == 0) {
//            return true;
//        } else {
//            String[] candidateSet = result.split(":");
//            String candidate = candidateSet[0] + ":" + candidateSet[1] + ":" + candidateSet[2];
//            return this.currentClasspathGavs.contains(candidate);
//        }
//    }

    private Set<String> filterCandidatesByImports(final Set<String> candidateSet, final List<String> imports) {
        Set<String> resultCandidates = new HashSet<>(candidateSet);
        resultCandidates.removeIf(candidate -> !imports.stream().anyMatch(value -> value.equals(candidate.substring(candidate.lastIndexOf(':') + 1, candidate.length()))));
        return resultCandidates;
    }

    public String resolve(String groupId, String artifactId, String version, String compilationUnit, String navFrom, String navTo) throws IOException {
        return resolve(groupId, artifactId, version, compilationUnit, navFrom, navTo, null);
    }

    public List<String> findGavCusByTypeName(String typeName) throws IOException {
        return findGAVsContaining(typeName);
    }

    private String makePath(GavCu gavCu, String navTo) {
        if (gavCu != null) {
            String groupId = gavCu.group;
            String artifactId = gavCu.artifact;
            String version = gavCu.version;
            String[] cuInfo = gavCu.cuName.split("[.]");
            String toId = gavCu.cuName;
            if (navTo != null && navTo.contains(".")) {
                String actualCu = gavCu.cuName.replace(navTo.substring(navTo.indexOf("."), navTo.length()), "");
                cuInfo = actualCu.split("[.]");
                toId = actualCu + "." + navTo.substring(navTo.indexOf(".") + 1, navTo.length());
            }
            String pathResult = "";

            pathResult = pathResult.concat(groupId.replace(".", "/") + "/" + artifactId + "/"
                    + version + "/" + artifactId + "-" + version);
            for (String info : cuInfo) {
                pathResult = pathResult.concat("/").concat(info);
            }
            pathResult = pathResult.concat(".html#");
            pathResult = pathResult.concat(toId);

            return pathResult;
        }
        return null;
    }

    private String validateAndMakePath(Stream<GavCu> candidateStream, String navTo) {
        /*iterate the classpath in order, when find the candidate in any classpath, make the path with this candidate*/
        /*which means, if there is multiple candidate, the one who is listed upper in classpath will be used.*/
        if (this.currentClasspathGavs != null && this.currentClasspathGavs.size() != 0) {
            List<GavCu> candidates = candidateStream.collect(Collectors.toList());
            for (String classpath : this.currentClasspathGavs) {
                GavCu result = candidates.stream().filter(candidate -> validateGavCuInSingleClasspath(candidate, classpath)).findFirst().orElse(null);
                if (result != null) {
                    return makePath(result, navTo);
                }
            }
            return CLASSPATH_NOT_INCLUDED_RESULT;
        }
        return makePath(candidateStream.findAny().orElse(null), navTo);
    }

    //One single property file pre type name
    private List<String> findGAVsContaining(String typename) throws IOException {
        List<String> gavs = null;
        if (RESOLVE_WITH_S3) {
            /*S3 is a case sensitive file system,but in order to make it compatible with the code in Windows
             * environment, we just double check in the @ folder. */
            String result = null;
            result = S3Handler.getStringfromS3ByPath(RepositoryWalker.outputIndexRootDir + SLASH + typename);
            if (result == null || result.isEmpty()) {
                result = S3Handler.getStringfromS3ByPath(RepositoryWalker.outputIndexRootDir.getPath() + SLASH + "@" + typename + SLASH + typename);
                if (result == null || result.isEmpty()) {
                    return null;
                }
            }
            gavs = Arrays.asList(result.replace("\r", "").split("\n"));

        } else {
            File file = new File(RepositoryWalker.outputIndexRootDir + SLASH + typename);
            if (!file.exists()) {
                return null;
            } else if (!file.getCanonicalFile().getName().equals(file.getName())) {
                file = new File(RepositoryWalker.outputIndexRootDir.getPath() + SLASH + "@" + typename, typename);
                if (!file.exists()) {
                    return null;
                }
            }
            gavs = Files.readAllLines(file.toPath());
        }
        return gavs;
    }

    /*this is the JSON model of the json file for every type*/
    private CompilationUnitDecl getCompilationUnitDecl(GavCu gavCu) throws IOException {
        File file = new File(RepositoryWalker.outputJsonRootDir + SLASH + gavCu.group.replace(".", SLASH) + SLASH
                + gavCu.artifact + SLASH + gavCu.version + SLASH + gavCu.artifact + "-" + gavCu.version + SLASH
                + gavCu.cuName.replace(".", SLASH) + ".json");
        if (RESOLVE_WITH_S3) {
            byte[] result = S3Handler.getBytefromS3ByPath(file.getPath());
            return objectMapper.readValue(result, CompilationUnitDecl.class);
        } else {
            return objectMapper.readValue(file, CompilationUnitDecl.class);
        }
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
