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

//        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, classpath);
        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, null);
        System.out.println(result);
        assertEquals("net" + "/" + "java" + "/" + "openjdk" + "/" + "java-base" + "/" + "11.0.1" + "/" + "java-base-11.0.1" + "/" + "javax" + "/" + "crypto" + "/" + "spec" + "/" + "PSource" + "/" + "PSpecified.html", result);

    }
}
