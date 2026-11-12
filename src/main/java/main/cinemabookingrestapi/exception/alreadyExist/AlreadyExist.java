package main.cinemabookingrestapi.exception.alreadyExist;

public abstract class AlreadyExist extends RuntimeException {
    public AlreadyExist(String message) {
        super(message);
    }
}
