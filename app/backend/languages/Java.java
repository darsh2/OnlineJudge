package backend.languages;

import backend.FileUtils;
import backend.LocalStorage;
import backend.Status;
import backend.Timer;
import org.apache.commons.io.FileDeleteStrategy;
import play.Logger;

import java.io.*;

/**
 * Created by Darshan on 12/3/2014.
 */
public class Java implements Language {
    private String fileName, filePath, parentFile,
            compileErrorFile, compileBatchFile, compileErrorFileName, compileBatchFileName,
            executeBatchFile, executeErrorFile, executeOutputFile, executeBatchFileName, executeErrorFileName, executeOutputFileName,
            executeTestCaseInputFile, executeTestCaseOutputFile, executeTestCaseInputFileName, executeTestCaseOutputFileName;

    /**
     * Creates a new Java submission instance with the path to the java submission and the input file path
     * @param fileName file name of the java program
     * @param filePath complete path of the java program
     * @param parentFile path of the parent file
     * @param inputFilePath path to the input file
     */
    public Java(String fileName, String filePath, String parentFile, String inputFilePath, String submissionID) {
        this.fileName = fileName;
        this.filePath = FileUtils.getFilePath(filePath);
        this.parentFile = FileUtils.getFilePath(parentFile);

        compileBatchFile = FileUtils.getFilePath(parentFile, FileUtils.appendToFileName("Compile", submissionID, ".bat"));
        compileBatchFileName = parentFile + FileUtils.appendToFileName("Compile", submissionID, ".bat");
        compileErrorFile = FileUtils.getFilePath(parentFile, FileUtils.appendToFileName("CompileError", submissionID, ".txt"));
        compileErrorFileName = parentFile + FileUtils.appendToFileName("CompileError", submissionID, ".txt");

        executeBatchFile = FileUtils.getFilePath(parentFile, FileUtils.appendToFileName("Execute", submissionID, ".bat"));
        executeBatchFileName = parentFile + FileUtils.appendToFileName("Execute", submissionID, ".bat");
        executeErrorFile = FileUtils.getFilePath(parentFile, FileUtils.appendToFileName("ExecuteError", submissionID, ".txt"));
        executeErrorFileName = parentFile + FileUtils.appendToFileName("ExecuteError", submissionID, ".txt");
        executeOutputFile = FileUtils.getFilePath(parentFile, FileUtils.appendToFileName("ExecuteOutput", submissionID, ".txt"));
        executeOutputFileName = parentFile + FileUtils.appendToFileName("ExecuteOutput", submissionID, ".txt");

        executeTestCaseInputFile = FileUtils.getFilePath(inputFilePath, FileUtils.appendToFileName("ip", "", ".txt"));
        executeTestCaseInputFileName = inputFilePath + FileUtils.appendToFileName("ip", "", ".txt");
        executeTestCaseOutputFile = FileUtils.getFilePath(inputFilePath, FileUtils.appendToFileName("op", "", ".txt"));
        executeTestCaseOutputFileName = inputFilePath + FileUtils.appendToFileName("op", "", ".txt");

        System.out.println("User submission file name: " + fileName);
        System.out.println("User submission file path: " + filePath);
        System.out.println("User submission file parent file: " + parentFile);

        System.out.println("Compilation batch file: " + compileBatchFile);
        System.out.println("Compilation error file: " + compileErrorFile);

        System.out.println("Execution batch file: " + executeBatchFile);
        System.out.println("Execution error file: " + executeErrorFile);
        System.out.println("Execution output file: " + executeOutputFile);

        System.out.println("Input Test File: " + executeTestCaseInputFile);
        System.out.println("Output Test File: " + executeTestCaseOutputFile);
    }

    @Override
    public Status compile() {
        File compilationErrorFile = null;

        try {
            /**
             * Each compilation is run in a new thread of execution.
             * Compilation batch file is created based on respective file names and then run.
             * Error file is checked for compile time errors.
             */
            Thread thread = new Thread() {
                Process process;
                File compilationBatchFile;

                @Override
                public void run() {
                    try {

                        System.out.println("--------------------------------------------------------------------");
                        System.out.println("Compilation script");
                        System.out.println("javac " + filePath + " 2> " + compileErrorFile);
                        System.out.println("--------------------------------------------------------------------");

                        /*
                        Creating compilation batch file
                         */
                        compilationBatchFile = new File(compileBatchFileName);
                        if (!compilationBatchFile.exists()) {
                            System.out.println("Creating compilation file");
                            compilationBatchFile.createNewFile();
                        }
                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(compilationBatchFile)));
                        out.println("javac " + filePath + " 2> " + compileErrorFile);
                        out.close();

                        /*
                        Running compilation batch file as a process in cmd
                         */
                        process = Runtime.getRuntime().exec("cmd /c " + compileBatchFile);
                        process.waitFor();

                        /*
                        Deleting compilation batch file
                         */
                        FileUtils.deleteFile(compilationBatchFile);

                    } catch (Exception e) {
                        System.out.println("Interrupted compilation Thread");
                        e.printStackTrace();

                        /*
                        Forcibly destroying cmd process
                         */
                        process.destroyForcibly();

                        FileUtils.deleteFile(compilationBatchFile);
                    }
                }
            };

