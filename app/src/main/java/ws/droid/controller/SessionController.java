package ws.droid.controller;

import android.app.Application;

public class SessionController extends Application {

    private static SessionController instance;
    private String sessionEmail;
    private String sessionPassword;
    private String sessionHref;

    public static SessionController getInstance() {
        if (instance == null)
            instance = new SessionController();
        return instance;
    }

    public static void destroyInstance() {
        if (instance != null)
            instance = null;
    }

    public String getSessionEmail() {
        return sessionEmail;
    }

    public void setSessionEmail(String sessionEmail) {
        this.sessionEmail = sessionEmail;
    }

    public String getSessionPassword() {
        return sessionPassword;
    }

    public void setSessionPassword(String sessionPassword) {
        this.sessionPassword = sessionPassword;
    }

    public String getSessionHref() {
        return sessionHref;
    }

    public void setSessionHref(String sessionHref) {
        this.sessionHref = sessionHref;
    }
}
