package ru.gb.server.auth;

import ru.gb.server.error.UserNotFoundException;
import ru.gb.server.error.WrongCredentialsException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryAuthService implements AuthService{
    private List<User> users;

    public InMemoryAuthService(){
        this.users = new ArrayList<>(
               List.of(
        new User("log1","pass", "nick1"),
        new User("log2","pass", "nick1"),
        new User("log3","pass", "nick1"),
        new User("log4","pass", "nick1")

               )
         //       Arrays.asList(new User(""))
        );
    }
    @Override
    public void start() {
        System.out.println("started");
    }

    @Override
    public void stop() {
        System.out.println("stopped");
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (login.equals(user.getLogin())) {
                if (password.equals(user.getPassword())) return user.getNickname();
                else throw new WrongCredentialsException("Неверные креденчиалсы");
            }
        }
        throw new UserNotFoundException("User not found");
    }

    @Override
    public String changeNickname(String oldNick, String newNick) {
        return null;
    }

    @Override
    public void changePassword(String nickName, String oldPassword, String newPassword) {

    }

    @Override
    public void createNewUser(String login, String password, String nickName) {

    }

    @Override
    public void deleteUser(String nickName) {

    }
}
