package main.cinemabookingrestapi.exception.isPresentException;

public class MovieIsPresentInScreeningException extends IsPresentException {
    public MovieIsPresentInScreeningException(Long id) {
        super("Movie: " + id + " is present in screening");
    }
}
