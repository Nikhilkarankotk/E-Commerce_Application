package com.nkk.Cart.service;

import com.nkk.Cart.Dto.CartDTO;
import com.nkk.Cart.entity.Cart;

public interface ICartService {

    /**
     *
     * @param token
     * @param productId
     * @param quantity
     * @return
     */
    public CartDTO addToCart(String token, Long productId, Integer quantity);

    /**
     *
     * @param token
     * @return
     */
    public CartDTO getCartByUserId(String token);

    /**
     *
     * @param token
     */
    public void clearCart(String token);
}
