package org.example.Amazon;

import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

class AmazonBehaviorMockitoTest {

    @Test
    void addToCart_delegatesToCartAddExactlyOnce() {
        // Given a mocked cart
        ShoppingCart cart = mock(ShoppingCart.class);
        Amazon amazon = new Amazon(cart, List.of());

        Item item = new Item(ItemType.OTHER, "Sticker", 1, 1.0);

        // When
        amazon.addToCart(item);

        // Then: the exact same instance is passed to cart.add once
        verify(cart, times(1)).add(same(item));
        verifyNoMoreInteractions(cart);
    }

    @Test
    void calculate_sumsAllRules_and_usesCartItems() {
        // Given a mocked cart that returns these items
        ShoppingCart cart = mock(ShoppingCart.class);
        List<Item> items = List.of(new Item(ItemType.OTHER, "A", 1, 2.0));
        when(cart.getItems()).thenReturn(items);

        // And two mocked rules that each contribute to the total
        PriceRule r1 = mock(PriceRule.class);
        PriceRule r2 = mock(PriceRule.class);
        when(r1.priceToAggregate(items)).thenReturn(10.0);
        when(r2.priceToAggregate(items)).thenReturn(5.5);

        Amazon amazon = new Amazon(cart, List.of(r1, r2));

        // When
    double total = amazon.calculate();

    // Then
    assertEquals(15.5, total, 1e-9);

        // cart.getItems() is called once per rule (2 rules => 2 calls)
    verify(cart, times(2)).getItems();

    // each rule is applied exactly once to the same items
    verify(r1, times(1)).priceToAggregate(items);
    verify(r2, times(1)).priceToAggregate(items);

    verifyNoMoreInteractions(cart, r1, r2);

}
}
