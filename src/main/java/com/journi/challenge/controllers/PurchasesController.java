package com.journi.challenge.controllers;

import com.journi.challenge.CurrencyConverter;
import com.journi.challenge.models.Purchase;
import com.journi.challenge.models.PurchaseRequest;
import com.journi.challenge.models.PurchaseStats;
import com.journi.challenge.repositories.ProductsRepository;
import com.journi.challenge.repositories.PurchasesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class PurchasesController {

    // Instance of Currency Converter Class
    private final CurrencyConverter cc = new CurrencyConverter();

    @Inject
    private PurchasesRepository purchasesRepository;

    @Inject
    private ProductsRepository productsRepository;

    @GetMapping("/purchases/statistics")
    public PurchaseStats getStats() {
        return purchasesRepository.getLast30DaysStats();
    }

    @PostMapping("/purchases")
    public Purchase save(@RequestBody PurchaseRequest purchaseRequest) {
        try {
            // Get the value in the local currency of the purchase (if the request doesn't have a currency, it uses EUR by default)
            Double amountInCurrency = cc.convertCurrencyToEur(purchaseRequest.getCurrencyCode() != null ? purchaseRequest.getCurrencyCode() : "EUR", purchaseRequest.getAmount());

            // Create a BigDecimal to round the double
            BigDecimal amountInDecimal = new BigDecimal(amountInCurrency);

            Purchase newPurchase = new Purchase(
                    purchaseRequest.getInvoiceNumber(),
                    LocalDateTime.parse(purchaseRequest.getDateTime(), DateTimeFormatter.ISO_DATE_TIME),
                    purchaseRequest.getProductIds(),
                    purchaseRequest.getCustomerName(),
                    // Round the value always to 2 places
                    amountInDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue()
            );
            // Save the purchase with the new values
            purchasesRepository.save(newPurchase);
            return newPurchase;
        } catch (Exception ex) {
            // The only error is going to happen if we pass an invalid value on the dateTime field to be parsed.
            // Also, the document said that in case of an invalid request, it must send a 401 response, but that's an UNAUTHORIZED exception
            // and that will not reflect the problem, so I thought that it would be better to use a 400 BAD REQUEST instead
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Missing Date");
        }
    }
}
