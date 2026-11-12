package main.cinemabookingrestapi.exception.alreadyExist;

public class CustomerEmailAlreadyExistsException extends AlreadyExist {
    public CustomerEmailAlreadyExistsException(String email) {
        super("Customer email already exist: " + email);
    }
}
