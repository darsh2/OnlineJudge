package models;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.modules.mongodb.jackson.MongoDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darshan on 11/26/2014.
 */
public class Problem {
    @Id
    private long problemID;

    private String problemName;
    private long numSubmissions;

    private static JacksonDBCollection<Problem, Long> problemCollection = MongoDB.getCollection("problems", Problem.class, Long.class);

    public long getProblemID() {
        return problemID;
    }

    public void setProblemID(long problemID) {
        this.problemID = problemID;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public long getNumSubmissions() {
        return numSubmissions;
    }

    public void setNumSubmissions(long numSubmissions) {
        this.numSubmissions = numSubmissions;
    }

    /**
     * Dummy constructor
     */
    public Problem() {

    }

    /**
     * Creates a new instance of type Problem with the specified parameters.
     * @param problemID Id of the problem
     * @param problemName Name of the problem
     */
    public Problem(long problemID, String problemName) {
        this.problemID = problemID;
        this.problemName = problemName;
        numSubmissions = 0;
    }

    /**
     * Adds new problem to the database based on its category
     * @param problemName Name of the problem to be added. Should be few characters in length and preferably unique
     */
    public static void addProblemToCollection(long problemID, String problemName) {
        Problem problem = new Problem(problemID, problemName);
        Problem.problemCollection.save(problem);
    }

    /**
     * Increases the number of submissions of problem with id as problemID by one
     * @param problemID Id of the problem whose number of successful submissions has to be incremented
     */
    public static void update(long problemID) {
        /**
         * BasicDBObject update = new BasicDBObject();
         * update.append("$inc", new BasicDBObject().append("numSubmissions", 1));
         * Problem.problemCollection.update(new BasicDBObject().append("_id", problemID), update);
         */

        Problem.problemCollection.updateById(problemID, DBUpdate.inc("numSubmissions"));
    }

    /**
     * Gets the number of problems present in the database
     * @return returns the number of problems currently in the database
     */
    public static long getProblemCount() {
        return Problem.problemCollection.getCount();
    }

    /**
     * Lists all problems in the online judge.
     * @return list of type Problem
     */
    public static List<Problem> getAllProblems() {
        List<Problem> problems = Problem.problemCollection.find().toArray();
        return problems;
    }

    /**
     * Lists all problems by their problem id's
     * @param problemIDs list of problem id's
     * @return list of type Problem corresponding to id's
     */
    public static List<Problem> getProblemsById(List problemIDs) {
        List<Problem> problems = new ArrayList<>();
        for (Object object : problemIDs)
            problems.add(problemCollection.findOneById(Long.parseLong(object.toString())));

        return problems;
    }
}
