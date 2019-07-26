package uk.ac.ncl.cs.tongzhou.navigator.webservice;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;

public class S3FrontendResHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        httpServerExchange.getResponseHeaders()
                .put(new HttpString("Access-Control-Allow-Origin"), "*");
        httpServerExchange.setStatusCode(StatusCodes.FOUND);
        httpServerExchange.getResponseHeaders().put(Headers.LOCATION, WebServiceRouter.S3_URL
                + "frontend" + httpServerExchange.getRelativePath());
        httpServerExchange.endExchange();
    }
}