package com.journi.challenge.repositories;

import com.journi.challenge.models.Purchase;
import com.journi.challenge.models.PurchaseStats;
import javax.inject.Named;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Named
@Singleton
public class PurchasesRepository {

    private final List<Purchase> allPurchases = new ArrayList<>();

    public List<Purchase> list() {
        return allPurchases;
    }

    public void save(Purchase purchase) {
        allPurchases.add(purchase);
    }

    public PurchaseStats getLast30DaysStats() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE.withZone(ZoneId.of("UTC"));

        LocalDateTime start = LocalDate.now().atStartOfDay().minusDays(30);

        List<Purchase> recentPurchases = allPurchases
                .stream()
                .filter(p -> p.getTimestamp().isAfter(start))
                .sorted(Comparator.comparing(Purchase::getTimestamp))
                .collect(Collectors.toList());

        return new PurchaseStats(
                recentPurchases.get(0).getTimestamp().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDate().toString(),
                recentPurchases.get(recentPurchases.size() - 1).getTimestamp().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDate().toString(),
                (long) recentPurchases.size(),
                recentPurchases.stream().mapToDouble(Purchase::getTotalValue).sum(),
                recentPurchases.stream().mapToDouble(Purchase::getTotalValue).average().orElse(0.0),
                recentPurchases.stream().mapToDouble(Purchase::getTotalValue).min().orElse(0.0),
                // It returned min instead of max value
                recentPurchases.stream().mapToDouble(Purchase::getTotalValue).max().orElse(0.0)
        );
    }
}
