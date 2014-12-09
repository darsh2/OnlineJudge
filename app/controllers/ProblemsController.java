package controllers;

import backend.LocalStorage;
import backend.Status;
import backend.SubmissionStatus;
import models.Comment;
import models.Problem;
import models.Submission;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.*;
import play.mvc.Result;
import views.html.problems;

import java.util.Date;

/**
 * Created by Darshan on 11/28/2014.
 */
public class ProblemsController extends Controller {
    public static class UserComment {
        public String commentbody;

        public String validate() {
            if (commentbody.length() > 0)
                return null;
            return "Comment must not be empty";
        }
    }

    private static Form<UserComment> form = Form.form(UserComment.class);

    public static Result viewProblems() {
        return ok(problems.render(Problem.getAllProblems()));
    }

    public static Result viewSelectedProblem(long id) {
        String directoryName = Long.toString(id);
        return ok(views.html.displayproblem.render(form, directoryName, id, "", Comment.getAllComments(id)));
    }

    public static Result submitSolution(long id) {
        MultipartFormData multipartFormData = request().body().asMultipartFormData();
        FilePart filePart = multipartFormData.getFile("solution");

        String[] fileAttributes = LocalStorage.saveSubmission(session().get("handle"), id, filePart.getFile());
        String inputFilePath = LocalStorage.problemDirectory + id, submissionID = Long.toString(Submission.numberOfSubmissions() + 1);

        backend.Status status = new SubmissionStatus(fileAttributes[0], fileAttributes[1], fileAttributes[2], inputFilePath, submissionID).getStatus();
        Submission.addSubmission(session().get("handle"), id, new Date(), status);

        if (status == backend.Status.AC) {
            System.out.println("Reached here");
            Problem.update(id);
        }

        return ok(views.html.displayproblem.render(form, Long.toString(id), id, status.toString(), Comment.getAllComments(id)));
    }

    public static Result addComment(long problemID) {
        Form<UserComment> filledForm = form.bindFromRequest();
        if (filledForm.hasErrors())
            return badRequest(views.html.displayproblem.render(filledForm, Long.toString(problemID), problemID, "", Comment.getAllComments(problemID)));

        Comment.addComment(session().get("handle"), problemID, filledForm.get().commentbody);
        return redirect(controllers.routes.ProblemsController.viewSelectedProblem(problemID));
    }

    public static Result deleteComment(long problemID, String commentID) {
        Comment.deleteComment(commentID);
        return redirect(controllers.routes.ProblemsController.viewSelectedProblem(problemID));
    }
}
