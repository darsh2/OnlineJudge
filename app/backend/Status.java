package backend;

/**
 * Created by Darshan on 12/7/2014.
 * Indicates the submission status of a user's solution.
 */

public enum Status {
    /**
     * Solution was accepted, it successfully passed all the test cases.
     */
    AC,

    /**
     * Solution gave a wrong answer on one of the test cases.
     */
    WA,

    /**
     * Solution timed out. Does not imply that solution is right or wrong.
     */
    TLE,

    /**
     * Solution ran into an runtime error during execution.
     */
    RE,

    /**
     * Solution produced an error on compilation.
     */
    CE,

    /**
     * Error on server side, retry your submitting your submission.
     */
    UE
}
