package com.nkk.Cart.mapper;

import com.nkk.Cart.Dto.CartDTO;
import com.nkk.Cart.Dto.CartItemDTO;
import com.nkk.Cart.entity.Cart;
import com.nkk.Cart.entity.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    @Autowired
    private CartItemMapper cartItemMapper;

    public CartDTO mapToCartDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        cartDTO.setUserId(cart.getUserId());

        //  Map cart Items
        List<CartItemDTO> cartItemDTOS =
        cart.getItems().stream()
                .map(cartItemMapper::mapToCartItemDTO)
                .collect(Collectors.toList());
        cartDTO.setItems(cartItemDTOS);
        return cartDTO;
    }

    public Cart mapToCart(CartDTO cartDTO){
        Cart cart = new Cart();
        cart.setUserId(cartDTO.getUserId());
        cart.setCartId(cartDTO.getCartId());

        // Map cart items
        List<CartItem> cartItems =
        cartDTO.getItems().stream()
                .map(cartItemMapper::mapToCartItem)
                .collect(Collectors.toList());
        cart.setItems(cartItems);
        return cart;
    }
}

