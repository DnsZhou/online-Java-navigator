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

        String groupId = "net.java.openjdk";
        String artifactId = "java-base";
        String version = "11.0.1";
        String compilationUnit = "com.sun.crypto.provider.OAEPParameters";
        String from = "OAEPParameters";
        String to = "PSource.PSpecified";
        List<String> classpath = Collections.singletonList("net.java.openjdk:java-base:11.0.1");

        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, classpath);
        System.out.println(result);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "net" + SLASH + "java" + SLASH + "openjdk" + SLASH + "java-base" + SLASH + "11.0.1" + SLASH + "java-base-11.0.1" + SLASH + "javax" + SLASH + "crypto" + SLASH + "spec" + SLASH + "PSource" + SLASH + "PSpecified.html", result);

    }
}
