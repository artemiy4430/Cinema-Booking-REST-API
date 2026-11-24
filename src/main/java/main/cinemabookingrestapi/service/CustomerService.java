package main.cinemabookingrestapi.service;

import main.cinemabookingrestapi.dto.customer.CreateCustomerRequest;
import main.cinemabookingrestapi.dto.customer.CustomerResponse;
import main.cinemabookingrestapi.dto.customer.UpdateCustomerRequest;
import main.cinemabookingrestapi.entity.Customer;
import main.cinemabookingrestapi.exception.alreadyExist.CustomerEmailAlreadyExistsException;
import main.cinemabookingrestapi.exception.notFound.CustomerNotFoundException;
import main.cinemabookingrestapi.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAll() {
        return customerRepository.getAll();
    }

    public Optional<Customer> getById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException();

       Customer customer = customerRepository.getById(id).orElseThrow(() -> new CustomerNotFoundException(id));

       return Optional.of(customer);
    }

    public Optional<Customer> getByEmail(String email) {
        if (email == null) throw new IllegalArgumentException();

        Customer customer = customerRepository.getByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException(email));

        return Optional.of(customer);
    }

    public CustomerResponse addCustomer(CreateCustomerRequest request) {
        if (request == null) throw new IllegalArgumentException();
        List<Customer> customers = customerRepository.getAll();

        boolean isPresent = customers.stream()
                .anyMatch(c -> Objects.equals(c.getEmail(), request.getEmail()));

        if (isPresent) throw new CustomerEmailAlreadyExistsException(request.getEmail());
        Customer customer = request.toEntity();

        customer.setActive(true);

        Customer savedCustomer = customerRepository.addCustomer(customer);

        if (savedCustomer == null) throw new IllegalArgumentException();

        return CustomerResponse.fromEntity(savedCustomer);
    }

    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request) {
        if (request == null || id == null || id < 0) throw new IllegalArgumentException();

        Customer customer = customerRepository.getById(id).orElseThrow(() -> new CustomerNotFoundException(id));

        boolean isEmailTaken = customerRepository.getAll().stream()
                .filter(c -> !Objects.equals(c.getId(), id))
                .anyMatch(c -> Objects.equals(c.getEmail(), request.getEmail()));

        if (isEmailTaken) throw new CustomerEmailAlreadyExistsException(request.getEmail());

        Customer updatedCustomer = customerRepository
                .updateCustomer(id, request.updateCustomer(customer))
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return CustomerResponse.fromEntity(updatedCustomer);
    }

    public void removeCustomer(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException();

        Customer customer = customerRepository.getById(id).orElseThrow(() -> new CustomerNotFoundException(id));

        customer.setActive(false);

        customerRepository.updateCustomer(id, customer);
    }
}
