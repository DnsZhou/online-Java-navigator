
import org.junit.jupiter.api.Test;
import uk.ac.ncl.cs.tongzhou.navigator.Resolver;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResolverTest {

    @Test
    public void testResolver() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.ANTLRGrammarParseBehavior";
        String to = "Token";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, to, classpath);

        assertEquals(null , result);

    }
}
