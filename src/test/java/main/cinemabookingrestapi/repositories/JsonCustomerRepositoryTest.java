package main.cinemabookingrestapi.repositories;

import main.cinemabookingrestapi.entity.Customer;
import main.cinemabookingrestapi.repository.json.JsonCustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class JsonCustomerRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonCustomerRepository repository;

    @BeforeEach
    void setUp() {
        Path file = tempDir.resolve("temp_customers.json");

        repository = new JsonCustomerRepository(new JsonMapper(), file.toString());
    }

    @Test
    void getAll_shouldReturnEmptyList_whenFileDoesNotExist() {
        assertThat(repository.getAll()).isEmpty();
    }

    @Test
    void getAll_shouldReturnListOfCustomers() {
        Customer customer1 = createCustomer();
        Customer customer2 = new Customer(
                2L,
                "Petr",
                "Petrov",
                "petrov@gmail.com",
                "+4833288943",
                true
        );

        repository.addCustomer(customer1);
        repository.addCustomer(customer2);

        List<Customer> customers = repository.getAll();

        assertNotNull(customers);
        assertEquals(2, customers.size());
        assertEquals(1L, customers.get(0).getId());
        assertEquals(2L, customers.get(1).getId());
    }

    @Test
    void getById_shouldReturnEmptyOptional_whenCustomerIsNotFound() {
        Long incorrectId = 12L;

        Optional<Customer> customer = repository.getById(incorrectId);

        assertNotNull(customer);
        assertTrue(customer.isEmpty());
    }

    @Test
    void getById_shouldReturnOptionalOfCustomer() {
        Customer customer = createCustomer();
        repository.addCustomer(customer);

        Optional<Customer> customerOptional = repository.getById(customer.getId());

        assertNotNull(customerOptional);
        assertTrue(customerOptional.isPresent());
        assertEquals(customer.getId(), customerOptional.get().getId());
    }

    @Test
    void getByEmail_shouldReturnEmptyOptional_whenCustomerIsNotFound() {
        String incorrectEmail = "customer@gmail.com";

        Optional<Customer> customer = repository.getByEmail(incorrectEmail);

        assertNotNull(customer);
        assertTrue(customer.isEmpty());
    }

    @Test
    void getByEmail_shouldReturnOptionalOfCustomer() {
        Customer customer = createCustomer();
        repository.addCustomer(customer);

        Optional<Customer> customerOptional = repository.getByEmail(customer.getEmail());

        assertNotNull(customerOptional);
        assertTrue(customerOptional.isPresent());
        assertEquals(customer.getEmail(), customerOptional.get().getEmail());
    }

    @Test
    void addCustomer_shouldGenerateIdAndAddEntityInFile() {
        Customer customer = createCustomer();
        customer.setId(null);
        Customer addedCustomer = repository.addCustomer(customer);

        assertEquals(customer.getEmail(), addedCustomer.getEmail());
        assertEquals(1, repository.getAll().size());

        Customer loadedCustomer = repository.getById(addedCustomer.getId()).orElseThrow();
        assertThat(loadedCustomer).usingRecursiveComparison().isEqualTo(addedCustomer);
    }

    @Test
    void updateCustomer_shouldReturnEmptyOptional_whenCustomerIsNotFound(){
        Long incorrectId = 12L;
        Customer customer = createCustomer();

        Optional<Customer> customerOptional = repository.updateCustomer(incorrectId, customer);

        assertNotNull(customerOptional);
        assertTrue(customerOptional.isEmpty());
    }

    @Test
    void updateCustomer_shouldReturnOptionalOfUpdatedCustomer() {
        Customer customer = createCustomer();
        repository.addCustomer(customer);
        Customer updatedCustomer = new Customer(
                customer.getId(),
                "Ivan",
                "Ivanov",
                "ivanovskyi@gmail.com",
                "+48225476856", true
        );

        Optional<Customer> customerOptional = repository.updateCustomer(customer.getId(), updatedCustomer);

        assertNotNull(customerOptional);
        assertTrue(customerOptional.isPresent());
        assertEquals(updatedCustomer.getId(), customerOptional.get().getId());
        assertEquals(updatedCustomer.getEmail(), customerOptional.get().getEmail());
    }

    @Test
    void removeCustomer_shouldReturnFalse_whenCustomerIsNotPresent() {
        Long incorrectId = 12L;

        boolean result = repository.removeCustomer(incorrectId);

        assertFalse(result);
    }

    @Test
    void removeCustomer_shouldReturnTrue() {
        Customer customer = createCustomer();
        repository.addCustomer(customer);

        boolean result = repository.removeCustomer(customer.getId());

        assertTrue(result);
    }


    private Customer createCustomer() {
        return new Customer(1L, "Ivan", "Ivanov",
                "ivanov@gmail.com", "+48225476856", true );
    }

}
