package org.example.Barnes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SPECIFICATION-BASED TESTS
 * These test what the system should do, according to its requirements.
 */
public class BarnesSpecificationTest {

    @Test
    @DisplayName("specification-based: null order should return null summary")
    void testNullOrderReturnsNull() {
        BookDatabase db = isbn -> new Book("1", 10, 5); // dummy implementation
        BuyBookProcess process = (book, amount) -> {};
        BarnesAndNoble store = new BarnesAndNoble(db, process);

        assertNull(store.getPriceForCart(null));
    }

    @Test
    @DisplayName("specification-based: valid order returns correct total price")
    void testValidOrderReturnsCorrectTotal() {
        BookDatabase db = isbn -> new Book(isbn, 25, 10);
        final int[] bought = {0};
        BuyBookProcess process = (book, amount) -> bought[0] += amount;
        BarnesAndNoble store = new BarnesAndNoble(db, process);

        Map<String, Integer> order = new HashMap<>();
        order.put("12345", 2); // 2 books * $25

        PurchaseSummary summary = store.getPriceForCart(order);

        assertNotNull(summary);
        assertEquals(50, summary.getTotalPrice());
        assertEquals(2, bought[0]);
    }

    @Test
    @DisplayName("specification-based: if order exceeds available quantity, unavailable list updated")
    void testUnavailableQuantityAdded() {
        BookDatabase db = isbn -> new Book(isbn, 20, 1); // only 1 book in stock
        BuyBookProcess process = (book, amount) -> {};
        BarnesAndNoble store = new BarnesAndNoble(db, process);

        Map<String, Integer> order = new HashMap<>();
        order.put("ABC", 5); // order 5 but only 1 available

        PurchaseSummary summary = store.getPriceForCart(order);

        assertEquals(20, summary.getTotalPrice()); // only 1 * $20 charged
        assertTrue(summary.getUnavailable().containsKey(new Book("ABC", 20, 1)));
        assertEquals(4, summary.getUnavailable().get(new Book("ABC", 20, 1)));
    }
}
