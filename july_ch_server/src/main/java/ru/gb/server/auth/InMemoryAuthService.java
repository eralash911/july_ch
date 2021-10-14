package ru.gb.server.auth;

import ru.gb.server.db.ClientsDatabaseService;

import java.sql.SQLException;


public class InMemoryAuthService implements AuthService{
    private ClientsDatabaseService dbService;

    @Override
    public void start() {
        dbService = ClientsDatabaseService.getInstance();
    }

    @Override
    public void stop() {
        dbService.closeConnection();
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String pass) {
        return dbService.getClientNameByLoginPass(login, pass);
    }

    @Override
    public String changeNickname(String oldName, String newName) {
        try {
            return dbService.changeUsername(oldName, newName);
        } catch (SQLException e) {
            throw new RuntimeException("Nickname change unsuccessful");
        }
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {

    }

    @Override
    public void createNewUser(String login, String password, String nickname) {

    }

    @Override
    public void deleteUser(String nickname) {

    }
}
