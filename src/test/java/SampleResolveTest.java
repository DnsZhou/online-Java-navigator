import org.junit.jupiter.api.Test;
import uk.ac.ncl.cs.tongzhou.navigator.Resolver;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;

public class SampleResolveTest {
    @Test
    public void resolveAny() throws IOException {
        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.ActionTransInfo";
        String from = "ActionTransInfo";
        String to = "ActionElement.TestInternalClass";
//        List<String> classpath = Collections.singletonList("net.java.openjdk:java-base:11.0.1");

//        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, classpath);
        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, null);
        System.out.println(result);
        assertEquals("antlr/antlr/2.7.7.redhat-7/antlr-2.7.7.redhat-7/antlr/ActionElement.html#antlr.ActionElement.TestInternalClass", result);

    }
}
