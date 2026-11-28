package main.cinemabookingrestapi.exception.isPresentException;


public class HallIsPresentInScreeningException extends IsPresentException {
    public HallIsPresentInScreeningException(Long id) {
        super("Hall: " + id + " is present in the screening");
    }
}
