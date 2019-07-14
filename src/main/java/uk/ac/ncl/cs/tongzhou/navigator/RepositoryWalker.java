/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ncl.cs.tongzhou.navigator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;

/**
 * Driver for the parsing and html generation task.
 */
public class RepositoryWalker {
    static String TARGET_EXTENSION = "html";
    static int allFileAmount = 0;
    static int existFileAmount = 0;
    static int errorFileAmount = 0;

    static List<LinkObject> linkObjectListInCurrentCu;

    //Switches for TEST use ONLY
    public static final boolean GENERATE_ID_FOR_ALL = false;
    public static final boolean DEBUG_MODE = false;
    public static final boolean GENERATE_PACKAGE_INFO = true;
    public static final boolean GENERATE_TEST_CASES = false;

    // TODO change me to an empty dir where the index output will be written
    public static final String INDEX_PATH = "tmp" + SLASH + "output" + SLASH + "Index";

    static final ObjectMapper objectMapper = new ObjectMapper();

    // TODO change me to an empty dir where the error output will be written
    static File outputErrorFileRootDir = new File("tmp" + SLASH + "output" + SLASH + "ErrorDocs");

    // TODO change me to an empty dir where the test case output will be written
    public static File outputTestCaseFileRootDir = new File("tmp" + SLASH + "output" + SLASH + "TestCaseDocs");

    static File outputIndexRootDir = new File(INDEX_PATH);

    // TODO change me to an empty dir where the output will be written
    static File outputHtmlRootDir = new File("tmp" + SLASH + "output" + SLASH + TARGET_EXTENSION + "Docs"); // expect ~227021 files.
    static File outputJsonRootDir = new File("tmp" + SLASH + "output" + SLASH + "JsonDocs");

    public static void processRepository() throws Exception {
        // TODO change me to the location of the repository root
        File inputRepoRootDir = new File("tmp" + SLASH + "input" + SLASH + "Repository");
//        File inputRepoRootDir = new File("tmp" + SLASH + "input" + SLASH + "funcTestRepository");

        if (DEBUG_MODE) {
            System.out.println("Debugging error files in " + outputErrorFileRootDir.toPath());
            RepositoryWalker instance = new RepositoryWalker();
            instance.processDebugRepository();

        } else {
            //Analysis Repository
            System.out.println("Analysing repository: " + inputRepoRootDir.getAbsolutePath() + "  ... ");
            allFileAmount = JarFileScanner.countJavaFiles(inputRepoRootDir);
            System.out.println("Done. " + allFileAmount + " java files found in this repository.");

            //Parse Repository
            System.out.println("== Start Parsing Repository ==");
            RepositoryWalker instance = new RepositoryWalker();
            instance.processRepository(inputRepoRootDir, outputHtmlRootDir, outputJsonRootDir);
            System.out.println("== Parsing Completed.\n" + (allFileAmount - existFileAmount - errorFileAmount) + " file processed.");
            if (existFileAmount > 0) {
                System.out.println(existFileAmount + " file already exist. ");
            }
            if (errorFileAmount > 0) {
                System.out.println(errorFileAmount + " file got error during the process. ");
            }
            System.out.println("==");

            //Index Repository
            System.out.println("== Start Indexing Repository ==");
            instance.indexRepository();
            System.out.println("== All process finished ==");
        }
    }

