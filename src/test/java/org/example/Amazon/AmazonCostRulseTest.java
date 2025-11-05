package org.example.Amazon;

import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AmazonCostRulesTest {

    private static Item it(ItemType t, String n, int q, double p) {
        return new Item(t, n, q, p);
    }

    // ---------- RegularCost ----------
    @Test @DisplayName("RegularCost: sum of (pricePerUnit * quantity) over all items")
    void regularCost_basicSum() {
        var rule = new RegularCost();
        var cart = List.of(
                it(ItemType.OTHER, "Book", 2, 10.00),   // 20.00
                it(ItemType.OTHER, "Pen", 5, 1.50)      // 7.50
        );
        assertEquals(27.50, rule.priceToAggregate(cart), 1e-9);
    }

    @Test @DisplayName("RegularCost: empty cart -> 0")
    void regularCost_empty() {
        var rule = new RegularCost();
        assertEquals(0.0, rule.priceToAggregate(List.of()), 1e-9);
    }

    // ---------- DeliveryPrice ----------
    @Test @DisplayName("DeliveryPrice: 0 items -> 0")
    void delivery_0() {
        var rule = new DeliveryPrice();
        assertEquals(0.0, rule.priceToAggregate(List.of()), 1e-9);
    }

    @Test @DisplayName("DeliveryPrice: 1..3 items -> 5.00")
    void delivery_1_to_3() {
        var rule = new DeliveryPrice();
        assertEquals(5.0, rule.priceToAggregate(List.of(
                it(ItemType.OTHER, "A", 1, 1),
                it(ItemType.OTHER, "B", 1, 1)
        )), 1e-9);

        assertEquals(5.0, rule.priceToAggregate(List.of(
                it(ItemType.OTHER, "A", 1, 1),
                it(ItemType.OTHER, "B", 1, 1),
                it(ItemType.OTHER, "C", 1, 1)
        )), 1e-9);
    }

    @Test @DisplayName("DeliveryPrice: 4..10 items -> 12.50")
    void delivery_4_to_10() {
        var rule = new DeliveryPrice();
        assertEquals(12.5, rule.priceToAggregate(List.of(
                it(ItemType.OTHER, "A", 1, 1),
                it(ItemType.OTHER, "B", 1, 1),
                it(ItemType.OTHER, "C", 1, 1),
                it(ItemType.OTHER, "D", 1, 1)
        )), 1e-9);
    }

    @Test @DisplayName("DeliveryPrice: >10 items -> 20.00")
    void delivery_gt_10() {
        var rule = new DeliveryPrice();
        var cart = java.util.stream.Stream
                .generate(() -> it(ItemType.OTHER, "X", 1, 1))
                .limit(11).toList();
        assertEquals(20.0, rule.priceToAggregate(cart), 1e-9);
    }

    // ---------- ExtraCostForElectronics ----------
    @Test @DisplayName("ExtraCostForElectronics: none -> 0")
    void electronics_none() {
        var rule = new ExtraCostForElectronics();
        var cart = List.of(it(ItemType.OTHER, "Mug", 1, 9.00));
        assertEquals(0.0, rule.priceToAggregate(cart), 1e-9);
    }

    @Test @DisplayName("ExtraCostForElectronics: any ELECTRONIC item -> 7.50 flat")
    void electronics_present() {
        var rule = new ExtraCostForElectronics();
        var cart = List.of(
                it(ItemType.OTHER, "Book", 1, 10.00),
                it(ItemType.ELECTRONIC, "Headphones", 1, 50.00)
        );
        assertEquals(7.50, rule.priceToAggregate(cart), 1e-9);
    }
}
