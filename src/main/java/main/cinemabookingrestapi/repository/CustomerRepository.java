package main.cinemabookingrestapi.repository;

import main.cinemabookingrestapi.entity.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    List<Customer> getAll();

    Optional<Customer> getById(Long id);

    Optional<Customer> getByEmail(String email);

    Customer addCustomer(Customer customer);

    Optional<Customer> updateCustomer(Long id, Customer updatedCustomer);

    boolean removeCustomer(Long id);
}
