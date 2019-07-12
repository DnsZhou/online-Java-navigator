import org.junit.jupiter.api.Test;
import uk.ac.ncl.cs.tongzhou.navigator.RepositoryWalker;
import uk.ac.ncl.cs.tongzhou.navigator.Resolver;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AutoResolveTest {
    @Test
    public void testResolve() throws IOException {
        Files.walkFileTree(RepositoryWalker.outputTestCaseFileRootDir.toPath(), new SimpleFileVisitor<Path>() {
            int errorCount = 0;
            int caseCount = 0;

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
                    for (String testcase : testCasesList) {
                        caseCount++;
                        try {
                            String resolveResult = resolveByString(testcase);
//                            System.out.println(resolveResult);
                            if (resolveResult == null) {
                                errorCount++;
                                System.out.print("Error: Resolve(" + testcase + ") got null result, ");
                                System.out.println("Processed: " + caseCount + " Error: " + errorCount);
                            }
                        } catch (Exception e) {
                            System.out.println("Error while processing test case file:" + file);
                            e.printStackTrace();
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        System.out.println(new Date());
    }

    private String resolveByString(String gavCuTo) throws IOException {
        Resolver resolver = new Resolver();
        String[] gavCuTokens = gavCuTo.split(",");
        String groupId = gavCuTokens[0];
        String artifactId = gavCuTokens[1];
        String version = gavCuTokens[2];
        String compilationUnit = gavCuTokens[3];
        String from = gavCuTokens[4];
        String to = gavCuTokens[5];
        List<String> classpath = null;
        return resolver.resolve(groupId, artifactId, version, compilationUnit, from, to);
    }
}
