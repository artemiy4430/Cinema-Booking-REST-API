package main.cinemabookingrestapi.dto.customer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import main.cinemabookingrestapi.entity.Customer;

import java.util.Objects;

public class CreateCustomerRequest {

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

    public CreateCustomerRequest(String name, String surname, String email, String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreateCustomerRequest that = (CreateCustomerRequest) o;
        return Objects.equals(name, that.name) && Objects.equals(surname, that.surname) && Objects.equals(email, that.email) && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, email, phoneNumber);
    }

    public Customer toEntity() {
        Customer customer = new Customer();

        customer.setName(this.name);
        customer.setSurname(this.surname);
        customer.setEmail(this.email);
        customer.setPhoneNumber(this.phoneNumber);

        return customer;
    }
}
