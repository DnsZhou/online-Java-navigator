import org.junit.jupiter.api.Test;
import uk.ac.ncl.cs.tongzhou.navigator.Resolver;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;

public class WinCaseSensitiveTest {
    @Test
    public void testCaseSensitiveOnWin() throws IOException {
        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.ActionElement";
        String from = "antlr.ActionElement";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String to = "Id";
        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, classpath);
        System.out.println(result);

    }
}
