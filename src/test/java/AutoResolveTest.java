import org.junit.jupiter.api.Test;
import uk.ac.ncl.cs.tongzhou.navigator.RepositoryWalker;
import uk.ac.ncl.cs.tongzhou.navigator.Resolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class AutoResolveTest {
    @Test
    public void testResolve() throws IOException {
        Files.walkFileTree(RepositoryWalker.outputTestCaseFileRootDir.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                if (file.toFile().isFile() && file.toFile().getName().endsWith(".csv")) {
                    byte[] inputBytes = Files.readAllBytes(file);
                    if (inputBytes.length == 0) {
                        return FileVisitResult.CONTINUE;
                    }
                    String contents = new String(inputBytes);
                    String[] contentsArray = contents.split("\r\n");
                    List<String> testCasesList = new ArrayList<String>(Arrays.asList(contentsArray));
                    testCasesList.forEach(testcase -> {
                        try {
                            String resolveResult = resolveByString(testcase);
                            if (resolveResult == null)
                                System.out.println("Resolve(" + testcase + "): Error ==" + resolveResult);
                        } catch (Exception e) {
                            System.out.println("Error while processing test case file:" + file);
                            e.printStackTrace();
                        }
                    });
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private String resolveByString(String gavCuTo) throws IOException {
        Resolver resolver = new Resolver();
        String[] gavCuTokens = gavCuTo.split(",");
        String groupId = gavCuTokens[0];
        String artifactId = gavCuTokens[1];
        String version = gavCuTokens[2];
        String compilationUnit = gavCuTokens[3];
        String from = gavCuTokens[3];
        String to = gavCuTokens[4];
        List<String> classpath = null;
        return resolver.resolve(groupId, artifactId, version, compilationUnit, from, to);
    }
}
