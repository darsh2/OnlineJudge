package models;

import backend.Status;
import net.vz.mongodb.jackson.*;
import play.modules.mongodb.jackson.MongoDB;

import java.util.*;

/**
 * Created by Darshan on 11/25/2014.
 */
public class Submission {
    @Id
    private long submissionID;

    private DBRef<User, String> user;
    private DBRef<Problem, Long> problem;

    private Date date;
    private Status status;

    private static JacksonDBCollection<Submission, Long> submissionColl = MongoDB.getCollection("submissions", Submission.class, Long.class);

    public long getSubmissionID() {
        return submissionID;
    }

    public void setSubmissionID(long submissionID) {
        this.submissionID = submissionID;
    }

    public DBRef<User, String> getUser() {
        return user;
    }

    public void setUser(DBRef<User, String> user) {
        this.user = user;
    }

    public DBRef<Problem, Long> getProblem() {
        return problem;
    }

    public void setProblem(DBRef<Problem, Long> problem) {
        this.problem = problem;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /*
    Comparator to sort the list of submissions by most recent submissions
     */
    private static Comparator<Submission> sortBy = new Comparator<Submission>() {
        @Override
        public int compare(Submission o1, Submission o2) {
            return o2.date.compareTo(o1.date);
        }
    };

    /**
     * Dummy constructor
     */
    public Submission() {

    }

    /**
     * Creates an instance of type Submission with the specified parameters.
     * @param handle User's handle
     * @param problemID Problem Id of problem
     * @param date Date and time of submission
     * @param status Submission status
     */
    public Submission(String handle, long problemID, Date date, Status status) {
        this.submissionID = Submission.numberOfSubmissions() + 1;
        this.user = new DBRef<>(handle, "users");
        this.problem = new DBRef<>(problemID, "problems");
        this.date = date;
        this.status = status;
    }

    /**
     * Computes the number of submissions made to the online judge.
     * @return number of overall submissions
     */
    public static long numberOfSubmissions() {
        return Submission.submissionColl.getCount();
    }

    /**
     * Adds the new submission to the collection.
     * @param handle User's handle
     * @param problemID Problem Id of the problem for which the user has submitted the solution
     * @param date Date and time of submissions
     * @param status Submission status
     */
    public static void addSubmission(String handle, long problemID, Date date, Status status) {
        Submission submission = new Submission(handle, problemID, date, status);
        Submission.submissionColl.save(submission);
    }

    /**
     * Lists all the submissions made by all users for all problems.
     * @return List containing all submissions made to the judge sorted by time
     */
    public static List<Submission> getAllSubmissions() {
        List<Submission> submissions = submissionColl.find().toArray();
        Collections.sort(submissions, sortBy);
        return submissions;
    }

    /**
     * Lists all submissions for a particular problem based on Id.
     * @param problemID Problem Id of problem whose submissions are queried
     * @return list of submissions for problem with given problem Id sorted by time
     */
    public static List<Submission> getAllSubmissionsForProblem(long problemID) {
        List<Submission> submissions = submissionColl.find(DBQuery.is("problem.$id", problemID)).toArray();
        Collections.sort(submissions, sortBy);
        return submissions;
    }

    /**
     * Lists all submissions made by a user.
     * @param handle User's handle whose submissions are queried
     * @return list of user submissions sorted by time
     */
    public static List<Submission> getUserSubmissions(String handle) {
        List<Submission> submissions = submissionColl.find(DBQuery.is("user.$id", handle)).toArray();
        Collections.sort(submissions, sortBy);
        return submissions;
    }

    /**
     * Lists all submissions made by a user for a particular problem.
     * @param handle User's handle
     * @param problemID Problem Id of problem whose submissions are queried
     * @return list of user submissions for the queried problem sorted by time
     */
    public static List<Submission> getUserSubmissionsForProblem(String handle, long problemID) {
        List<Submission> submissions = submissionColl.find(DBQuery.and(DBQuery.is("user.$id", handle), DBQuery.is("problem.$id", problemID))).toArray();
        Collections.sort(submissions, sortBy);
        return submissions;
    }
}