    private void processDebugRepository() throws IOException {
        File debugOutput = new File(outputErrorFileRootDir.getPath(), "DebugResult");
        Files.walkFileTree(outputErrorFileRootDir.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                if (file.toFile().isFile() && file.toFile().getName().endsWith(".java")) {
                    byte[] inputBytes = Files.readAllBytes(file);
                    processCompilationUnit(inputBytes, debugOutput.toPath(), debugOutput, null, null);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void processRepository(File inputRepoRootDir, File outputHtmlRootDir, File outputJsonRootDir) throws IOException {
        Files.walkFileTree(inputRepoRootDir.toPath(), new SimpleFileVisitor<Path>() {
            int processedFileCount = 0;
            int currentPercentage = 0;

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {

                if (file.toFile().isFile() && file.toFile().getName().endsWith("-sources.jar")) {
                    String relativePath = file.toString()
                            .replace(inputRepoRootDir.getPath(), "")
                            .replace("-sources.jar", "");
                    File targetDir = new File(outputHtmlRootDir, relativePath);
                    Files.createDirectories(targetDir.toPath());

                    File targetJsonDir = new File(outputJsonRootDir, relativePath);
                    Files.createDirectories(targetJsonDir.toPath());

                    /**
                     * To solve the separator problem with regular expression
                     */
                    String[] pathTokens = relativePath.substring(1).split(SLASH.equals("\\") ? "\\\\" : SLASH);

                    String artifact = pathTokens[pathTokens.length - 3];
                    String version = pathTokens[pathTokens.length - 2];
                    String group = relativePath.substring(1).replace(SLASH + artifact + SLASH + version + SLASH + artifact + "-" + version, "");
                    group = group.replace(SLASH, ".");
                    PackageInfo currentPackageInfo = new PackageInfo();
                    String gavString = group + ":" + artifact + ":" + version;

                    File packageInfoFile = new File(targetJsonDir, "package.json");

                    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file.toFile()));
                    ZipEntry zipEntry = zipInputStream.getNextEntry();

                    while (zipEntry != null) {
                        if (zipEntry.getName().endsWith(".java")) {

                            byte[] inputBytes = zipInputStream.readAllBytes();

                            String cuPath = zipEntry.getName().replace(".java", "");
                            String gavCuString = gavString + ":" + cuPath.replace(SLASH, ".").replace("/", ".");

                            File outputFile = new File(targetDir, zipEntry.getName().replace(".java", "." + TARGET_EXTENSION));
                            File outputJsonFile = new File(targetJsonDir, zipEntry.getName().replace(".java", ".json"));


                            if (!outputFile.exists()) {

                                outputJsonFile.getParentFile().mkdirs();
                                outputFile.getParentFile().mkdirs();

                                byte[] outputBytes = processCompilationUnit(inputBytes, outputFile.toPath(), outputJsonFile, currentPackageInfo, gavCuString);

                                if (outputBytes != null) {
                                    Files.write(outputFile.toPath(), outputBytes);
                                }

                            } else {
                                System.out.println("target Html file exists " + file.toFile().getAbsolutePath() + " " + cuPath);
                                existFileAmount++;
                            }
                            printPercentage();
                        }
                        zipEntry = zipInputStream.getNextEntry();
                    }
                    zipInputStream.close();
                    if (GENERATE_PACKAGE_INFO && currentPackageInfo.compilationUnitDecls != null && currentPackageInfo.compilationUnitDecls.length != 0)
                        objectMapper.writeValue(packageInfoFile, currentPackageInfo);
                }
                return FileVisitResult.CONTINUE;
            }

            private void printPercentage() {
                this.processedFileCount++;
                int newPercentage = (processedFileCount * 1000) / allFileAmount;
                if (newPercentage != this.currentPercentage) {
                    this.currentPercentage = newPercentage;
                    System.out.println((double) newPercentage / 10 + "% " + new Date());
                }
            }
        });

    }

    private byte[] processCompilationUnit(byte[] inputBytes, Path outputPath, File outputJsonFile, PackageInfo currentPackageInfo, String gavCuString) {
//        File outputTestCaseFile = new File(outputTestCaseFileRootDir,)
        byte[] bytesOut;
        String outputString = null;
        try {
            CompilationUnit compilationUnit = parseWithFallback(inputBytes);
            PackageDeclaration packageDeclaration =
                    compilationUnit.getPackageDeclaration().isPresent() ? compilationUnit.getPackageDeclaration().get() : new PackageDeclaration();

            JavaSymbolSolver javaSymbolSolver = new JavaSymbolSolver(new DummyTypeSolver());
            javaSymbolSolver.inject(compilationUnit);
            LinkingVisitor linkingVisitor = new LinkingVisitor();
            linkingVisitor.visit(compilationUnit, javaSymbolSolver);
            linkObjectListInCurrentCu = new ArrayList<>();

            switch (TARGET_EXTENSION) {
                case "html":
                    outputString = StacklessPrinterDriver.print(compilationUnit, new HtmlPrinter());
                    break;
                default:
                    outputString = StacklessPrinterDriver.print(compilationUnit, new Printer());
                    break;
            }


            List<String> typeDeclarations = linkingVisitor.getDeclaredTypes();
            List<ImportDeclaration> importDeclarations = linkingVisitor.getImportDeclarations();
            CompilationUnitDecl compilationUnitDecl = new CompilationUnitDecl(typeDeclarations, importDeclarations, packageDeclaration);

            objectMapper.writeValue(outputJsonFile, compilationUnitDecl);

            if (GENERATE_PACKAGE_INFO) {
                compilationUnitDecl.importDecls = null;
                currentPackageInfo.add(compilationUnitDecl);
            }

            if (GENERATE_TEST_CASES && linkObjectListInCurrentCu != null && !linkObjectListInCurrentCu.isEmpty()) {
                List<String> gavCuToList = linkObjectListInCurrentCu.stream().map(linkObject -> gavCuString.replace(":", ",") + "," + linkObject.navFrom + "," + linkObject.navTo).distinct().collect(Collectors.toList());
                File testCaseFile = new File(outputTestCaseFileRootDir, gavCuString.replace(":", "_") + ".csv");
                testCaseFile.getParentFile().mkdirs();
                Files.write(testCaseFile.toPath(), gavCuToList, StandardCharsets.UTF_8);
            }

            bytesOut = outputString.getBytes(StandardCharsets.UTF_8);
        } catch (Throwable e) {
            System.out.println("error for " + outputPath.toFile().getAbsolutePath() + " " + e.toString());
            errorFileAmount++;
            try {
                File outputFile = new File(outputErrorFileRootDir.getAbsolutePath(), outputPath.toString().replace(".html", ".java"));
                outputFile.getParentFile().mkdirs();
                Files.write(outputFile.toPath(), inputBytes);
            } catch (Exception fe) {
                System.out.println("ERROR: can not generate the error file to folder: " + fe.toString());
            }
            return null;
        }

        return bytesOut;
    }

    private CompilationUnit parseWithFallback(byte[] inputBytes) {

        ParserConfiguration.LanguageLevel[] levels = new ParserConfiguration.LanguageLevel[]{
                ParserConfiguration.LanguageLevel.JAVA_11,
                // fallback to 8 deals with '_' as a var name.
                ParserConfiguration.LanguageLevel.JAVA_8,
                // fallback to 1_4 deals with 'enum' as a var name.
                ParserConfiguration.LanguageLevel.JAVA_1_4
        };

        CompilationUnit compilationUnit = null;
        for (int i = 0; i < levels.length && compilationUnit == null; i++) {
            compilationUnit = tryParseAtLevel(inputBytes, levels[i], false);

            /** If parsing failed with all version of java, then try parsing with Unicode Escapes*/
            if (compilationUnit == null)
                compilationUnit = tryParseAtLevel(inputBytes, levels[i], true);
        }

        return compilationUnit;
    }

    private CompilationUnit tryParseAtLevel(byte[] inputBytes, ParserConfiguration.LanguageLevel languageLevel, boolean preprocessUnicodeEscapes) {

        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setLanguageLevel(languageLevel);
        parserConfiguration.setAttributeComments(false);

        //Solve bug OJN-28 Error while processing Unicode Escaping
        parserConfiguration.setPreprocessUnicodeEscapes(preprocessUnicodeEscapes);

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(inputBytes);
            Provider provider = new StreamProvider(byteArrayInputStream);
            ParseResult<CompilationUnit> result =
                    new JavaParser(parserConfiguration).parse(ParseStart.COMPILATION_UNIT, provider);

            if (!result.isSuccessful()) {
                return null;
            } else {
                return result.getResult().get();
            }

        } catch (Exception e) {
            return null;
        }
    }

