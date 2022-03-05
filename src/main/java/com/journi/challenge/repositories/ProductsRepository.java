package com.journi.challenge.repositories;

import com.journi.challenge.CurrencyConverter;
import com.journi.challenge.models.Product;
import org.springframework.stereotype.Component;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Named
@Singleton
public class ProductsRepository {

    // Instance of Currency Converter Class
    private CurrencyConverter cc=new CurrencyConverter();

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
        List<Product> productsWithPrices=new ArrayList<>();

        allProducts.stream().map(p->(Product)p).forEach(p->{
            Product newProduct=new Product(p.getId(),p.getDescription(),cc.convertEurToCurrency(countryCode,p.getPrice()));
            productsWithPrices.add(newProduct);
        });

        return productsWithPrices;
    }
}
