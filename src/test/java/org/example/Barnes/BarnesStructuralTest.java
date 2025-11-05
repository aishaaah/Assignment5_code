package org.example.Barnes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BarnesStructuralTest {

    @Test
    @DisplayName("structural-based: empty order → zero total, no unavailable")
    void testEmptyOrderPath() {
        BookDatabase db = isbn -> new Book(isbn, 30, 10);
        BuyBookProcess process = (book, amount) -> {};
        BarnesAndNoble store = new BarnesAndNoble(db, process);

        PurchaseSummary summary = store.getPriceForCart(Collections.emptyMap());
        assertNotNull(summary);
        assertEquals(0, summary.getTotalPrice());
        assertTrue(summary.getUnavailable().isEmpty());
    }

    @Test
    @DisplayName("structural-based: exact quantity → no unavailable branch")
    void testExactQuantityBranch() {
        BookDatabase db = isbn -> new Book(isbn, 40, 3);
        final int[] bought = {0};
        BuyBookProcess process = (book, amount) -> bought[0] += amount;
        BarnesAndNoble store = new BarnesAndNoble(db, process);

        Map<String, Integer> order = Map.of("XYZ", 3);
        PurchaseSummary summary = store.getPriceForCart(order);

        assertEquals(120, summary.getTotalPrice());
        assertTrue(summary.getUnavailable().isEmpty());
        assertEquals(3, bought[0]);
    }

    @Test
    @DisplayName("structural-based: request > available triggers unavailable branch")
    void testUnavailableBranch() {
        BookDatabase db = isbn -> new Book(isbn, 15, 2);
        BuyBookProcess process = (book, amount) -> {};
        BarnesAndNoble store = new BarnesAndNoble(db, process);

        PurchaseSummary summary = store.getPriceForCart(Map.of("Q1", 4));

        assertEquals(30, summary.getTotalPrice()); // only 2 * 15
        Book key = new Book("Q1", 15, 2);
        assertFalse(summary.getUnavailable().isEmpty());
        assertEquals(2, summary.getUnavailable().get(key));
    }
}