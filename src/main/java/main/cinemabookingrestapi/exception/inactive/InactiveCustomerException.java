package main.cinemabookingrestapi.exception.inactive;

public class InactiveCustomerException extends InactiveException {
    public InactiveCustomerException(String message) {
        super(message);
    }
}
