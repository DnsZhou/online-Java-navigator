import org.junit.jupiter.api.Test;
import uk.ac.ncl.cs.tongzhou.navigator.Resolver;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ResolverTest {

    @Test
    public void testResolver() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.AlternativeBlock";
        String to = "Vector";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, to, classpath);
        System.out.println(result);
//        assertEquals(null , result);

    }

    @Test
    public void testResolveWithNoImport() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.ActionTransInfo";
        String to = "Vector";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, to, classpath);
        System.out.println(result);
//        assertEquals(null , result);

    }
    @Test
    public void testResolveAny() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "ch.qos.cal10n";
        String artifactId = "cal10n-api";
        String version = "0.8.1.redhat-1";
        String compilationUnit = "ch.qos.cal10n.MessageConveyor";
        String to = "AnnotationExtractorViaEnumClass";
        List<String> classpath = Collections.singletonList("ch.qos.cal10n:cal10n-api:0.8.1.redhat-1");

        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, to, classpath);
        System.out.println(result);
//        assertEquals(null , result);

    }
}
