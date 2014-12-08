package controllers;

import backend.LocalStorage;
import backend.Status;
import backend.SubmissionStatus;
import models.Problem;
import models.Submission;
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
    public static Result viewProblems() {
        return ok(problems.render(Problem.getAllProblems()));
    }

    public static Result viewSelectedProblem(long id) {
        String directoryName = Long.toString(id);
        return ok(views.html.displayproblem.render(directoryName, id, ""));
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

        return ok(views.html.displayproblem.render(Long.toString(id), id, status.toString()));
    }
}
