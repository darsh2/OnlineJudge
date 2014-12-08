package controllers;

import models.Submission;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import java.io.File;

/**
 * Created by Darshan on 12/7/2014.
 */
public class SubmissionsController extends Controller {
    public static Result viewAllSubmissions() {
        return ok(views.html.submissions.render("All Submissions", Submission.getAllSubmissions()));
    }

    public static Result viewProblemSubmissions(long problemID) {
        return ok(views.html.submissions.render("Submissions", Submission.getAllSubmissionsForProblem(problemID)));
    }

    public static Result viewUserSubmissions(long problemID) {
        return ok(views.html.submissions.render("My Submissions", Submission.getUserSubmissionsForProblem(session().get("handle"), problemID)));
    }

    public static Result viewSubmissionCode(String user, long problemID, long submissionID) {
        return ok(views.html.viewsubmission.render(user, Long.toString(problemID), Long.toString(submissionID)));
    }
}
