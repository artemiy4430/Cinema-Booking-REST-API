package main.cinemabookingrestapi.repository.json;

import main.cinemabookingrestapi.entity.Customer;
import main.cinemabookingrestapi.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JsonCustomerRepository extends JsonRepository<Customer> implements CustomerRepository {
    private final JsonMaxIdRepository maxIdRepository;

    public JsonCustomerRepository(JsonMapper jsonMapper, @Value("./data/customers.json") String filePath, JsonMaxIdRepository maxIdRepository) {
        super(jsonMapper, Path.of(filePath), new TypeReference<List<Customer>>() {});
        this.maxIdRepository = maxIdRepository;
    }
    @Override
    public synchronized List<Customer> getAll() {
        return readEntities();
    }
    @Override
    public synchronized Optional<Customer> getById(Long id) {
        return readEntities().stream().filter(c -> Objects.equals(c.getId(), id)).findFirst();
    }
    @Override
    public synchronized Optional<Customer> getByEmail(String email) {
        return readEntities().stream().filter(c -> Objects.equals(c.getEmail(), email)).findFirst();
    }

    @Override
    public synchronized Customer addCustomer(Customer customer) {
        List<Customer> customers = readEntities();

        if (customers.contains(customer)) return null;

        customer.setId(maxIdRepository.generateNewId(customer));
        customers.add(customer);
        writeEntities(customers);
        return customer;
    }

    @Override
    public synchronized Optional<Customer> updateCustomer(Long id, Customer updatedCustomer) {
        if (updatedCustomer == null || id == null || id < 1) return Optional.empty();

        List<Customer> customers = readEntities();
        Optional<Customer> customer = customers
                .stream()
                .filter(c -> Objects.equals(c.getId(), id))
                .findFirst();

        if (customer.isEmpty()) return Optional.empty();
        updatedCustomer.setId(id);

        customers.remove(customer.get());
        customers.add(updatedCustomer);
        writeEntities(customers);
        return Optional.of(updatedCustomer);
    }

    @Override
    public synchronized boolean removeCustomer(Long id) {
        List<Customer> customers = readEntities();
        Optional<Customer> customer = customers
                .stream()
                .filter(c -> Objects.equals(c.getId(), id))
                .findFirst();

        if (customer.isEmpty()) return false;

        customers.remove(customer.get());
        writeEntities(customers);
        return true;
    }

}
