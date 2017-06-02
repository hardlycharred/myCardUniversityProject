package com.example.charliehard.mycard.dao;

import com.example.charliehard.mycard.domain.Card;
import com.example.charliehard.mycard.domain.Customer;
import com.example.charliehard.mycard.domain.Transaction;

import java.util.ArrayList;

/**
 * Created by Charlie Hard on 23/05/2017.
 */

public class MyCardDAO {

    private static Customer customer;
    private static Card card;
    private static ArrayList<Transaction> transactions;

    public static ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public static void setTransactions(ArrayList<Transaction> transactions) {
        MyCardDAO.transactions = transactions;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public static Card getCard() {
        return card;
    }

    public static void setCard(Card card) {
        MyCardDAO.card = card;
    }
}
