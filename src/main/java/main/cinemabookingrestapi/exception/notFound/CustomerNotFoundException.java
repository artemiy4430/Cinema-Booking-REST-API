package main.cinemabookingrestapi.exception.notFound;

public class CustomerNotFoundException extends NotFoundException {
    public CustomerNotFoundException(Long id) {
        super("Customer not found with id: " + id);
    }

    public CustomerNotFoundException(String email) {
        super("Customer not found with email: " + email);
    }
}
