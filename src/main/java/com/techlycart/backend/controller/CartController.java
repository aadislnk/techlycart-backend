package com.techlycart.backend.controller;

import com.techlycart.backend.dto.CartResponse;
import com.techlycart.backend.entity.Cart;
import com.techlycart.backend.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Add a product to cart.
     * If product already exists, quantity increases.
     */
    @PostMapping("/add/{productId}")
    public void addToCart(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        cartService.addProductToCart(username, productId);
    }

    /**
     * View current user's cart.
     */
    @GetMapping
    public CartResponse viewCart(Authentication authentication) {
        String username = authentication.getName();
        return cartService.getCartResponse(username);
    }


    /**
     * Update quantity of a product in cart.
     */
    @PutMapping("/update/{productId}")
    public void updateQuantity(
            @PathVariable Long productId,
            @RequestParam int quantity,
            Authentication authentication
    ) {
        String username = authentication.getName();
        cartService.updateQuantity(username, productId, quantity);
    }

    /**
     * Remove a product from cart.
     */
    @DeleteMapping("/remove/{productId}")
    public void removeItem(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        cartService.removeItem(username, productId);
    }
}
