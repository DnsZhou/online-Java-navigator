package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

import uk.ac.ncl.cs.tongzhou.navigator.RepositoryWalker;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexType {
    static Map<String, IndexType> allIndexTypes = new HashMap<String, IndexType>();
    private String name;
    private List<String> gavCus;
    static File outputIndexRootDir = new File(RepositoryWalker.INDEX_PATH);

    private IndexType(String name) {
        this.name = name;
        this.gavCus = new ArrayList<>();
    }

    public static boolean addIndexItem(String typeName, String gavCu) {
        try {
            IndexType targetType = allIndexTypes.get(typeName);
            if (targetType == null) {
                targetType = new IndexType(typeName);
            }
            targetType.gavCus.add(gavCu);
            allIndexTypes.put(typeName, targetType);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean generateIndex() throws IOException {

        allIndexTypes.forEach((typeName, indexValue) -> {
            try {
                File outputFile = new File(outputIndexRootDir, typeName);
                if (outputFile.exists()) {
                    System.out.println("exists");
//                    outputFile = new File(outputFile, outputFile.getName());
//                    outputFile.getParentFile().mkdirs();
//                    Files.write(outputFile.toPath(), indexValue.convertToString().getBytes("UTF-8"));
//                } else {
                }
                Files.write(outputFile.toPath(), indexValue.convertToString().getBytes("UTF-8"));
//                }
                /*no need to worry about those duplicate File name, because we use hashMap to store Type names, its
                Impossible to find two same Type name.*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return true;
    }

    private String convertToString() {
        String result = "";
        for (String item : this.gavCus) {
            result = result.concat(item).concat("\n");
        }
        return result;
    }
}
