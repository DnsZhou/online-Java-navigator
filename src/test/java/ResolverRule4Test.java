import org.junit.jupiter.api.Test;
import uk.ac.ncl.cs.tongzhou.navigator.Resolver;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;

public class ResolverRule4Test {

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

    @Test
    public void test2() throws IOException {

        Resolver resolver = new Resolver();

        String groupId = "ch.qos.cal10n";
        String artifactId = "cal10n-api";
        String version = "0.8.1.redhat-1";
        String compilationUnit = "ch.qos.cal10n.verifier.AbstractMessageKeyVerifier";
        String to = "Cal10nError";
        List<String> classpath = Collections.singletonList("ch.qos.cal10n:cal10n-api:0.8.1.redhat-1");

        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, to, classpath);
        System.out.println(result);
        assertEquals("tmp" + SLASH + "output" + SLASH + "htmlDocs" + SLASH + "ch" + SLASH + "qos" + SLASH + "cal10n" + SLASH + "cal10n-api" + SLASH + "0.8.1.redhat-1" + SLASH + "cal10n-api-0.8.1.redhat-1" + SLASH + "ch" + SLASH + "qos" + SLASH + "cal10n" + SLASH + "verifier" + SLASH + "Cal10nError.html", result);

    }
}
