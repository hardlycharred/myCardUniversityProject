package com.example.charliehard.gobus.dao;

import com.example.charliehard.gobus.domain.Card;
import com.example.charliehard.gobus.domain.Customer;
import com.example.charliehard.gobus.domain.Transaction;

import java.util.ArrayList;

/**
 * Created by Charlie Hard on 23/05/2017.
 */

public class GoBusDAO {

    private static Customer customer;

    public static ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public static void setTransactions(ArrayList<Transaction> transactions) {
        GoBusDAO.transactions = transactions;
    }

    private static Card card;
    private static ArrayList<Transaction> transactions;

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
        GoBusDAO.card = card;
    }
}
