package service;

import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

public class UserSession {

    private static UserSession instance;

    private String userName;

    private String password;
    private String privileges;

    private UserSession(String userName, String password, String privileges) {
        this.userName = userName;
        this.password = password;
        this.privileges = privileges;
        Preferences userPreferences = Preferences.userRoot().node(this.getClass().getName());
        userPreferences.put("USERNAME", userName);
        userPreferences.put("PASSWORD", password);
        userPreferences.put("PRIVILEGES", privileges);
    }



    public static UserSession getInstace(String userName,String password, String privileges) {
        if(instance == null) {
            synchronized (UserSession.class) {
                if(instance == null) {
                    instance = new UserSession(userName,password,privileges);
                }
            }
        }
        return instance;
    }

    public static UserSession getInstace(String userName,String password) {
        if(instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession(userName, password, "NONE");
                }
            }
        }
        return instance;
    }

    // Method to load user session from Preferences
    public static UserSession loadUserSession() {
        Preferences userPreferences = Preferences.userRoot().node(UserSession.class.getName());
        String userName = userPreferences.get("USERNAME", null);
        String password = userPreferences.get("PASSWORD", null);
        String privileges = userPreferences.get("PRIVILEGES", "NONE");

        if (userName != null && password != null) {
            return new UserSession(userName, password, privileges);
        }
        return null;  // No saved session found
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public String getPrivileges() {
        return this.privileges;
    }

    public void cleanUserSession() {
        this.userName = "";// or null
        this.password = "";
        this.privileges = "";// or null

        Preferences userPreferences = Preferences.userRoot().node(this.getClass().getName());
        userPreferences.remove("USERNAME");
        userPreferences.remove("PASSWORD");
        userPreferences.remove("PRIVILEGES");
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "userName='" + this.userName + '\'' +
                ", privileges=" + this.privileges +
                '}';
    }
}
