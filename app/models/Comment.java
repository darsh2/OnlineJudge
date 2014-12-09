package models;

import net.vz.mongodb.jackson.*;
import play.modules.mongodb.jackson.MongoDB;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Darshan on 12/9/2014.
 */
public class Comment {
    @Id
    @ObjectId
    private String commentID;

    private DBRef<User, String> user;
    private DBRef<Problem, Long> problem;

    private String comment;
    private Date date;

    private static JacksonDBCollection<Comment, String> commentColl = MongoDB.getCollection("comments", Comment.class, String.class);

    private static Comparator<Comment> sortBy = new Comparator<Comment>() {
        @Override
        public int compare(Comment o1, Comment o2) {
            return o2.date.compareTo(o1.date);
        }
    };

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Dummy constructor
     */
    public Comment() {

    }

    /**
     * Instantiates a new Comment object with the specified parameters.
     * @param handle User's handle
     * @param problemID Problem Id of the problem
     * @param comment Comment body
     * @param date Date of posting comment
     */
    public Comment(String handle, long problemID, String comment, Date date) {
        this.user = new DBRef<>(handle, "users");
        this.problem = new DBRef<>(problemID, "problems");
        this.comment = comment;
        this.date = date;
    }

    /**
     * Adds a new comment to the collection.
     * @param handle User's handle
     * @param problemID Problem Id of the problem
     * @param commentBody Comment body
     */
    public static void addComment(String handle, long problemID, String commentBody) {
        Comment comment = new Comment(handle, problemID, commentBody, new Date());
        commentColl.save(comment);
    }

    /**
     * Lists all comments for a particular problem.
     * @param problemID Problem Id of the problem
     * @return list of all comments for the problem
     */
    public static List<Comment> getAllComments(long problemID) {
        List<Comment> comments = commentColl.find(DBQuery.is("problem.$id", problemID)).toArray();
        Collections.sort(comments, sortBy);
        return comments;
    }

    /**
     * Deletes a particular comment based on commentID
     * @param commentID Comment Id of the comment to be deleted
     */
    public static void deleteComment(String commentID) {
        commentColl.removeById(commentID);
    }
}
