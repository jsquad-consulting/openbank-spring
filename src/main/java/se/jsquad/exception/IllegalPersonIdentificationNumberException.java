package se.jsquad.exception;

public class IllegalPersonIdentificationNumberException extends RuntimeException {
    public IllegalPersonIdentificationNumberException(String message) {
        super(message);
    }
}