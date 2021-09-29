package ru.gb.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class Database {

    private static Connection connection;
    private static Statement statement;
    private static final String dbClass = "org.sqlite.JDBC";
    private static final String dbURL = "jdbc:sqlite:chat.db";


    public static void main(String[] args)  {

        Database.connect ();


//       Database.disconnect();


    }


    synchronized static void connect() {
        try {
            Class.forName(dbClass);
            openDatabase(Paths.get("at.db"));
            connection = DriverManager.getConnection(dbURL);
            statement = connection.createStatement();
            statement.execute("create table if not exists users " +
                    "(login varchar primary key, password varchar, 'nickname' varchar)");
            addUser("Vasya","pass","Petya");
            updateUserData("LoG", "Col", "123");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void openDatabase(Path dbPath) {
        if (!Files.exists(dbPath)) {
            try {
                Files.createFile(dbPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    synchronized static void updateUserData(String login, String column, String value) {
        try {
            statement.execute(String.format("update users set %s = '%s' where login = '%s'", column, value, login));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    synchronized static void addUser(String login, String password) {
        addUser(login, password, login);
    }
    synchronized static void addUser(String login, String password, String nickname) {
        try {
            statement.execute(String.format("insert into users ('login', 'password', 'nickname') values ('%s', '%s', '%s')",
                    login, password, nickname));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
//
    synchronized static String getData(String table, String column, String criteria, String data) {
        try {
            String query = String.format("select %s from %s where %s = '%s'", column, table, criteria, data);
            ResultSet set = statement.executeQuery(query);
            if (set.next()) {
                return set.getString(column);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}