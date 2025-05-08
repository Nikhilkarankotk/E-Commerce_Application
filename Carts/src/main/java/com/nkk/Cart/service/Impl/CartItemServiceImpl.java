package com.nkk.Cart.service.Impl;

import com.nkk.Cart.Dto.CartDTO;
import com.nkk.Cart.Dto.CartItemDTO;
import com.nkk.Cart.Dto.ProductDTO;
import com.nkk.Cart.entity.Cart;
import com.nkk.Cart.entity.CartItem;
import com.nkk.Cart.exception.InsufficientStockException;
import com.nkk.Cart.service.ICartItemService;
import com.nkk.Cart.service.client.ProductsFeignClient;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.naming.InsufficientResourcesException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartItemServiceImpl implements ICartItemService {

    private final ProductsFeignClient productsFeignClient;

    public CartItemServiceImpl(ProductsFeignClient productsFeignClient) {
        this.productsFeignClient = productsFeignClient;
    }

    @Transactional
    public void addOrUpdateItem(Cart cart, Long productId, Integer quantity) {
        // Fetch product details from ProductService
        ProductDTO product = productsFeignClient.getProductById(productId);
        // Check product stock availability
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }
        // Add or update item in the cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
        if (existingItem.isPresent()) {
            // Update quantity if item already exists
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // Add new item to the cart
            CartItem newItem = new CartItem();
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }
    }

    public CartDTO toCartDTOWithProductDetails(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        cartDTO.setUserId(cart.getUserId());
        // Calculate the total amount and fetch product details for each cart item
        double totalAmount = 0.0;
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            // Fetch product details from Product Service
            ProductDTO product = productsFeignClient.getProductById(cartItem.getProductId());
            // Calculate the total price for this item
            double totalPrice = product.getPrice() * cartItem.getQuantity();
            totalAmount += totalPrice;
            // Create the CartItemDTO
            CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.setCartItemId(cartItem.getCartItemId());
            cartItemDTO.setProductId(cartItem.getProductId());
            cartItemDTO.setProductName(product.getName());
            cartItemDTO.setProductPrice(product.getPrice());
            cartItemDTO.setQuantity(cartItem.getQuantity());
            cartItemDTO.setTotalPrice(totalPrice);
            cartItemDTOs.add(cartItemDTO);
        }
        cartDTO.setTotalAmount(totalAmount);
        cartDTO.setItems(cartItemDTOs);
        return cartDTO;
    }
}
