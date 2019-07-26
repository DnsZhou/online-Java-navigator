package uk.ac.ncl.cs.tongzhou.navigator.awsservice;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

import static uk.ac.ncl.cs.tongzhou.navigator.Util.SLASH;

public class S3Handler {
    static String S3_BUCKET_NAME = "online-java-navigator";

    public static byte[] getBytefromS3ByPath(String path) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String objectKey = path.replace(SLASH, "/");
        try {
            S3Object s3Object = s3.getObject(S3_BUCKET_NAME, objectKey);
            S3ObjectInputStream s3ObjInputStream = s3Object.getObjectContent();
            byte[] byteArray = IOUtils.toByteArray(s3Object.getObjectContent());
            s3ObjInputStream.close();
            return byteArray;

        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static String getStringfromS3ByPath(String path) {
        var res = getBytefromS3ByPath(path);
        return res == null ? null : new String(getBytefromS3ByPath(path));
    }


}
