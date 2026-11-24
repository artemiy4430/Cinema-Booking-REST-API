package main.cinemabookingrestapi.exception.alreadyExist;

public class MovieAlreadyExistsException extends AlreadyExist {
    public MovieAlreadyExistsException() {
        super("given movie already exists");
    }
}