    private void indexRepository() throws IOException {
        //Todo: scan all the index file in the output/JsonDocs folder and generate index into index/ folder
        /*
        Index format:
        <group>:<artifact>:<version>:<declaration name>
        eg  antlr:antlr:2.7.7.redhat-7:antlr.actions.cpp.ActionLexer
         */
        Files.createDirectories(outputIndexRootDir.toPath());
        Files.walkFileTree(outputJsonRootDir.toPath(), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {

                //Todo: think about whether change it to a special name
                if (file.toFile().isFile() && file.toFile().getName().equals("package.json")) {
                    String relativePath = file.toString()
                            .replace(outputJsonRootDir.getPath(), "")
                            .replace(SLASH + "package.json", "");

                    //To solve the separator problem with regular expression
                    String[] pathTokens = relativePath.substring(1).split(SLASH.equals("\\") ? "\\\\" : SLASH);

                    String artifact = pathTokens[pathTokens.length - 3];
                    String version = pathTokens[pathTokens.length - 2];
                    String group = relativePath.substring(1).replace(SLASH + artifact + SLASH + version + SLASH + artifact + "-" + version, "");
                    group = group.replace(SLASH, ".");
                    PackageInfo currentPackageInfo = objectMapper.readValue(file.toFile(), PackageInfo.class);
                    for (CompilationUnitDecl cuItem : currentPackageInfo.compilationUnitDecls) {
                        for (TypeDecl typeDecl : cuItem.typeDecls) {
                            String typeName = typeDecl.name.substring(typeDecl.name.lastIndexOf(".") + 1, typeDecl.name.length());
                            String gavCuString = group + ":" + artifact + ":" + version + ":" + typeDecl.name;
                            IndexType.addIndexItem(typeName, gavCuString);
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        IndexType.generateIndex();
    }


}
