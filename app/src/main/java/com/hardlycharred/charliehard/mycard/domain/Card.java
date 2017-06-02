package com.example.charliehard.mycard.domain;

import java.io.Serializable;

/**
 * Created by Charlie Hard on 1/05/2017.
 */

public class Card implements Serializable {

    private String cardNumber;
    private Double balance;

    public Card(String cardNumber, Double balance) {
        this.cardNumber = cardNumber;
        this.balance = balance;
    }

    public Card() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardNumber='" + cardNumber + '\'' +
                ", balance=" + balance +
                '}';
    }
}
