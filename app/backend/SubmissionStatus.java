package backend;

import backend.languages.Java;

/**
 * Created by Darshan on 12/3/2014.
 */
public class SubmissionStatus {
    private String fileName, filePath, parentFile, inputFilePath, submissionID;

    /**
     * Creates an instance of type SubmissionStatus with given parameters.
     * @param fileName File name of the submission
     * @param filePath File path of the submission
     * @param parentFile Parent file of the submission file
     * @param inputFilePath File path for the test cases
     * @param submissionID Current submission Id
     */
    public SubmissionStatus(String fileName, String filePath, String parentFile, String inputFilePath, String submissionID) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.parentFile = parentFile;
        this.inputFilePath = inputFilePath;
        this.submissionID = submissionID;
    }

    /**
     * Computes the status of the submission.
     * @return an enum Status indicating status of submission
     */
    public Status getStatus() {
        Java java = new Java(fileName, filePath, parentFile, inputFilePath, submissionID);
        Status status = java.compile();
        if (status == null)
            status = java.execute();

        return status;
    }
}
