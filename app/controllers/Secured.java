package controllers;

import play.mvc.Http.*;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by Darshan on 11/25/2014.
 */
public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context context) {
        return context.session().get("handle");
    }

    @Override
    public Result onUnauthorized(Context context) {
        return redirect(routes.Application.index());
    }

}
