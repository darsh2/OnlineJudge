package backend.languages;

import backend.Status;

/**
 * Created by Darshan on 12/3/2014.
 */
public interface Language {
    /**
     * Creates and executes a batch file to run the language specific compilation script. Compile errors are stored in CompileErrors.txt file
     * @return returns null if successful compilation else returns appropriate Status
     */
    public Status compile();

    /**
     * Creates and executes a batch file to run the language specific execution script. Output is stored in Output.txt file.
     * @return returns true if generated output file matched the testcases output, false otherwise
     */
    public Status execute();
}
