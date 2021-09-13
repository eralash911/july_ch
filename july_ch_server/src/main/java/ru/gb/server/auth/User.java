package ru.gb.server.auth;

public class User {
    String login;
    String password;
    String Nickname;

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", Password='" + password + '\'' +
                ", Nickname='" + Nickname + '\'' +
                '}';
    }

    public User(String login, String password, String nickname) {
        this.login = login;
        this.password = password;
        Nickname = nickname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }


}
