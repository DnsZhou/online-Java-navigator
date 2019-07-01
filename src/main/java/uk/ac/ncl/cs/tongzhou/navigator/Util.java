package uk.ac.ncl.cs.tongzhou.navigator;

import java.io.File;

public class Util {
    public static String SLASH = File.separator;

    public static String subStringLastDot(String string) {
        if (string.contains(".")) {
            return string.substring(string.lastIndexOf("."), string.length());
        }
        return string;
    }
}
