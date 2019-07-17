package uk.ac.ncl.cs.tongzhou.navigator.webservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import uk.ac.ncl.cs.tongzhou.navigator.Resolver;

import java.io.ByteArrayOutputStream;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class TypeNameHandler implements HttpHandler {
    static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        Resolver resolver = new Resolver();
        Map<String, Deque<String>> params = httpServerExchange.getQueryParameters();
        String typeName = params.get("typeName").getFirst();
        List<String> gavCuList = resolver.findGavCusByTypeName(typeName);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        objectMapper.writeValue(out, gavCuList);

        final byte[] data = out.toByteArray();
        String res = new String(data);
        httpServerExchange.getResponseHeaders()
                .put(new HttpString("Access-Control-Allow-Origin"), "*");
        httpServerExchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
        httpServerExchange.getResponseSender().send("{\"typeGavCuList\": " + res + "}");
    }
}
