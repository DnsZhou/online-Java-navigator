package uk.ac.ncl.cs.tongzhou.navigator.webservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;
import uk.ac.ncl.cs.tongzhou.navigator.RepositoryWalker;
import uk.ac.ncl.cs.tongzhou.navigator.Resolver;
import uk.ac.ncl.cs.tongzhou.navigator.jsonmodel.ClasspathDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;

public class ResolverHandler implements HttpHandler {
    static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        httpServerExchange.getResponseHeaders()
                .put(new HttpString("Access-Control-Allow-Origin"), "*");
        List<String> classpathList = null;
        Resolver resolver = new Resolver();
        Map<String, Deque<String>> params = httpServerExchange.getQueryParameters();
        String group = params.get("g").getFirst();
        String artifact = params.get("a").getFirst();
        String version = params.get("v").getFirst();
        String cu = params.get("cu").getFirst();
        String from = params.get("from").getFirst();
        String to = params.get("to").getFirst();
        Cookie classpathCookie = httpServerExchange.getRequestCookies().get("classpath-hash");
        if (classpathCookie != null && !classpathCookie.getValue().isEmpty()) {
            classpathList = getClasspathDtoByHash(classpathCookie.getValue());
        }
        String result = resolver.resolve(group, artifact, version, cu, from, to, classpathList);
        System.out.println(result);
        httpServerExchange.setStatusCode(StatusCodes.FOUND);
        if (Resolver.RESOLVE_WITH_S3) {
            httpServerExchange.getResponseHeaders().put(Headers.LOCATION, WebServiceRouter.S3_URL
                    + RepositoryWalker.outputHtmlRootDir.getPath().replace(SLASH, "/") + "/" + result);
        } else {
            httpServerExchange.getResponseHeaders().put(Headers.LOCATION, "/repository/" + result);
        }
        httpServerExchange.endExchange();
    }

    private static List<String> getClasspathDtoByHash(String classpathHash) {
        try {
            File targetClasspathFile = new File(ClasspathSetterHandler.outputCustomizeClasspathRootDir, classpathHash + ".json");
            String classpathJson = Files.readString(targetClasspathFile.toPath());
            ClasspathDto classpathDto = objectMapper.readValue(classpathJson, ClasspathDto.class);
            return classpathDto.classpathList;

        } catch (IOException e) {
            System.out.println("Warning: Unable to fetch customize classpath file by hash: " + classpathHash);
            e.printStackTrace();
            return null;
        }
    }
}
