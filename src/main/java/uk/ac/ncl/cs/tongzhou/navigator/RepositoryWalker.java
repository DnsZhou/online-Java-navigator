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
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.CompilationUnitDecl;

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
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Driver for the parsing and html generation task.
 */
public class RepositoryWalker {
    static String TARGET_EXTENSION = "html";
    static int ALL_FILE_AMOUNT = 0;

    static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * To solve the separator difference between Linux and Windows environment
     */
    static String SLASH_TAG = File.separator;

    // TODO change me to an empty dir where the error output will be written
    static File outputErrorFileRootDir = new File("tmp" + SLASH_TAG + TARGET_EXTENSION + "ErrorDocs");

    public static void processRepository() throws Exception {
        // TODO change me to the location of the repository root
        File inputRepoRootDir = new File("tmp" + SLASH_TAG + "Repository");

        // TODO change me to an empty dir where the output will be written
        File outputHtmlRootDir = new File("tmp" + SLASH_TAG + TARGET_EXTENSION + "Docs"); // expect ~227021 files.

        System.out.println("Preprocessing repository: " + inputRepoRootDir.getAbsolutePath() + "  ... ");
        ALL_FILE_AMOUNT = JarFileScanner.countJavaFiles(inputRepoRootDir);
        System.out.println("Done. " + ALL_FILE_AMOUNT + " java files found in this repository.");

        System.out.println("== Start Parsing Repository ==");
        RepositoryWalker instance = new RepositoryWalker();
        instance.processRepository(inputRepoRootDir, outputHtmlRootDir);
    }

    private void processRepository(File inputRepoRootDir, File outputHtmlRootDir) throws IOException {
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

                    /**
                     * To solve the separator problem with regular expression
                     */
                    String[] pathTokens = relativePath.substring(1).split(SLASH_TAG.equals("\\") ? "\\\\" : SLASH_TAG);

                    String artifact = pathTokens[pathTokens.length - 3];
                    String version = pathTokens[pathTokens.length - 2];
                    String group = relativePath.substring(1).replace(SLASH_TAG + artifact + SLASH_TAG + version + SLASH_TAG + artifact + "-" + version, "");
                    group = group.replace(SLASH_TAG, ".");

                    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file.toFile()));
                    ZipEntry zipEntry = zipInputStream.getNextEntry();

                    while (zipEntry != null) {
                        if (zipEntry.getName().endsWith(".java")) {

                            byte[] inputBytes = zipInputStream.readAllBytes();

                            String cuName = zipEntry.getName().replace(".java", "");
                            File outputFile = new File(targetDir, zipEntry.getName().replace(".java", "." + TARGET_EXTENSION));

                            if (!outputFile.exists()) {

                                outputFile.getParentFile().mkdirs();

                                byte[] outputBytes = processCompilationUnit(inputBytes, outputFile.toPath());

                                if (outputBytes != null) {
                                    Files.write(outputFile.toPath(), outputBytes);
                                }

                            } else {
                                System.out.println("target file exists " + file.toFile().getAbsolutePath() + " " + cuName);
                            }
                            printPercentage();
                        }
                        zipEntry = zipInputStream.getNextEntry();
                    }
                    zipInputStream.close();
                }
                return FileVisitResult.CONTINUE;
            }

            private void printPercentage() {
                this.processedFileCount++;
                int newPercentage = (processedFileCount * 1000) / ALL_FILE_AMOUNT;
                if (newPercentage != this.currentPercentage) {
                    this.currentPercentage = newPercentage;
                    System.out.println((double) newPercentage / 10 + "% " + new Date());
                }
            }
        });

    }

    private byte[] processCompilationUnit(byte[] inputBytes, Path outputPath) {
        byte[] bytesOut;
        String outputString = null;
        try {
            CompilationUnit compilationUnit = parseWithFallback(inputBytes);

            JavaSymbolSolver javaSymbolSolver = new JavaSymbolSolver(new DummyTypeSolver());
            javaSymbolSolver.inject(compilationUnit);
            LinkingVisitor linkingVisitor = new LinkingVisitor();
            linkingVisitor.visit(compilationUnit, javaSymbolSolver);

            List<TypeDeclaration> typeDeclarations = linkingVisitor.getTypeDeclarations();
            CompilationUnitDecl compilationUnitDecl = new CompilationUnitDecl(typeDeclarations);
            File jsonFile = null;
            //objectMapper.writeValue(jsonFile, compilationUnitDecl);


            switch (TARGET_EXTENSION) {
                case "html":
                    outputString = StacklessPrinterDriver.print(compilationUnit, new HtmlPrinter());
                    break;
                default:
                    outputString = StacklessPrinterDriver.print(compilationUnit, new Printer());
                    break;
            }
            bytesOut = outputString.getBytes(StandardCharsets.UTF_8);
        } catch (Throwable e) {
            System.out.println("error for " + outputPath.toFile().getAbsolutePath() + " " + e.toString());
            try {
                File outputFile = new File(outputErrorFileRootDir.getAbsolutePath(), outputPath.toString());
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
}
