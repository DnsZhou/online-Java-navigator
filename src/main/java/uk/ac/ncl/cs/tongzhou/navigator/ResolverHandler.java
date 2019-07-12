package uk.ac.ncl.cs.tongzhou.navigator;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.Deque;
import java.util.Map;

public class ResolverHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

        Map<String, Deque<String>> params = httpServerExchange.getQueryParameters();
//        Todo: finish fetching the parameter and call resover
        String groupId = params.get("g").getFirst();
    }
}
