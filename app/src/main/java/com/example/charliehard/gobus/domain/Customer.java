package com.example.charliehard.gobus.domain;

import java.io.Serializable;

/**
 * Created by Charlie Hard on 1/05/2017.
 */

public class Customer implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String cardNumber;
    private String password;


    public Customer(Long id, String firstName, String lastName, String email, String cardNumber, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cardNumber = cardNumber;
        this.password = password;
    }

    public Customer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
