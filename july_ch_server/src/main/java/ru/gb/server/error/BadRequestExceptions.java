package ru.gb.server.error;

public class BadRequestExceptions extends RuntimeException{
    public BadRequestExceptions(String message) {
        super(message);
    }
}
