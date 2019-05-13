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

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Driver for the parsing and html generation task.
 */
public class RepositoryWalker {
    //                    solved the separator difference between Linux and Windows environment
    static String SLASH_TAG = File.separator;
    static String TARGET_EXTENSION = "java";

    public static void main(String[] args) throws Exception {

        // TODO change me to the location of the repository root
        File inputRepoRootDir = new File("." + SLASH_TAG + "tmp" + SLASH_TAG + "repository");

        // TODO change me to an empty dir where the output will be written
        File outputHtmlRootDir = new File("." + SLASH_TAG + "tmp" + SLASH_TAG + TARGET_EXTENSION +"Docs"); // expect ~227021 files.

        RepositoryWalker instance = new RepositoryWalker();
        instance.processRepository(inputRepoRootDir, outputHtmlRootDir);
    }

    private void processRepository(File inputRepoRootDir, File outputHtmlRootDir) throws IOException {
        Files.walkFileTree(inputRepoRootDir.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {

                if (file.toFile().isFile() && file.toFile().getName().endsWith("-sources.jar")) {
                    String relativePath = file.toString()
                            .replace(inputRepoRootDir.getPath(), "")
                            .replace("-sources.jar", "");
                    File targetDir = new File(outputHtmlRootDir, relativePath);
                    Files.createDirectories(targetDir.toPath());

//                    solve the separator problem with regular expression
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

//                                @TODO: compare the two byte things.
                                if (outputBytes != null) {
                                    Files.write(outputFile.toPath(), outputBytes);
                                }

                            } else {
                                System.out.println("target file exists " + file.toFile().getAbsolutePath() + " " + cuName);
                            }
                        }
                        zipEntry = zipInputStream.getNextEntry();
                    }
                    zipInputStream.close();
                }
                return FileVisitResult.CONTINUE;
            }
        });

    }

    private byte[] processCompilationUnit(byte[] inputBytes, Path outputPath) {

        CompilationUnit compilationUnit = parseWithFallback(inputBytes);
        if (compilationUnit == null) {
            return null;
        }

        byte[] bytesOut;
        String outputString = null;
        try {
            switch (TARGET_EXTENSION) {
                case "html":
                    outputString = StacklessPrinterDriver.print(compilationUnit, new HtmlPrinter());
                    break;
                default:
                    outputString = StacklessPrinterDriver.print(compilationUnit, new Printer());
                    break;
            }
            bytesOut = outputString.getBytes(StandardCharsets.UTF_8);
        } catch (Error e) {
            System.out.println("error for " + outputPath.toFile().getAbsolutePath() + " " + e.toString());
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
            compilationUnit = tryParseAtLevel(inputBytes, levels[i]);
        }

        return compilationUnit;
    }

    private CompilationUnit tryParseAtLevel(byte[] inputBytes, ParserConfiguration.LanguageLevel languageLevel) {

        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setLanguageLevel(languageLevel);
        parserConfiguration.setAttributeComments(false);

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
