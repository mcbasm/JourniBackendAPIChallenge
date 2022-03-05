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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class PurchasesController {

    // Instance of Currency Converter Class
    private CurrencyConverter cc = new CurrencyConverter();

    @Inject
    private PurchasesRepository purchasesRepository;

    @GetMapping("/purchases")
    public List<Purchase> getPurchases() {
        return purchasesRepository.list();
    }

    @GetMapping("/purchases/statistics")
    public PurchaseStats getStats() {
        return purchasesRepository.getLast30DaysStats();
    }

    @PostMapping("/purchases")
    public Purchase save(@RequestBody PurchaseRequest purchaseRequest) {

        Purchase newPurchase = new Purchase(
                purchaseRequest.getInvoiceNumber(),
                LocalDateTime.parse(purchaseRequest.getDateTime(), DateTimeFormatter.ISO_DATE_TIME),
                purchaseRequest.getProductIds(),
                purchaseRequest.getCustomerName(),
                cc.convertCurrencyToEur(purchaseRequest.getCurrencyCode() != null ? purchaseRequest.getCurrencyCode() : "EUR", purchaseRequest.getAmount())
        );
        purchasesRepository.save(newPurchase);
        return newPurchase;
    }
}
