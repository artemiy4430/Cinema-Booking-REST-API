package main.cinemabookingrestapi.exception.inactive;

public abstract class InactiveException extends RuntimeException {
    public InactiveException(String message) {
        super("The given entity is inactive");
    }
}
