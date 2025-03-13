package com.nkk.Cart.service;

import com.nkk.Cart.Dto.CartDTO;
import com.nkk.Cart.entity.Cart;

public interface ICartService {

    /**
     *
     * @param userId
     * @param productId
     * @param quantity
     * @return
     */
    public CartDTO addToCart(Long userId, Long productId, Integer quantity);

    /**
     *
     * @param userId
     * @return
     */
    public CartDTO getCartByUserId(Long userId);

    /**
     *
     * @param userId
     */
    public void clearCart(Long userId);
}
