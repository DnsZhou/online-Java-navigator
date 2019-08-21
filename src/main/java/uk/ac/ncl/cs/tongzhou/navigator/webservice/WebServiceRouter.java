package uk.ac.ncl.cs.tongzhou.navigator.webservice;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import uk.ac.ncl.cs.tongzhou.navigator.RepositoryWalker;
import uk.ac.ncl.cs.tongzhou.navigator.Resolver;

import java.io.File;

import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;

public class WebServiceRouter {
    public static String HOST_NAME = "localhost";
    //    public static String HOST_NAME = "ec2-35-178-134-147.eu-west-2.compute.amazonaws.com";
    public static String S3_URL = "http://online-java-navigator.s3.eu-west-2.amazonaws.com/";
    public static int PORT = 8080;

    public static void runServer() {
        File htmlDir = RepositoryWalker.outputHtmlRootDir;
        File frontendDir = new File("frontend" + SLASH);

        /*For local storage use*/
        ResourceHandler resourceHandler = new ResourceHandler().setResourceManager(new FileResourceManager(htmlDir));
        ResourceHandler frontendHandler = new ResourceHandler().setResourceManager(new FileResourceManager(frontendDir));

        /*For S3 use*/
        HttpHandler s3OutputResHandler = new S3OutputResHandler();
        HttpHandler s3FrontendResHandler = new S3FrontendResHandler();

        PathHandler pathHandler = new PathHandler();
        if (Resolver.RESOLVE_SOLUTION.equals("S3") || Resolver.RESOLVE_SOLUTION.equals("HYBRID")) {
            pathHandler.addPrefixPath("/repository", s3OutputResHandler);
        } else {
            pathHandler.addPrefixPath("/repository", resourceHandler);
        }
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