            thread.start();
            thread.join();

            /*
            Status of compilation
             */
            Status status = null;

            /*
            Checking status of compilation
             */
            compilationErrorFile = new File(compileErrorFileName);
            BufferedReader br = new BufferedReader(new FileReader(compilationErrorFile));
            if (br.readLine() != null)
                status = Status.CE;
            br.close();
            FileUtils.deleteFile(compilationErrorFile);

            return status;

        } catch (Exception e) {
            e.printStackTrace();
            FileUtils.deleteFile(compilationErrorFile);
            return Status.UE;
        }
    }

    @Override
    public Status execute() {
        File executionOutputFile = null, executionErrorFile = null;
        try {
            Thread thread = new Thread() {
                Process process;
                File executionBatchFile;

                @Override
                public void run() {
                    try {
                        System.out.println("--------------------------------------------------------------------");
                        System.out.println("Execution script");
                        System.out.println("java -cp " + parentFile + " " + fileName.substring(0, fileName.indexOf('.')) + " < " + executeTestCaseInputFile + " > " + executeOutputFile + " 2> " + executeErrorFile);
                        System.out.println("--------------------------------------------------------------------");

                        /*
                        Creating execution batch file
                         */
                        executionBatchFile = new File(executeBatchFileName);
                        if (!executionBatchFile.exists()) {
                            System.out.println("creating execution file");
                            executionBatchFile.createNewFile();
                        }
                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(executionBatchFile)));
                        out.println("java -cp " + parentFile + " Main < " + executeTestCaseInputFile + " > " + executeOutputFile + " 2> " + executeErrorFile);
                        out.close();

                        /*
                        Running execution batch file as a process in cmd
                         */
                        process = Runtime.getRuntime().exec("cmd /c " + executeBatchFile);
                        process.waitFor();

                        /*
                        Deletes execution batch file
                         */
                        FileUtils.deleteFile(executionBatchFile);

                    } catch (Exception e) {
                        e.printStackTrace();

                        /*
                        Forcibly destroying cmd process
                         */
                        process.destroyForcibly();

                        FileUtils.deleteFile(executionBatchFile);
                    }
                }
            };
            thread.start();

            Timer timer = new Timer();
            timer.start();
            timer.join();

            /*
            Status of user submission
             */
            Status status = null;

            executionErrorFile = new File(executeErrorFileName);
            executionOutputFile = new File(executeOutputFileName);

            if (thread.getState() != Thread.State.TERMINATED) {
                thread.interrupt();
                status = Status.TLE;
            }

            if (status == null) {
                BufferedReader br = new BufferedReader(new FileReader(executionErrorFile));
                if (br.readLine() != null)
                    status = Status.RE;
                br.close();

                if (status == null) {
                    br = new BufferedReader(new FileReader(executionOutputFile));
                    BufferedReader brO = new BufferedReader(new FileReader(executeTestCaseOutputFileName));

                    String lineU = br.readLine(), lineO = brO.readLine();
                    while (lineU != null && lineO != null) {
                        lineU = lineU.trim();
                        lineO = lineO.trim();

                        /*
                        Outputs don't match
                         */
                        if (lineU.compareTo(lineO) != 0) {
                            br.close();
                            brO.close();
                            FileUtils.deleteFile(executionOutputFile);

                            return Status.WA;
                        }

                        lineU = br.readLine();
                        lineO = brO.readLine();
                    }

                    br.close();
                    brO.close();

                    if (lineU == null && lineO == null)
                        status = Status.AC;
                    else
                        status = Status.WA;
                }
            }

            FileUtils.deleteFile(executionErrorFile, executionOutputFile);
            return status;

        } catch (Exception e) {
            e.printStackTrace();
            FileUtils.deleteFile(executionErrorFile, executionOutputFile);
            return Status.UE;
        }
    }
}
