package uk.ac.ncl.cs.tongzhou.navigator.webservice;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;
import uk.ac.ncl.cs.tongzhou.navigator.RepositoryWalker;

import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;

public class S3OutputResHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        httpServerExchange.getResponseHeaders()
                .put(new HttpString("Access-Control-Allow-Origin"), "*");
        httpServerExchange.setStatusCode(StatusCodes.FOUND);
        httpServerExchange.getResponseHeaders().put(Headers.LOCATION, WebServiceRouter.S3_URL
                + RepositoryWalker.outputHtmlRootDir.getPath().replace(SLASH, "/") + httpServerExchange.getRelativePath());
        httpServerExchange.endExchange();
    }
}
