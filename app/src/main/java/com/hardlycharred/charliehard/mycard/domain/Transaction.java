package com.example.charliehard.mycard.domain;

import java.io.Serializable;

/**
 * Created by Charlie Hard on 1/05/2017.
 */

public class Transaction implements Serializable {

    private long id;
    private String cardNumber;
    private String date;
    private String time;
    private Double amount;

    public Transaction(Long id, String cardNumber, String date, String time, Double amount) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.date = date;
        this.time = time;
        this.amount = amount;
    }

    public Transaction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", cardNumber='" + cardNumber + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", amount=" + amount +
                '}';
    }
}
