package main.cinemabookingrestapi.exception.alreadyExist;

public class HallNameAlreadyExistsException extends AlreadyExist {
    public HallNameAlreadyExistsException(String name) {
        super("Hall name already exist: " + name);
    }
}
