package uk.ac.ncl.cs.tongzhou.navigator.webservice;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RedirectHandler;
import uk.ac.ncl.cs.tongzhou.navigator.Resolver;

import java.util.Deque;
import java.util.Map;

public class ResolverHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        Resolver resolver = new Resolver();
        Map<String, Deque<String>> params = httpServerExchange.getQueryParameters();
//        Todo: finish fetching the parameter and call resover
        String group = params.get("g").getFirst();
        String artifact = params.get("a").getFirst();
        String version = params.get("v").getFirst();
        String cu = params.get("cu").getFirst();
        String from = params.get("from").getFirst();
        String to = params.get("to").getFirst();
        String result = resolver.resolve(group, artifact, version, cu, from, to);
        System.out.println(result);
        new RedirectHandler(ResolverService.HOST_NAME + ":" + ResolverService.PORT + "/repository/" + result).handleRequest(httpServerExchange);
    }
}
