package org.example.Amazon;

import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AmazonEndToEndTest {

    // Minimal in-memory cart just for tests
    static class FakeShoppingCart implements ShoppingCart {
        private final List<Item> items = new ArrayList<>();
        @Override public void add(Item item) { items.add(item); }
        @Override public List<Item> getItems() { return List.copyOf(items); }
        @Override public int numberOfItems() { return items.size(); }
    }

    private static Item it(ItemType t, String n, int q, double p) {
        return new Item(t, n, q, p);
    }

    @Test
    @DisplayName("Amazon.calculate: regular + delivery + electronics surcharge combined")
    void calculate_withElectronics() {
        var cart = new FakeShoppingCart();
        var rules = List.of(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics());
        var amazon = new Amazon(cart, rules);

        // Items: total regular = 2*15 + 1*40 = 70.00
        amazon.addToCart(it(ItemType.OTHER, "Book", 2, 15.00));
        amazon.addToCart(it(ItemType.ELECTRONIC, "Mouse", 1, 40.00));

        // 2 items -> delivery = 5.00 ; electronics surcharge = 7.50
        // final = 70 + 5 + 7.5 = 82.5
        assertEquals(82.50, amazon.calculate(), 1e-9);
        assertEquals(2, cart.numberOfItems());
    }

    @Test
    @DisplayName("Amazon.calculate: no electronics; delivery tiers still applied")
    void calculate_noElectronics() {
        var cart = new FakeShoppingCart();
        var rules = List.of(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics());
        var amazon = new Amazon(cart, rules);

        // 4 items (for delivery tier): regular = 3*10 + 1*5 = 35.00
        amazon.addToCart(it(ItemType.OTHER, "Notebook", 3, 10.00));
        amazon.addToCart(it(ItemType.OTHER, "Pen", 1, 5.00));

        // cart size = 2 lines, but DELIVERY RULE counts items by cart.size() (==2)
        // If your DeliveryPrice should count lines vs. physical items,
        // the expected number aligns with your implementation (cart.size()).
        // With 2 lines it falls into 1..3 -> 5.00
        double expected = 35.00 + 5.00 + 0.00;
        assertEquals(expected, amazon.calculate(), 1e-9);
    }
}
