package backend;

import java.io.File;

/**
 * Created by Darshan on 12/8/2014.
 */
public class FileUtils {
    private static String fileSeparator = File.separator;

    /**
     * Creates the string to be appended to complete file name.
     * @param fileName File name
     * @param submissionID Submission Id
     * @param extension Extension of the file
     * @return concatenated string
     */
    public static String appendToFileName(String fileName, String submissionID, String extension) {
        return fileSeparator + fileName + submissionID + extension;
    }

    /**
     * Encloses file path within double quotes.
     * @param files List of file names
     * @return file path within double quotes
     */
    public static String getFilePath(String... files) {
        String temp = "\"";
        for (String file : files)
            temp += file;
        temp += "\"";
        return temp;
    }

    /**
     * Deletes the specified list of files.
     * @param files List of files to be deleted
     */
    public static void deleteFile(File... files) {
        for (File file : files) {
            if (file != null && file.exists())
                file.delete();
        }
    }
}
