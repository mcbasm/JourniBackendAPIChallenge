package com.journi.challenge.models;

/**
 * Represents a Product the company can sell.
 * Id is of course unique.
 * price is always in Euros.
 */
public class Product {

    private final String id;
    private final String description;
    private final Double price;
    // Added field to get the currencyCode of the product and show it on the response of the API request
    private final String currencyCode;

    // Added new Constructor to support current implementation of the ProductsRepository class (filling the list with products)
    public Product(String id, String description, Double price) {
        this(id,description,price,"EUR");
    }

    public Product(String id, String description, Double price, String currencyCode) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.currencyCode = currencyCode;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }
}
