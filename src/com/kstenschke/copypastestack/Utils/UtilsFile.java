package com.kstenschke.copypastestack.Utils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class UtilsFile {

    /**
     * @param   path
     * @return  String
     */
    public static String getNewestFileInPath(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();

        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f2.lastModified()).compareTo(
                        f1.lastModified());
            }
        });

        return files != null && files.length > 0 ? files[0].getAbsolutePath() : null;
    }

}