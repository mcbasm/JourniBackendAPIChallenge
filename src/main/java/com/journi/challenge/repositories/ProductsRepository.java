package com.journi.challenge.repositories;

import com.journi.challenge.CurrencyConverter;
import com.journi.challenge.models.Product;

import javax.inject.Named;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Named
@Singleton
public class ProductsRepository {

    // Instance of Currency Converter Class
    private final CurrencyConverter cc = new CurrencyConverter();

    private List<Product> allProducts = new ArrayList<>();

    {
        allProducts.add(new Product("photobook-square-soft-cover", "Photobook Square with Soft Cover", 25.0));
        allProducts.add(new Product("photobook-square-hard-cover", "Photobook Square with Hard Cover", 30.0));
        allProducts.add(new Product("photobook-landscape-soft-cover", "Photobook Landscape with Soft Cover", 35.0));
        allProducts.add(new Product("photobook-landscape-hard-cover", "Photobook Landscape with Hard Cover", 45.0));
    }

    public List<Product> list() {
        return allProducts;
    }

    // Calculate the prices on the local currency set by the countryCode field
    public List<Product> listWithLocalPrices(String countryCode) {
        List<Product> productsWithPrices = new ArrayList<>();

        // Because all the products values are final, we create a new list to be filled with the prices per country
        allProducts.stream().map(p -> (Product) p).forEach(p -> {
            // Get the value in the local currency of the purchase
            Double amountInCurrency = cc.convertEurToCurrency(cc.getCurrencyForCountryCode(countryCode), p.getPrice());

            // Create a BigDecimal to round the double
            BigDecimal amountInDecimal = new BigDecimal(amountInCurrency);
            Product newProduct = new Product(p.getId(), p.getDescription(),
                    // Round the value always to 2 decimals
                    amountInDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue(),
                    // Get the CurrencyCode
                    cc.getCurrencyForCountryCode(countryCode));
            productsWithPrices.add(newProduct);
        });

        return productsWithPrices;
    }
}
