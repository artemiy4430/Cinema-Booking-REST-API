package main.cinemabookingrestapi.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import main.cinemabookingrestapi.entity.Customer;

import java.util.Objects;

public class CustomerResponse {

    @Size(min = 2, max = 50, message = "имя должно быть от 2 до 50 символов")
    @NotBlank
    private String name;

    @Size(min = 2, max = 50, message = "фимилия должно быть от 2 до 50 символов")
    @NotBlank
    private String surname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\+?\\d{1,15}$", message = "inappropriate format")
    private String phoneNumber;

    public CustomerResponse(String name, String surname, String email, String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public CustomerResponse() {
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public static CustomerResponse formEntity(Customer customer) {
        CustomerResponse response = new CustomerResponse();

        response.setName(customer.getName());
        response.setSurname(customer.getSurname());
        response.setEmail(customer.getEmail());
        response.setPhoneNumber(customer.getPhoneNumber());

        return response;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CustomerResponse response = (CustomerResponse) o;
        return Objects.equals(name, response.name) && Objects.equals(surname, response.surname) && Objects.equals(email, response.email) && Objects.equals(phoneNumber, response.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, email, phoneNumber);
    }
}
