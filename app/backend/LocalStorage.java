package backend;

import models.Submission;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import play.mvc.Http.MultipartFormData.*;
import models.Problem;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Darshan on 12/1/2014.
 */
public class LocalStorage {
    private static final String directory = System.getProperty("user.dir") + File.separator + "public" + File.separator;
    public static final String problemDirectory = directory + "problems" + File.separator;
    public static final String submissionDirectory = directory + "users" + File.separator;

    /**
     * Saves the added problem to problems directory in public folder. The respective file names are:
     * ip.txt: Input file
     * op.txt: Output file
     * problem.html: Problem statement
     * Problem name is added to problems collection
     * @param filePartP FilePart containing the uploaded problem statement
     * @param filePartI FilePart containing the uploaded test cases file
     * @param filePartO FilePart containing the uploaded output file
     * @return true if problem was successfully added, false otherwise
     */
    public static boolean saveProblem(FilePart filePartP, FilePart filePartI, FilePart filePartO) {
        long fileCount = Problem.getProblemCount() + 1;
        String directoryName = Long.toString(fileCount);
        System.out.println(directoryName);

        /*
        create directory if it does not exist
        */
        String absolutePath = problemDirectory + directoryName;
        File problemDirectory = new File(absolutePath);
        if (!problemDirectory.mkdirs())
            System.out.println("Error in creating directory");
        System.out.println(absolutePath + " created directory");

        /*
        setting up the files for problem statement, ip and op
        */
        File problem = new File(absolutePath + File.separator + "problem.html"),
                ip = new File(absolutePath + File.separator + "ip.txt"),
                op = new File(absolutePath + File.separator + "op.txt");

        File fileP = filePartP.getFile(), fileI = filePartI.getFile(), fileO = filePartO.getFile();
        System.out.println(fileP.getName());
        try {
            /**
             * creating local copy of the provided files
             * by using their inputstreams
             */
            InputStream isFileP = new FileInputStream(fileP), isFileI = new FileInputStream(fileI), isFileO = new FileInputStream(fileO);
            byte[] byteFileP = IOUtils.toByteArray(isFileP), byteFileI = IOUtils.toByteArray(isFileI), byteFileO = IOUtils.toByteArray(isFileO);

            FileUtils.writeByteArrayToFile(problem, byteFileP);
            FileUtils.writeByteArrayToFile(ip, byteFileI);
            FileUtils.writeByteArrayToFile(op, byteFileO);

            isFileP.close();
            isFileI.close();
            isFileO.close();

            Problem.addProblemToCollection(fileCount, "D");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves user submission in user's directory for the respective problem.
     * @param handle user's handle
     * @param problemID problem's problem ID
     * @param submission user's uploaded submission file
     * @return a String array containing:
     * file name of uploaded solution,
     * absolute path to the locally stored submission,
     * parent file of the locally stored submission
     */
    public static String[] saveSubmission(String handle, long problemID, File submission) {
        String absolutePath = submissionDirectory + handle + File.separator + problemID;
        System.out.println(absolutePath);

        File submissionDirectory = new File(absolutePath);
        if (!submissionDirectory.mkdirs())
            System.out.println("Error creating user directory");
        System.out.println("Successfully created user directory for current problem");

        File userSubmission = new File(absolutePath + File.separator + Long.toString(Submission.numberOfSubmissions() + 1) + ".java");
        try {
            InputStream inputStream = new FileInputStream(submission);
            byte[] bytes = IOUtils.toByteArray(inputStream);

            FileUtils.writeByteArrayToFile(userSubmission, bytes);

            inputStream.close();

            System.out.println("Saved user submission");

            String[] fileAttributes = new String[3];
            fileAttributes[0] = userSubmission.getName();
            fileAttributes[1] = userSubmission.getAbsolutePath();
            fileAttributes[2] = userSubmission.getParent();

            return fileAttributes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
