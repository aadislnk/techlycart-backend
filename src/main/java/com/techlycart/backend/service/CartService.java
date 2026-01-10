package com.techlycart.backend.service;
import com.techlycart.backend.dto.CartItemResponse;
import com.techlycart.backend.dto.CartResponse;
import java.util.List;
import java.util.stream.Collectors;
import com.techlycart.backend.entity.Cart;
import com.techlycart.backend.entity.CartItem;
import com.techlycart.backend.entity.Product;
import com.techlycart.backend.entity.User;
import com.techlycart.backend.repository.CartItemRepository;
import com.techlycart.backend.repository.CartRepository;
import com.techlycart.backend.repository.ProductRepository;
import com.techlycart.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * Add product to user's cart.
     * If cart does not exist, create it.
     * If product already exists in cart, increase quantity.
     */
    public void addProductToCart(String username, Long productId) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(1);

            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
    }

    /**
     * Get user's cart.
     * Cart may not exist yet.
     */
    public Cart getCart(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepository.findByUser(user).orElse(null);
    }

    /**
     * Update quantity of a product in cart.
     * If quantity <= 0, item is removed.
     */
    public void updateQuantity(String username, Long productId, int quantity) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        if (quantity <= 0) {
            cart.getItems().remove(cartItem); // orphanRemoval handles DB delete
        } else {
            cartItem.setQuantity(quantity);
        }

        cartRepository.save(cart);
    }

    /**
     * Remove product completely from cart.
     */
    public void removeItem(String username, Long productId) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cart.getItems().remove(cartItem);
        cartRepository.save(cart);
    }

    public CartResponse getCartResponse(String username) {

        Cart cart = getCart(username);

        if (cart == null || cart.getItems().isEmpty()) {
            return new CartResponse(List.of(), 0, 0.0);
        }

        List<CartItemResponse> items = cart.getItems()
                .stream()
                .map(item -> new CartItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        int totalItems = cart.getItems()
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        double totalPrice = cart.getItems()
                .stream()
                .mapToDouble(item ->
                        item.getProduct().getPrice() * item.getQuantity()
                )
                .sum();

        return new CartResponse(items, totalItems, totalPrice);
    }

}
