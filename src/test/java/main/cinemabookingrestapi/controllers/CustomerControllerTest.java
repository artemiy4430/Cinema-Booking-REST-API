package main.cinemabookingrestapi.controllers;

import main.cinemabookingrestapi.controller.CustomerController;
import main.cinemabookingrestapi.dto.customer.CreateCustomerRequest;
import main.cinemabookingrestapi.dto.customer.CustomerResponse;
import main.cinemabookingrestapi.dto.customer.UpdateCustomerRequest;
import main.cinemabookingrestapi.entity.Customer;
import main.cinemabookingrestapi.exception.alreadyExist.CustomerEmailAlreadyExistsException;
import main.cinemabookingrestapi.exception.notFound.CustomerNotFoundException;
import main.cinemabookingrestapi.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;


    @Test
    void getAll_shouldReturnAllCustomers() {
        when(customerService.getAll()).thenReturn(createListCustomers());

        List<Customer> result = customerController.getAll();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(customerService).getAll();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenServiceReturnsEmptyList() {
        when(customerService.getAll()).thenReturn(Collections.emptyList());

        List<Customer> result = customerController.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(customerService).getAll();
    }

    @Test
    void getById_shouldReturnOk_whenCustomerExists() {
        Customer customer = createCustomer();

        when(customerService.getById(customer.getId())).thenReturn(Optional.of(customer));

        ResponseEntity<Customer> response = customerController.getById(customer.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(customer);

        verify(customerService).getById(customer.getId());
    }

    @Test
    void getById_shouldReturnStatusNotFound_whenCustomerDoesNotExist() {
        Long incorrectId = 99L;

        when(customerService.getById(incorrectId)).thenReturn(Optional.empty());

        ResponseEntity<Customer> response = customerController.getById(incorrectId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(customerService).getById(incorrectId);
    }

    @Test
    void getById_shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        Long incorrectId = 99L;

        when(customerService.getById(incorrectId)).thenThrow(new CustomerNotFoundException(incorrectId));

        assertThrows(CustomerNotFoundException.class, () -> customerController.getById(incorrectId));

        verify(customerService).getById(incorrectId);
    }

    @Test
    void getById_shouldThrowIllegalArgumentException_whenIdIsIncorrect() {
        Long incorrectId = -1L;

        when(customerService.getById(incorrectId)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> customerController.getById(incorrectId));

        verify(customerService).getById(incorrectId);
    }

    @Test
    void getByEmail_shouldReturnOk_whenCustomerExists() {
        Customer customer = createCustomer();

        when(customerService.getByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        ResponseEntity<Customer> response = customerController.getById(customer.getEmail());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(customer);

        verify(customerService).getByEmail(customer.getEmail());
    }

    @Test
    void getByEmail_shouldReturnStatusNotFound_whenCustomerDoesNotExist() {
        String unknownEmail = "unknown@gmail.com";

        when(customerService.getByEmail(unknownEmail)).thenReturn(Optional.empty());

        ResponseEntity<Customer> response = customerController.getById(unknownEmail);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(customerService).getByEmail(unknownEmail);
    }

    @Test
    void getByEmail_shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        String unknownEmail = "unknown@gmail.com";

        when(customerService.getByEmail(unknownEmail)).thenThrow(new CustomerNotFoundException(unknownEmail));

        assertThrows(CustomerNotFoundException.class, () -> customerController.getById(unknownEmail));

        verify(customerService).getByEmail(unknownEmail);
    }

    @Test
    void getByEmail_shouldThrowIllegalArgumentException_whenEmailIsNull() {
        when(customerService.getByEmail(null)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> customerController.getById((String) null));

        verify(customerService).getByEmail(null);
    }

    @Test
    void addCustomer_shouldReturnStatusCreated_whenRequestIsValid() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Ivan", "Ivanov", "ivanov@gmail.com", "+48225476856"
        );
        CustomerResponse expectedResponse = new CustomerResponse(
                "Ivan", "Ivanov", "ivanov@gmail.com", "+48225476856"
        );

        when(customerService.addCustomer(request)).thenReturn(expectedResponse);

        ResponseEntity<CustomerResponse> response = customerController.addCustomer(request);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        verify(customerService).addCustomer(request);
    }

    @Test
    void addCustomer_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        when(customerService.addCustomer(null)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> customerController.addCustomer(null));

        verify(customerService).addCustomer(null);
    }

    @Test
    void addCustomer_shouldThrowCustomerEmailAlreadyExistsException_whenEmailExists() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Ivan", "Ivanov", "ivanov@gmail.com", "+48225476856"
        );

        when(customerService.addCustomer(request))
                .thenThrow(new CustomerEmailAlreadyExistsException(request.getEmail()));

        assertThrows(CustomerEmailAlreadyExistsException.class, () -> customerController.addCustomer(request));

        verify(customerService).addCustomer(request);
    }

    @Test
    void updateCustomer_shouldReturnStatusAcceptedAndCustomerResponse_whenRequestIsValid() {
        Long customerId = 1L;
        UpdateCustomerRequest request = new UpdateCustomerRequest(
                "Ivan", "Updated", "ivanov@gmail.com", "+48225476856"
        );
        CustomerResponse expectedResponse = new CustomerResponse(
                "Ivan", "Updated", "ivanov@gmail.com", "+48225476856"
        );

        when(customerService.updateCustomer(customerId, request)).thenReturn(expectedResponse);

        ResponseEntity<CustomerResponse> response = customerController.updateCustomer(customerId, request);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isSameAs(expectedResponse);

        verify(customerService).updateCustomer(customerId, request);
    }

    @Test
    void updateCustomer_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        Long customerId = 1L;

        when(customerService.updateCustomer(customerId, null)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> customerController.updateCustomer(customerId, null));

        verify(customerService).updateCustomer(customerId, null);
    }

    @Test
    void updateCustomer_shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        Long incorrectId = 99L;
        UpdateCustomerRequest request = new UpdateCustomerRequest(
                "Ivan", "Updated", "ivanov@gmail.com", "+48225476856"
        );

        when(customerService.updateCustomer(incorrectId, request))
                .thenThrow(new CustomerNotFoundException(incorrectId));

        assertThrows(CustomerNotFoundException.class,
                () -> customerController.updateCustomer(incorrectId, request));

        verify(customerService).updateCustomer(incorrectId, request);
    }

    @Test
    void updateCustomer_shouldThrowCustomerEmailAlreadyExistsException_whenEmailIsTaken() {
        Long customerId = 1L;
        UpdateCustomerRequest request = new UpdateCustomerRequest(
                "Ivan", "Ivanov", "petrov@gmail.com", "+48225476856"
        );

        when(customerService.updateCustomer(customerId, request))
                .thenThrow(new CustomerEmailAlreadyExistsException(request.getEmail()));

        assertThrows(CustomerEmailAlreadyExistsException.class,
                () -> customerController.updateCustomer(customerId, request));

        verify(customerService).updateCustomer(customerId, request);
    }

    @Test
    void removeCustomer_shouldReturnNoContent_whenCustomerExists() {
        Long customerId = 1L;

        ResponseEntity<Void> response = customerController.removeCustomer(customerId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(customerService).removeCustomer(customerId);
    }

    @Test
    void removeCustomer_shouldThrowIllegalArgumentException_whenIdIsIncorrect() {
        Long incorrectId = -1L;

        doThrow(new IllegalArgumentException()).when(customerService).removeCustomer(incorrectId);

        assertThrows(IllegalArgumentException.class, () -> customerController.removeCustomer(incorrectId));

        verify(customerService).removeCustomer(incorrectId);
    }

    @Test
    void removeCustomer_shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        Long incorrectId = 99L;

        doThrow(new CustomerNotFoundException(incorrectId)).when(customerService).removeCustomer(incorrectId);

        assertThrows(CustomerNotFoundException.class, () -> customerController.removeCustomer(incorrectId));

        verify(customerService).removeCustomer(incorrectId);
    }


    private Customer createCustomer() {
        return new Customer(1L, "Ivan", "Ivanov",
                "ivanov@gmail.com", "+48225476856", true);
    }

    private List<Customer> createListCustomers() {
        return List.of(
                new Customer(1L, "Ivan", "Ivanov",
                        "ivanov@gmail.com", "+48225476856", true),
                new Customer(2L, "Alex", "Petrov",
                        "petrov@gmail.com", "+487372884", true),
                new Customer(3L, "Pavel", "Smirnov",
                        "smirnov@gmail.com", "+4822547883", false)
        );
    }
}