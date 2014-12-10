package models;

import com.mongodb.BasicDBObject;
import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.modules.mongodb.jackson.MongoDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darshan on 11/24/2014.
 */
public class User {
    @Id
    private String handle;

    private String firstName, lastName, college, password;
    private int solved, submitted;

    private static JacksonDBCollection<User, String> usersColl = MongoDB.getCollection("users", User.class, String.class);

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSolved() {
        return solved;
    }

    public void setSolved(int solved) {
        this.solved = solved;
    }

    public int getSubmitted() {
        return submitted;
    }

    public void setSubmitted(int submitted) {
        this.submitted = submitted;
    }

    /**
     * Dummy constructor used to prevent this: org.codehaus.jackson.map.JsonMappingException: No suitable constructor found
     * for type [simple type, class models.User]: can not instantiate from JSON object (need to add/enable type information?)
     */
    public User() {

    }

    /**
     * User defined constructor for Collection of users. Each row is an object of class User.
     * @param handle User's handle
     * @param firstName User's first name
     * @param lastName User's last name
     * @param college User's college
     * @param password User's password
     */
    public User(String handle, String firstName, String lastName, String college, String password) {
        this.handle = handle;
        this.firstName = firstName;
        this.lastName = lastName;
        this.college = college;
        this.password = password;
        solved = 0; submitted = 0;
    }

    /**
     * Checks if user entered values for the specific fields are correct.
     * Characters that can be used in handle include [a-zA-Z0-9@_]
     * First and last name must not be blank and must contain characters [a-zA-Z.]
     * @param handle User's handle
     * @param firstName User's first name
     * @param lastName User's last name
     * @param college User's college
     * @param password User's password
     * @param confirm Re-enter password for confirmation
     * @return returns a null String if all details are correctly entered else returns String indicating error
     */
    public static String validate(String handle, String firstName, String lastName, String college, String password, String confirm) {
        if (handle.matches("[^a-zA-Z@0-9_]"))
            return "Handle can contain only characters: a-z A-z 0-9 @ _";
        User match = usersColl.findOneById(handle);
        if (match != null)
            return "Handle already exists";

        if (firstName.length() == 0 || lastName.length() == 0)
            return "Name required";
        if (firstName.matches("[^a-zA-Z.]") || lastName.matches("[^a-zA-Z.]"))
            return "Unexpected character in name";

        if (password.compareTo(confirm) != 0)
            return "Passwords don't match";

        User.addNewUser(handle, firstName, lastName, college, password);
        return null;
    }

    /**
     * Checks if user with given handle and password exists
     * @param handle User's handle
     * @param password User's password
     * @return returns null if such a user exists else returns a String indicating error
     */
    public static String checkIfExistingUser(String handle, String password) {
        User match = usersColl.findOneById(handle);
        if (match == null)
            return "Incorrect handle";

        if (password.compareTo(match.password) != 0)
            return "Incorrect password";

        return null;
    }

    /**
     * Adds a new user to the database.
     * @param handle User's handle
     * @param firstName User's first name
     * @param lastName User's last name
     * @param college User's college
     * @param password User's password
     */
    public static void addNewUser(String handle, String firstName, String lastName, String college, String password) {
        User user = new User(handle, firstName, lastName, college, password);
        User.usersColl.save(user);
    }

    /**
     * Lists all the registered users. A cursor is used to iterate over all users and the users are then added to a list.
     * @return returns a list of all registered users
     */
    public static List<User> getAllUsers() {
        return User.usersColl.find().toArray();
    }

    public static User getUserByHandle(String handle) {
        return usersColl.findOneById(handle);
    }
}
