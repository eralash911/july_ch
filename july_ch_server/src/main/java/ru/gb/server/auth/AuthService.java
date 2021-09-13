package ru.gb.server.auth;

public interface AuthService {
    void start();
    void stop();
    String getNicknameByLoginAndPassword(String login, String password);
    String changeNickname(String oldNick, String newNick);
    void changePassword(String nickName, String oldPassword, String newPassword);
    void createNewUser(String login, String password,String nickName);
    void deleteUser(String nickName);
}
