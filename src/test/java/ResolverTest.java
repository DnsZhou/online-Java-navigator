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
        String from = "AlternativeBlock";
        String to = "Vector";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, classpath);
        System.out.println(result);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "collections" + SLASH + "impl" + SLASH + "Vector.html", result);

    }


    @Test
    public void testResolveAny() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "ch.qos.cal10n";
        String artifactId = "cal10n-api";
        String version = "0.8.1.redhat-1";
        String compilationUnit = "ch.qos.cal10n.verifier.Cal10nError";
        String from = "Cal10nError";

        String to = "ErrorType";
        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to);
        System.out.println(result);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "ch" + SLASH + "qos" + SLASH + "cal10n" + SLASH + "cal10n-api" + SLASH + "0.8.1.redhat-1" + SLASH + "cal10n-api-0.8.1.redhat-1" + SLASH + "ch" + SLASH + "qos" + SLASH + "cal10n" + SLASH + "verifier" + SLASH + "Cal10nError.html", result);
    }

    /*Rule 1: Current Type itself within the input CU*/
    @Test
    public void testSelfNavigation() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.ActionElement";
        String from = "ActionElement";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String toSelfType = "ActionElement";
        String result1 = resolver.resolve(groupId, artifactId, version, compilationUnit, from, toSelfType, classpath);
        System.out.println(result1);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "ActionElement.html", result1);

        String toSelfTypeWithPackage = "antlr.ActionElement";
        String result2 = resolver.resolve(groupId, artifactId, version, compilationUnit, from, toSelfTypeWithPackage, classpath);
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
        String from = "ActionElement";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String toInternalType1 = "TestInternalClass";
        String result1 = resolver.resolve(groupId, artifactId, version, compilationUnit, from, toInternalType1, classpath);
        System.out.println(result1);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "ActionElement.html", result1);

        String toInternalType2 = "ActionElement.TestInternalClass";
        String result2 = resolver.resolve(groupId, artifactId, version, compilationUnit, from, toInternalType2, classpath);
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
        String from = "AlternativeBlock";
        String to = "Vector";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, classpath);
        System.out.println(result);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "collections" + SLASH + "impl" + SLASH + "Vector.html", result);

    }

    /*Rule3: The specific imported Type with nested type situation*/
    @Test
    public void testSpecificImportedTypeWithNested() throws IOException {
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

    /*Rule4: Types from same package*/
    @Test
    public void testResolveSamePackageWithoutImport() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.AlternativeBlock";
        String from = "AlternativeBlock";
        String to = "AlternativeElement";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, classpath);
        System.out.println(result);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "AlternativeElement.html", result);

    }

    @Test
    public void testResolveSamePackageWithoutImport2() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "ch.qos.cal10n";
        String artifactId = "cal10n-api";
        String version = "0.8.1.redhat-1";
        String compilationUnit = "ch.qos.cal10n.verifier.AbstractMessageKeyVerifier";
        String from = "ch.qos.cal10n.verifier.AbstractMessageKeyVerifier";
        String to = "Cal10nError";
        List<String> classpath = Collections.singletonList("ch.qos.cal10n:cal10n-api:0.8.1.redhat-1");

        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, to, from, classpath);
        System.out.println(result);
//        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "ch" + SLASH + "qos" + SLASH + "cal10n" + SLASH + "cal10n-api" + SLASH + "0.8.1.redhat-1" + SLASH + "cal10n-api-0.8.1.redhat-1" + SLASH + "ch" + SLASH + "qos" + SLASH + "cal10n" + SLASH + "verifier" + SLASH + "Cal10nError.html", result);

    }

    /*Rule 4.1 Types defined in same CU*/
    @Test
    public void testSameFileClass() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.ActionElement";
        String from = "ActionElement";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String to = "TestClassWithinSameFile";
        String result1 = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, classpath);
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
        String from = "ActionElement";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String to = "ActionLexer";
        String result1 = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, classpath);
        System.out.println(result1);

        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "actions" + SLASH + "cpp" + SLASH + "ActionLexer.html", result1);


    }

    /*Rule 6:  Default imported Type like java.lang */
    @Test
    public void testDefaultImportType() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.ActionElement";
        String from = "ActionElement";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String to = "String";
        String result1 = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, classpath);
        System.out.println(result1);

        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "net" + SLASH + "java" + SLASH + "openjdk" + SLASH + "java-base" + SLASH + "11.0.1" + SLASH + "java-base-11.0.1" + SLASH + "java" + SLASH + "lang" + SLASH + "String.html", result1);


    }

    /*Rule 7:  Directly referred Type eg: com.google.testClass test = new com.google.testClass() */
    @Test
    public void testDirectlyReferredType() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "antlr";
        String artifactId = "antlr";
        String version = "2.7.7.redhat-7";
        String compilationUnit = "antlr.ActionElement";
        String from = "ActionElement";
        List<String> classpath = Collections.singletonList("antlr:antlr:2.7.7.redhat-7");

        String to = "antlr.actions.cpp.ActionLexer";
        String result1 = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, classpath);
        System.out.println(result1);

        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "antlr" + SLASH + "antlr" + SLASH + "2.7.7.redhat-7" + SLASH + "antlr-2.7.7.redhat-7" + SLASH + "antlr" + SLASH + "actions" + SLASH + "cpp" + SLASH + "ActionLexer.html", result1);


    }
}
