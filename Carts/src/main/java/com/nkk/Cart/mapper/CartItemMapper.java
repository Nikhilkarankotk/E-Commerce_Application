package com.nkk.Cart.mapper;

import com.nkk.Cart.Dto.CartItemDTO;
import com.nkk.Cart.entity.CartItem;
import org.springframework.stereotype.Component;

@Component
public class CartItemMapper {

    public CartItemDTO mapToCartItemDTO(CartItem cartItem){
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setCartItemId(cartItem.getCartItemId());
        cartItemDTO.setProductId(cartItemDTO.getProductId());
        cartItemDTO.setQuantity(cartItemDTO.getQuantity());
        return cartItemDTO;
    }

    public CartItem mapToCartItem(CartItemDTO cartItemDTO){
        CartItem cartItem= new CartItem();
        cartItem.setCartItemId(cartItemDTO.getCartItemId());
        cartItem.setProductId(cartItemDTO.getProductId());
        cartItem.setQuantity(cartItem.getQuantity());
        return cartItem;
    }
}
