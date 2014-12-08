package controllers;

import backend.LocalStorage;
import models.Submission;
import play.mvc.Controller;
import play.mvc.Http.*;
import play.mvc.Http.MultipartFormData.*;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by Darshan on 11/26/2014.
 */
@Security.Authenticated(Secured.class)
public class AdminActions extends Controller {
    /**
     * Action that displays all the admin previliges.
     * @return web page listing admin previliges
     */
    public static Result viewOptions() {
        return ok(views.html.admin.render());
    }

    /**
     * Action that adds the new problem based on its category to the collection. Creates a
     * new directory with directory name as the current problem count + 1, and stores the problem statement, ip and op files
     * as "problem.txt", "ip.txt" and "op.txt" respectively.
     * @return Redirects back to admin home page with success or error message
     */
    public static Result addProblem() {
        MultipartFormData multipartFormData = request().body().asMultipartFormData();

        /*
        extracting the 3 fileparts
         */
        FilePart filePartP = multipartFormData.getFile("problem"),
                filePartI = multipartFormData.getFile("ip"),
                filePartO = multipartFormData.getFile("op");

        /*
        if any file part is null, indicates missing file
         */
        if (filePartP == null || filePartI == null || filePartO == null) {
            flash("Error", "Missing file");
            return redirect(routes.AdminActions.viewOptions());
        } else {
            if (LocalStorage.saveProblem(filePartP, filePartI, filePartO))
                return ok(views.html.admin.render());
            else
                return badRequest();
        }
    }

    /**
     * Action that opens the page from where admin can view the submitted code of the users
     * @return page where admin can view all submissions
     */
    public static Result viewSubmissions() {
        return SubmissionsController.viewAllSubmissions();
    }
}
