package uk.ac.ncl.cs.tongzhou.navigator.webservice;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import uk.ac.ncl.cs.tongzhou.navigator.RepositoryWalker;

import java.io.File;

public class ResolverService {
    public static String HOST_NAME = "localhost";
    public static int PORT = 8080;

    public static void runServer() {
        File htmlDir = RepositoryWalker.outputHtmlRootDir;
        ResourceHandler resourceHandler = new ResourceHandler().setResourceManager(new FileResourceManager(htmlDir));

        PathHandler pathHandler = new PathHandler();
        pathHandler.addPrefixPath("/repository", resourceHandler);

        HttpHandler resolver = new ResolverHandler();
        pathHandler.addExactPath("/resolver", resolver);

        Undertow server = Undertow.builder()
                .addHttpListener(PORT, HOST_NAME).setHandler(pathHandler).build();


        server.start();
    }

}
