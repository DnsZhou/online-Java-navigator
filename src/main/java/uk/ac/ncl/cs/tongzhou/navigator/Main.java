package uk.ac.ncl.cs.tongzhou.navigator;

import uk.ac.ncl.cs.tongzhou.navigator.webservice.WebServiceRouter;

import java.util.Date;

/**
 * s
 *
 * @author Tong Zhou b8027512@ncl.ac.uk
 * @created 21:12 25-03-2019
 */

public class Main<T> {

    public static void main(String[] args) throws Exception {
//        var startTime = new Date();
//        System.out.println("Process Started at " + startTime);
//        RepositoryWalker.processRepository();
//        var endTime = new Date();
//        System.out.println("Process Finished at " + endTime);
//        System.out.println("Took " + ((double) (endTime.getTime() - startTime.getTime()) / 1000) + " seconds.");

        WebServiceRouter.runServer();
    }
}
