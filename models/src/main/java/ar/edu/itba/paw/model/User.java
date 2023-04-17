package ar.edu.itba.paw.model;

public class User {

    private final int userId;
    private final String username;
    private final String password;
    private final String name;
    private final String email;

    public User(int userId, String username, String password, String name, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getUserId() {
        return userId;
    }
}
