package controllers;

import controllers.*;
import net.vz.mongodb.jackson.DBCursor;
import play.mvc.*;
import play.data.*;

import models.*;
import views.html.authenticate;
import views.html.index;
import views.html.problems;

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

        return ok(index.render());
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

    public static Result logout() {
        session().clear();
        return redirect(routes.Application.index());
    }
}