package main.cinemabookingrestapi.entity;

import java.util.Objects;

public class Customer extends Entity {
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private boolean active;

    public Customer(Long id, String name, String surname, String email, String phoneNumber, boolean active) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.active = active;
    }

    public Customer(Long id) {
        super(id);
    }

    public Customer() {
    }

    public Long getId() {
        return id;
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

    public boolean isActive() {
        return active;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return active == customer.active && Objects.equals(id, customer.id) && Objects.equals(name, customer.name) && Objects.equals(surname, customer.surname) && Objects.equals(email, customer.email) && Objects.equals(phoneNumber, customer.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, email, phoneNumber, active);
    }
}
