package controllers;

import controllers.*;
import net.vz.mongodb.jackson.DBCursor;
import play.mvc.*;
import play.data.*;

import models.*;
import views.html.authenticate;
import views.html.index;
import views.html.problems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Application extends Controller {
    public static class SignUp {
        public String handle, firstname, lastname, college, password, confirm;

        public String validate() {
            return User.validate(handle, firstname, lastname, college, password, confirm);
        }
    }

    public static class Login {
        public String handle, password;

        public String validate() {
            return User.checkIfExistingUser(handle, password);
        }
    }

    private static Form<SignUp> signUpForm = Form.form(SignUp.class);
    private static Form<Login> loginForm = Form.form(Login.class);

    public static class ViewHelper {
        public int count;
        public ArrayList<Problem> problems;

        public ViewHelper() {
            count = 0;
            problems = new ArrayList<>();
        }
    }

    public static class LeaderboardHelper {
        public long rank, solved;
        public String handle;

        public LeaderboardHelper(int rank, long solved, String handle) {
            this.rank = rank;
            this.solved = solved;
            this.handle = handle;
        }
    }

    public static Result index() {
        return ok(views.html.index.render());
    }

    public static Result authenticate() {
        return ok(authenticate.render(Form.form(SignUp.class), Form.form(Login.class)));
    }

    public static Result addUser() {
        Form<SignUp> filledForm = signUpForm.bindFromRequest();
        if (filledForm.hasErrors())
            return badRequest(authenticate.render(filledForm, Form.form(Login.class)));

        return redirect(routes.Application.index());
    }

    public static Result loginUser() {
        Form<Login> filledForm = loginForm.bindFromRequest();
        if (filledForm.hasErrors())
            return badRequest(authenticate.render(Form.form(SignUp.class), filledForm));

        session().clear();
        session("handle", filledForm.get().handle);

        if (filledForm.get().handle.compareTo("admin") == 0)
            return redirect(controllers.routes.AdminActions.viewOptions());

        return redirect(controllers.routes.ProblemsController.viewProblems());
    }

    public static Result viewLeaderBoard() {
        ArrayList<ArrayList<String>> numSolved = new ArrayList<ArrayList<String>>();
        long numProblems = Problem.getProblemCount() + 1;
        for (long i = 0; i <= numProblems; i++)
            numSolved.add(new ArrayList<>());

        List<User> users = User.getAllUsers();
        for (User user : users)
            numSolved.get(Submission.getUniqueUserSolvedProblems(user.getHandle()).size()).add(user.getHandle());

        ArrayList<LeaderboardHelper> leaderboard = new ArrayList<>();
        int pos = 1;
        for ( ; numProblems > -1; numProblems--) {
            int count = 0;
            Collections.sort(numSolved.get((int) numProblems));
            for (String handle : numSolved.get((int) numProblems)) {
                leaderboard.add(new LeaderboardHelper(pos, numProblems, handle));
                count++;
            }
            pos += count;
        }

        return ok(views.html.leaderboard.render(leaderboard));
    }

    public static Result userProfile(String handle) {
        if (handle.compareTo("admin") == 0)
            return redirect(routes.AdminActions.viewOptions());

        List<Problem> problems = Submission.getUniqueUserSolvedProblems(handle);
        ArrayList<ViewHelper> viewHelpers = new ArrayList<>();
        ViewHelper viewHelper = new ViewHelper();

        for (Problem problem : problems) {
            if (viewHelper.count == 4) {
                viewHelpers.add(viewHelper);
                viewHelper = new ViewHelper();
            }
            viewHelper.problems.add(problem);
            viewHelper.count++;
        }
        viewHelpers.add(viewHelper);

        return ok(views.html.userprofile.render(User.getUserByHandle(handle), problems.size() * 1L, Submission.getCountUserSubmissions(handle), viewHelpers));
    }

    public static Result logout() {
        session().clear();
        return redirect(routes.Application.index());
    }
}
