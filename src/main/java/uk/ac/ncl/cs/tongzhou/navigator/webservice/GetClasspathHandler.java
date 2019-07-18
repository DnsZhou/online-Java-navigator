package uk.ac.ncl.cs.tongzhou.navigator.webservice;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.Headers;

import java.io.File;
import java.nio.file.Files;

public class GetClasspathHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        Cookie classpathCookie = httpServerExchange.getRequestCookies().get("classpath-hash");
        if (classpathCookie != null && !classpathCookie.getValue().isEmpty()) {
            String classpathHash = classpathCookie.getValue();
            File targetClasspathFile = new File(ClasspathSetterHandler.outputCustomizeClasspathRootDir, classpathHash + ".json");
            String classpathJson = Files.readString(targetClasspathFile.toPath());
            httpServerExchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
            httpServerExchange.getResponseSender().send(classpathJson);
        } else {
            httpServerExchange.setStatusCode(404);
        }
    }
}
