import org.junit.jupiter.api.Test;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.IndexType;

import java.io.IOException;

public class IndexTypeTest {

    @Test
    public void testIndexType() throws IOException {
        IndexType.addIndexItem("TestTypeNmae1", "antlr:antlr:2.7.7.redhat-7:antlr.actions.cpp.ActionLexer");
        IndexType.addIndexItem("TestTypeNmae1", "antlr:antlr:2.7.8.redhat-7:antlr.actions.cpp.ActionLexer");
        IndexType.addIndexItem("TestTypeNmae1", "antlr:antlr:2.7.9.redhat-7:antlr.actions.cpp.ActionLexer");
        IndexType.addIndexItem("TestTypeNmae1", "antlr:antlr:2.7.10.redhat-7:antlr.actions.cpp.ActionLexer");
        IndexType.addIndexItem("TestTypeNmae2", "antlr:antlr:2.7.7.redhat-7:antlr.actions.cpp.ActionLexer");
        IndexType.addIndexItem("TestTypeNmae2", "antlr:antlr:2.7.8.redhat-7:antlr.actions.cpp.ActionLexer");
        IndexType.addIndexItem("TestTypeNmae2", "antlr:antlr:2.7.9.redhat-7:antlr.actions.cpp.ActionLexer");
        IndexType.addIndexItem("TestTypeNmae2", "antlr:antlr:2.7.10.redhat-7:antlr.actions.cpp.ActionLexer");
        IndexType.generateIndex();
    }
}
