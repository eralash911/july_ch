package ru.gb.server.error;

public class WrongCredentialsException extends RuntimeException{
    public WrongCredentialsException(String message) {
        super(message);
    }
}
