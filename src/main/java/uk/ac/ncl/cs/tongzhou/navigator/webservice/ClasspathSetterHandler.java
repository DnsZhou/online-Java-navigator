package uk.ac.ncl.cs.tongzhou.navigator.webservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;

public class ClasspathSetterHandler implements HttpHandler {
    // TODO change me to an empty dir where the error output will be written
    public static File outputCustomizeClasspathRootDir = new File("tmp" + SLASH + "output" + SLASH + "CustomizeClasspath");
    static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        httpServerExchange.getResponseHeaders()
                .put(new HttpString("Access-Control-Allow-Origin"), "*");
        String jsonString = getString(httpServerExchange.getInputStream());
        byte[] jsonBytes = jsonString.getBytes("UTF-8");
        int jsonHash = Math.abs(jsonString.hashCode());
        File customizeClasspath = new File(outputCustomizeClasspathRootDir.getPath(), jsonHash + ".json");
        customizeClasspath.getParentFile().mkdirs();
        Files.write(customizeClasspath.toPath(), jsonBytes);
        httpServerExchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
        httpServerExchange.getResponseSender().send("{\"classpathHash\": \"" + jsonHash + "\"}");
    }

    private String getString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        return br.lines().collect(Collectors.joining(System.lineSeparator()));
    }
}
