import org.junit.jupiter.api.Test;
import uk.ac.ncl.cs.tongzhou.navigator.Resolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;

public class SampleResolveTest {
    @Test
    public void resolveAny() throws IOException {
        Resolver resolver = new Resolver();

        String groupId = "xerces";
        String artifactId = "xercesImpl";
        String version = "2.12.0.SP02-redhat-00001";
        String compilationUnit = "org.apache.xerces.util.SymbolHash";
        String from = "SymbolHash";
        List<String> classpath = new ArrayList<String>();
        classpath.add("xerces:xercesImpl:2.12.0.SP02-redhat-00001");
        String to = "Entry";

        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, null);
        System.out.println(result);
        assertEquals("xerces/xercesImpl/2.12.0.SP02-redhat-00001/xercesImpl-2.12.0.SP02-redhat-00001/org/apache/xerces/util/SymbolHash.html#org.apache.xerces.util.SymbolHash.apache.xerces.util.SymbolHash.Entry", result);

    }

    @Test
    public void resolveAny2() throws IOException {
        Resolver resolver = new Resolver();

        String groupId = "xerces";
        String artifactId = "xercesImpl";
        String version = "2.12.0.SP02-redhat-00001";
        String compilationUnit = "org.apache.xerces.util.SymbolHash";
        String from = "SymbolHash";
        List<String> classpath = new ArrayList<String>();
        classpath.add("xerces:xercesImpl:2.12.0.SP02-redhat-00001");
        String to = "SymbolHash.Entry";

        String result = resolver.resolve(groupId, artifactId, version, compilationUnit, from, to, null);
        System.out.println(result);
        assertEquals("xerces/xercesImpl/2.12.0.SP02-redhat-00001/xercesImpl-2.12.0.SP02-redhat-00001/org/apache/xerces/util/SymbolHash.html#org.apache.xerces.util.SymbolHash.apache.xerces.util.SymbolHash.Entry", result);

    }
}
