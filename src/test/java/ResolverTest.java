import org.junit.jupiter.api.Test;
import uk.ac.ncl.cs.tongzhou.navigator.Resolver;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;

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
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "collections" + SLASH + "impl" + SLASH + "Vector.html", result);

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
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "collections" + SLASH + "impl" + SLASH + "Vector.html", result);

    }

    @Test
    public void testResolveAny() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.ActionElement";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String to = "Alternative";
        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, to, classpath);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "Alternative.html", result);
    }

    @Test
    public void testSelfNavigation() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.ActionElement";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String toSelfType = "ActionElement";
        String result1 = resolver.resolve(groupId, artifactId, version, compilationUnit, toSelfType, classpath);
        System.out.println(result1);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "ActionElement.html", result1);

        String toSelfTypeWithPackage = "antlr.ActionElement";
        String result2 = resolver.resolve(groupId, artifactId, version, compilationUnit, toSelfType, classpath);
        System.out.println(result2);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "ActionElement.html", result1);
    }

    @Test
    public void testInternalClass() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.ActionElement";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String toInternalType1 = "TestInternalClass";
        String result1 = resolver.resolve(groupId, artifactId, version, compilationUnit, toInternalType1, classpath);
        System.out.println(result1);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "ActionElement.html", result1);

        String toInternalType2 = "ActionElement.TestInternalClass";
        String result2 = resolver.resolve(groupId, artifactId, version, compilationUnit, toInternalType2, classpath);
        System.out.println(result2);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "ActionElement.html", result2);

    }

    @Test
    public void testSameFileClass() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.ActionElement";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String toSelfType = "TestClassWithinSameFile";
        String result1 = resolver.resolve(groupId, artifactId, version, compilationUnit, toSelfType, classpath);
        System.out.println(result1);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "ActionElement.html", result1);


    }
}
