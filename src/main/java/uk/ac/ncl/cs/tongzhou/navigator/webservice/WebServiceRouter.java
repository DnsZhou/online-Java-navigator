package uk.ac.ncl.cs.tongzhou.navigator.webservice;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import uk.ac.ncl.cs.tongzhou.navigator.RepositoryWalker;

import java.io.File;

import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;

public class WebServiceRouter {
    //    public static String HOST_NAME = "ec2-35-178-134-147.eu-west-2.compute.amazonaws.com";
    public static String HOST_NAME = "localhost";
    public static int PORT = 8080;

    public static void runServer() {
        File htmlDir = RepositoryWalker.outputHtmlRootDir;
        File frontendDir = new File("frontend" + SLASH);

        ResourceHandler resourceHandler = new ResourceHandler().setResourceManager(new FileResourceManager(htmlDir));
        ResourceHandler frontendHandler = new ResourceHandler().setResourceManager(new FileResourceManager(frontendDir));

        PathHandler pathHandler = new PathHandler();
        pathHandler.addPrefixPath("/repository", resourceHandler);

        pathHandler.addPrefixPath("/", frontendHandler);

        HttpHandler resolver = new ResolverHandler();
        pathHandler.addExactPath("/resolver", resolver);

        HttpHandler classpathSetterHandler = new ClasspathSetterHandler();
        pathHandler.addExactPath("/setClasspath", new BlockingHandler(classpathSetterHandler));

        HttpHandler getClasspathHandler = new GetClasspathHandler();
        pathHandler.addExactPath("/getClasspath", new BlockingHandler(getClasspathHandler));

        HttpHandler typeNameHandler = new TypeNameHandler();
        pathHandler.addExactPath("/findType", new BlockingHandler(typeNameHandler));

        Undertow server = Undertow.builder()
                .addHttpListener(PORT, HOST_NAME).setHandler(pathHandler).build();


        server.start();
    }

}
