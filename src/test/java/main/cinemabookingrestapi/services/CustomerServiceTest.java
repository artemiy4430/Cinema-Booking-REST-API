package main.cinemabookingrestapi.services;

import main.cinemabookingrestapi.dto.customer.CreateCustomerRequest;
import main.cinemabookingrestapi.dto.customer.CustomerResponse;
import main.cinemabookingrestapi.dto.customer.UpdateCustomerRequest;
import main.cinemabookingrestapi.entity.Customer;
import main.cinemabookingrestapi.exception.alreadyExist.CustomerEmailAlreadyExistsException;
import main.cinemabookingrestapi.exception.notFound.CustomerNotFoundException;
import main.cinemabookingrestapi.repository.CustomerRepository;
import main.cinemabookingrestapi.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
    public class CustomerServiceTest {

        @Mock
        private CustomerRepository customerRepository;

        @InjectMocks
        private CustomerService customerService;

        @Test
        void getAll_shouldReturnEmptyList() {
            when(customerRepository.getAll()).thenReturn(Collections.emptyList());

            List<Customer> customers = customerService.getAll();

            assertNotNull(customers);
            assertTrue(customers.isEmpty());

            verify(customerRepository).getAll();
        }

        @Test
        void getAll_shouldReturnListOfCustomers() {
            List<Customer> customerList = createList();

            when(customerRepository.getAll()).thenReturn(customerList);

            List<Customer> customers = customerService.getAll();

            assertNotNull(customers);
            assertEquals(3, customers.size());
            assertEquals("Ivan", customers.get(0).getName());
            assertFalse(customers.get(2).isActive());

            verify(customerRepository).getAll();
        }

        @Test
        void getById_shouldReturnCustomer() {
            Customer customer = createCustomer();

            when(customerRepository.getById(customer.getId())).thenReturn(Optional.of(customer));

            Optional<Customer> customerOptional = customerService.getById(customer.getId());

            assertTrue(customerOptional.isPresent());
            assertEquals(customer.getId(), customerOptional.get().getId());
            assertEquals(customer.getEmail(), customerOptional.get().getEmail());

            verify(customerRepository).getById(1L);
        }

        @Test
        void getById_shouldThrowIllegalArgumentException_whenIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> customerService.getById(null));

            verifyNoInteractions(customerRepository);
        }

        @Test
        void getById_shouldThrowIllegalArgumentException_whenIdIsLessThanOne() {
            assertThrows(IllegalArgumentException.class, () -> customerService.getById(0L));

            verifyNoInteractions(customerRepository);
        }

        @Test
        void getById_shouldThrowCustomerNotFoundException_whenNotFound() {
            when(customerRepository.getById(99L)).thenReturn(Optional.empty());

            assertThrows(CustomerNotFoundException.class, () -> customerService.getById(99L));

            verify(customerRepository).getById(99L);
        }

        @Test
        void getByEmail_shouldReturnCustomer() {
            Customer customer = createCustomer();

            when(customerRepository.getByEmail("ivanov@gmail.com")).thenReturn(Optional.of(customer));

            Optional<Customer> result = customerService.getByEmail("ivanov@gmail.com");

            assertTrue(result.isPresent());
            assertEquals("ivanov@gmail.com", result.get().getEmail());

            verify(customerRepository).getByEmail("ivanov@gmail.com");
        }

        @Test
        void getByEmail_shouldThrowIllegalArgumentException_whenEmailIsNull() {
            assertThrows(IllegalArgumentException.class, () -> customerService.getByEmail(null));

            verifyNoInteractions(customerRepository);
        }

        @Test
        void getByEmail_shouldThrowCustomerNotFoundException_whenNotFound() {
            when(customerRepository.getByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

            assertThrows(CustomerNotFoundException.class, () -> customerService.getByEmail("unknown@gmail.com"));

            verify(customerRepository).getByEmail("unknown@gmail.com");
        }

        @Test
        void addCustomer_shouldReturnCustomerResponseAndSetActiveTrue() {
            CreateCustomerRequest request = new CreateCustomerRequest("Oleg", "Sidorov", "sidorov@gmail.com", "+48111222333");
            Customer savedCustomer = new Customer(4L, "Oleg", "Sidorov", "sidorov@gmail.com", "+48111222333", true);

            when(customerRepository.getAll()).thenReturn(createList());
            when(customerRepository.addCustomer(any(Customer.class))).thenReturn(savedCustomer);

            CustomerResponse response = customerService.addCustomer(request);

            assertNotNull(response);
            assertEquals("sidorov@gmail.com", response.getEmail());

            ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
            verify(customerRepository).getAll();
            verify(customerRepository).addCustomer(customerCaptor.capture());

            assertTrue(customerCaptor.getValue().isActive());
        }

        @Test
        void addCustomer_shouldThrowIllegalArgumentException_whenRequestIsNull() {
            assertThrows(IllegalArgumentException.class, () -> customerService.addCustomer(null));

            verifyNoInteractions(customerRepository);
        }

        @Test
        void addCustomer_shouldThrowCustomerEmailAlreadyExistsException_whenEmailExists() {
            CreateCustomerRequest request = new CreateCustomerRequest("Ivan", "Ivanov", "ivanov@gmail.com", "+48999888777");

            when(customerRepository.getAll()).thenReturn(createList());

            assertThrows(CustomerEmailAlreadyExistsException.class, () -> customerService.addCustomer(request));

            verify(customerRepository).getAll();
            verify(customerRepository, never()).addCustomer(any(Customer.class));
        }

        @Test
        void addCustomer_shouldThrowIllegalArgumentException_whenRepositoryReturnsNull() {
            CreateCustomerRequest request = new CreateCustomerRequest("Oleg", "Sidorov", "sidorov@gmail.com", "+48111222333");

            when(customerRepository.getAll()).thenReturn(createList());
            when(customerRepository.addCustomer(any(Customer.class))).thenReturn(null);

            assertThrows(IllegalArgumentException.class, () -> customerService.addCustomer(request));

            verify(customerRepository).getAll();
            verify(customerRepository).addCustomer(any(Customer.class));
        }

        @Test
        void updateCustomer_shouldReturnUpdatedCustomerResponse() {
            Customer existingCustomer = createCustomer();
            UpdateCustomerRequest request = new UpdateCustomerRequest("Ivan", "Updated", "ivanov@gmail.com", "+48225476856");
            Customer updatedCustomer = new Customer(1L, "Ivan", "Updated", "ivanov@gmail.com", "+48225476856", true);

            when(customerRepository.getById(1L)).thenReturn(Optional.of(existingCustomer));
            when(customerRepository.getAll()).thenReturn(createList());
            when(customerRepository.updateCustomer(eq(1L), any(Customer.class))).thenReturn(Optional.of(updatedCustomer));

            CustomerResponse response = customerService.updateCustomer(1L, request);

            assertNotNull(response);
            assertEquals("Updated", response.getSurname());
            assertEquals("ivanov@gmail.com", response.getEmail());

            verify(customerRepository).getById(1L);
            verify(customerRepository).getAll();
            verify(customerRepository).updateCustomer(eq(1L), any(Customer.class));
        }

        @Test
        void updateCustomer_shouldThrowIllegalArgumentException_whenRequestIsNull() {
            assertThrows(IllegalArgumentException.class, () -> customerService.updateCustomer(1L, null));

            verifyNoInteractions(customerRepository);
        }

        @Test
        void updateCustomer_shouldThrowIllegalArgumentException_whenIdIsNull() {
            UpdateCustomerRequest request = new UpdateCustomerRequest("Ivan", "Updated", "new@gmail.com", "+48225476856");

            assertThrows(IllegalArgumentException.class, () -> customerService.updateCustomer(null, request));

            verifyNoInteractions(customerRepository);
        }

        @Test
        void updateCustomer_shouldThrowIllegalArgumentException_whenIdIsNegative() {
            UpdateCustomerRequest request = new UpdateCustomerRequest("Ivan", "Updated", "new@gmail.com", "+48225476856");

            assertThrows(IllegalArgumentException.class, () -> customerService.updateCustomer(-1L, request));

            verifyNoInteractions(customerRepository);
        }

        @Test
        void updateCustomer_shouldThrowCustomerNotFoundException_whenCustomerNotFound() {
            UpdateCustomerRequest request = new UpdateCustomerRequest("Ivan", "Updated", "new@gmail.com", "+48225476856");

            when(customerRepository.getById(99L)).thenReturn(Optional.empty());

            assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(99L, request));

            verify(customerRepository).getById(99L);
            verify(customerRepository, never()).updateCustomer(anyLong(), any(Customer.class));
        }

        @Test
        void updateCustomer_shouldThrowCustomerEmailAlreadyExistsException_whenEmailIsTakenByAnotherCustomer() {
            Customer existingCustomer = createCustomer();
            UpdateCustomerRequest request = new UpdateCustomerRequest("Ivan", "Ivanov", "petrov@gmail.com", "+48225476856");

            when(customerRepository.getById(1L)).thenReturn(Optional.of(existingCustomer));
            when(customerRepository.getAll()).thenReturn(createList());

            assertThrows(CustomerEmailAlreadyExistsException.class, () -> customerService.updateCustomer(1L, request));

            verify(customerRepository).getById(1L);
            verify(customerRepository).getAll();
            verify(customerRepository, never()).updateCustomer(anyLong(), any(Customer.class));
        }

        @Test
        void updateCustomer_shouldThrowCustomerNotFoundException_whenRepositoryReturnsEmpty() {
            Customer existingCustomer = createCustomer();
            UpdateCustomerRequest request = new UpdateCustomerRequest("Ivan", "Updated", "new@gmail.com", "+48225476856");

            when(customerRepository.getById(1L)).thenReturn(Optional.of(existingCustomer));
            when(customerRepository.getAll()).thenReturn(createList());
            when(customerRepository.updateCustomer(eq(1L), any(Customer.class))).thenReturn(Optional.empty());

            assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(1L, request));

            verify(customerRepository).updateCustomer(eq(1L), any(Customer.class));
        }

        @Test
        void removeCustomer_shouldDeactivateCustomer() {
            Customer customer = createCustomer();

            when(customerRepository.getById(1L)).thenReturn(Optional.of(customer));

            customerService.removeCustomer(1L);

            ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
            verify(customerRepository).getById(1L);
            verify(customerRepository).updateCustomer(eq(1L), customerCaptor.capture());

            assertFalse(customerCaptor.getValue().isActive());
        }

        @Test
        void removeCustomer_shouldThrowIllegalArgumentException_whenIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> customerService.removeCustomer(null));

            verifyNoInteractions(customerRepository);
        }

        @Test
        void removeCustomer_shouldThrowIllegalArgumentException_whenIdIsLessThanOne() {
            assertThrows(IllegalArgumentException.class, () -> customerService.removeCustomer(0L));

            verifyNoInteractions(customerRepository);
        }

        @Test
        void removeCustomer_shouldThrowCustomerNotFoundException_whenCustomerNotFound() {
            when(customerRepository.getById(99L)).thenReturn(Optional.empty());

            assertThrows(CustomerNotFoundException.class, () -> customerService.removeCustomer(99L));

            verify(customerRepository).getById(99L);
            verify(customerRepository, never()).updateCustomer(anyLong(), any(Customer.class));
        }

    private Customer createCustomer() {
        return new Customer(1L, "Ivan", "Ivanov",
                "ivanov@gmail.com", "+48225476856", true);
    }

    private List<Customer> createList() {
        return List.of(
                new Customer(1L, "Ivan", "Ivanov",
                        "ivanov@gmail.com", "+48225476856", true),
                new Customer(2L, "Alex", "Petrov",
                        "petrov@gmail.com", "+487372884", true ),
                new Customer(3L, "Pavel", "Smirnov",
                        "smirnov@gmail.com", "+4822547883", false)
                );
    }
}
