package uk.ac.ncl.cs.tongzhou.navigator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarFileScanner {
    public static int javaFileCount = 0;

    public static int countJavaFiles(File inputRepoRootDir) throws IOException {

        Files.walkFileTree(inputRepoRootDir.toPath(), new SimpleFileVisitor<Path>() {
            int javaFileCount = 0;

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                if (file.toFile().isFile() && file.toFile().getName().endsWith("-sources.jar")) {

                    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file.toFile()));
                    ZipEntry zipEntry = zipInputStream.getNextEntry();

                    while (zipEntry != null) {
                        if (zipEntry.getName().endsWith(".java")) {
                            JarFileScanner.javaFileCount++;
                        }
                        zipEntry = zipInputStream.getNextEntry();
                    }
                    zipInputStream.close();
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return javaFileCount;
    }

}
