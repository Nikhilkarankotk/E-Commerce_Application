package com.nkk.Cart.service.Impl;

import com.nkk.Cart.Dto.CartDTO;
import com.nkk.Cart.Dto.CartItemDTO;
import com.nkk.Cart.Dto.ProductDTO;
import com.nkk.Cart.entity.Cart;
import com.nkk.Cart.entity.CartItem;
import com.nkk.Cart.exception.InsufficientStockException;
import com.nkk.Cart.exception.ResourceNotFoundException;
import com.nkk.Cart.mapper.CartMapper;
import com.nkk.Cart.repository.CartRepository;
import com.nkk.Cart.service.ICartItemService;
import com.nkk.Cart.service.ICartService;
import com.nkk.Cart.service.client.ProductsFeignClient;
import com.nkk.Cart.service.client.UsersFeignClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements ICartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);


    private final CartRepository cartRepository;
    private final ICartItemService cartItemService;
    private final CartMapper cartMapper;
    private final ProductsFeignClient productsFeignClient;
    private static final String SECRET_KEY = "MySuperSecretKeyThatShouldBeVeryLong";

    public static Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }
   @Autowired
   private UsersFeignClient usersFeignClient;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, ICartItemService cartItemService, CartMapper cartMapper, ProductsFeignClient productsFeignClient) {
        this.cartRepository = cartRepository;
        this.cartItemService = cartItemService;
        this.cartMapper = cartMapper;
        this.productsFeignClient = productsFeignClient;
    }

    @Transactional
    public CartDTO addToCart(String token, Long productId, Integer quantity) {
        String email = extractEmailFromToken(token);
        logger.info("Email: {}", email);
        Long userId = usersFeignClient.getUserIdByEmail(email).getBody();
        Integer stockQuantity = productsFeignClient.getProductByStock(productId);
        if (quantity > stockQuantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + productId);
        }
        // Fetch or create cart for the user
        Cart cart = (Cart) cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return cartRepository.save(newCart);
        });
        // Calculate total quantity of the product
        int totalQuantity = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                        .mapToInt(CartItem::getQuantity)
                                .sum();
        //Validate the total quantity in the cart plus the requested quantity exceeds the stock quantity
        if(totalQuantity + quantity > stockQuantity){
            throw new RuntimeException("Insufficient stock for product: " + productId +"   "+
                    "Available stock: " + stockQuantity+"    "+
                    "Requested quantity: " + (totalQuantity+quantity));
        }
        // Add or update item in the cart
        cartItemService.addOrUpdateItem(cart, productId, quantity);
        // Save the updated cart
        Cart savedCart = cartRepository.save(cart);
        // Return the cart as a DTO
        return cartItemService.toCartDTOWithProductDetails(savedCart);
    }



    public CartDTO getCartByUserId(String token) {
        // Extract the user ID from the token
        String email = extractEmailFromToken(token);
        Long userId = usersFeignClient.getUserIdByEmail(email).getBody();
        // Step 1: Fetch the cart
        Cart cart = (Cart) cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        // Step 2: Map the cart to a DTO
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        cartDTO.setUserId(cart.getUserId());
        // Step 3: Calculate the total amount and fetch product details for each cart item
        double[] totalAmount = {0.0};
        List<CartItemDTO> cartItemDTOs = cart.getItems().stream()
                .map(cartItem -> {
                    // Fetch product details
                    ProductDTO product = productsFeignClient.getProductById(cartItem.getProductId());
                    // Calculate the total price for this item
                    double totalPrice = product.getPrice() * cartItem.getQuantity();
                    totalAmount[0] += totalPrice; // Add to the total cart amount
                    // Create the CartItemDTO
                    CartItemDTO cartItemDTO = new CartItemDTO();
                    cartItemDTO.setCartItemId(cartItem.getCartItemId());
                    cartItemDTO.setProductId(cartItem.getProductId());
                    cartItemDTO.setProductName(product.getName());
                    cartItemDTO.setProductPrice(product.getPrice());
                    cartItemDTO.setQuantity(cartItem.getQuantity());
                    cartItemDTO.setTotalPrice(totalPrice);
                    return cartItemDTO;
                })
                .collect(Collectors.toList());
        // Set the total amount for the cart
        cartDTO.setTotalAmount(totalAmount[0]);
        cartDTO.setItems(cartItemDTOs);
        return cartDTO;
    }
    @Transactional
    public void clearCart(String token) {
        // Extract the user ID from the token
        String email = extractEmailFromToken(token);
        Long userId = usersFeignClient.getUserIdByEmail(email).getBody();
        Cart cart = (Cart) cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        cartRepository.delete(cart);
    }

    private String extractEmailFromToken(String token) {
        String jwtToken = token.substring(7); // Extract token from "Bearer " prefix
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
        return claims.getSubject();
    }
}
