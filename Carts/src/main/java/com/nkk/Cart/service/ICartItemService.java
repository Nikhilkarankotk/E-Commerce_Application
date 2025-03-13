package com.nkk.Cart.service;

import com.nkk.Cart.Dto.CartDTO;
import com.nkk.Cart.entity.Cart;

public interface ICartItemService {

    public void addOrUpdateItem(Cart cart, Long productId, Integer quantity);
    public  CartDTO toCartDTOWithProductDetails(Cart cart);

}
