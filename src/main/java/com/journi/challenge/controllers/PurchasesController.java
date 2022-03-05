package com.journi.challenge.controllers;

import com.journi.challenge.CurrencyConverter;
import com.journi.challenge.models.Purchase;
import com.journi.challenge.models.PurchaseRequest;
import com.journi.challenge.models.PurchaseStats;
import com.journi.challenge.repositories.PurchasesRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class PurchasesController {

    // Instance of Currency Converter Class
    private final CurrencyConverter cc = new CurrencyConverter();

    @Inject
    private PurchasesRepository purchasesRepository;

    @GetMapping("/purchases/statistics")
    public PurchaseStats getStats() {
        return purchasesRepository.getLast30DaysStats();
    }

    @PostMapping("/purchases")
    public Purchase save(@RequestBody PurchaseRequest purchaseRequest) {
        // Get the value in the local currency of the purchase
        Double amountInCurrency = cc.convertCurrencyToEur(purchaseRequest.getCurrencyCode() != null ? purchaseRequest.getCurrencyCode() : "EUR", purchaseRequest.getAmount());

        // Round the amount to two places always
        BigDecimal amountInDecimal = new BigDecimal(amountInCurrency);

        Purchase newPurchase = new Purchase(
                purchaseRequest.getInvoiceNumber(),
                LocalDateTime.parse(purchaseRequest.getDateTime(), DateTimeFormatter.ISO_DATE_TIME),
                purchaseRequest.getProductIds(),
                purchaseRequest.getCustomerName(),
                amountInDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue()
        );
        purchasesRepository.save(newPurchase);
        return newPurchase;
    }
}
