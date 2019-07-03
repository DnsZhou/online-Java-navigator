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
        System.out.println(result);
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

        String to = "AlternativeElement";
        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, to, classpath);
        System.out.println(result);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "AlternativeElement.html", result);
    }

    /*Rule 1: Current Type itself within the input CU*/
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

    /*Rule2: Nested Type within the input context*/
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

    /*Rule3: The specific imported Type*/
    @Test
    public void testSpecificImportedType() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.AlternativeBlock";
        String to = "Vector";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, to, classpath);
        System.out.println(result);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "collections" + SLASH + "impl" + SLASH + "Vector.html", result);

    }


    /*Rule4: Types from same package*/
    @Test
    public void testResolveSamePackageWithoutImport() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.AlternativeBlock";
        String to = "AlternativeElement";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, to, classpath);
        System.out.println(result);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "AlternativeElement.html", result);

    }

    /*Rule 4.1 Types defined in same CU*/
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

    /*Rule 5: on demand import, aka wildcard * import*/
    @Test
    public void testWildcardImport() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.ActionElement";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String toSelfType = "ActionLexer";
        String result1 = resolver.resolve(groupId, artifactId, version, compilationUnit, toSelfType, classpath);
        System.out.println(result1);

        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "actions" + SLASH + "cpp" + SLASH + "ActionLexer.html", result1);


    }
}
