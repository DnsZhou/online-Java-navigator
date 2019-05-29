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

    static final ObjectMapper objectMapper = new ObjectMapper();

    public String resolve(String groupId, String artifactId, String version, String compilationUnit, String to, List<String> classpathGAVs) throws IOException {

        List<String> gavsContainingMatchingCUs = findGAVsContaining(to);

        Set<String> classpathSet = new HashSet<>(classpathGAVs);
        Set<String> candidateSet = new HashSet<>(gavsContainingMatchingCUs);
        candidateSet.retainAll(classpathSet);

        List<ImportDecl> imports = getImports(groupId, artifactId, version, compilationUnit);
        // filter candidates against imports

        for(String gav : gavsContainingMatchingCUs) {
            if(!candidateSet.contains(gav)) {
                continue;
            }


            List<TypeDecl> declaredTypes = getAllDeclaredTypes(gav);

            System.out.println(declaredTypes);

            for(TypeDecl typeDecl : declaredTypes) {
                if(typeDecl.name.equals(to)) {
                    return makePath(gav, typeDecl.name);
                }
            }




        }

        return null;
    }

    private String makePath(String gav, String type) {

        String[] tokens = gav.split(":");
        String groupId = tokens[0];
        String artifactId = tokens[1];
        String version = tokens[2];

        return "/home/jhalli/tmp/htdocs/"+groupId+"/"+artifactId+"/"+version+"/"+artifactId+"-"+version+"/"+type+".html";
    }


    private List<String> findGAVsContaining(String typename) throws IOException {

        File file = new File("/home/jhalli/tmp/generated/index", typename);

        List<String> gavs = Files.readAllLines(file.toPath());

        return gavs;
    }

    public List<TypeDecl> getAllDeclaredTypes(String gav) throws IOException {

        File file = new File("/home/jhalli/tmp/generated/json/"+gav.replaceAll(":", "/")+"/package.json");
        GAVIndex gavIndex = objectMapper.readValue(file, GAVIndex.class);

        List<TypeDecl> results = new ArrayList<>();
        for(CompilationUnitDecl compilationUnitDecl : gavIndex.compilationUnitDecls) {
            for(TypeDecl typeDecl : compilationUnitDecl.typeDecls) {
                results.add(typeDecl);
            }
        }

        return results;
    }


    private List<ImportDecl> getImports(String groupId, String artifactId, String version, String compilationUnit) throws IOException {

        File file = new File("/home/jhalli/tmp/generated/json/"+groupId+"/"+artifactId+"/"+version+"/"+artifactId+"-"+version+"/"+compilationUnit.replace(".", "/")+".json");
        CompilationUnitDecl compilationUnitDecl = objectMapper.readValue(file, CompilationUnitDecl.class);

        List<ImportDecl> importDeclList = new ArrayList<>(Arrays.asList(compilationUnitDecl.importDecls));

        importDeclList.add(new ImportDecl(compilationUnit.substring(0, compilationUnit.lastIndexOf('.'))));

        return importDeclList;
    }



}
