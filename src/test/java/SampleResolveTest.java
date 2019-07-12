import org.junit.jupiter.api.Test;
import uk.ac.ncl.cs.tongzhou.navigator.Resolver;

import java.io.IOException;

public class SampleResolveTest {
    @Test
    public void resolveAny() throws IOException {
        Resolver resolver = new Resolver();

        String groupId = "net.java.openjdk";
        String artifactId = "java-base";
        String version = "11.0.1";
        String compilationUnit = "com.sun.crypto.provider.SealedObjectForKeyProtector";
        String from = "SealedObjectForKeyProtector.DeserializationChecker";

        String to = "Status";
        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to);
        System.out.println(result);
    }
}
