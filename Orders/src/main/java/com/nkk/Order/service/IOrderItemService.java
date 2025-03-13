package com.nkk.Order.service;

import com.nkk.Order.entity.Order;
import com.nkk.Order.entity.OrderItem;

import java.util.List;

public interface IOrderItemService {

//    OrderItem createOrderItem(Order order, Long productId, Integer quantity, Double price);

    void deleteOrderItem(Long orderItemId);

    List<OrderItem> getOrderItemsByOrderId(Long orderId);
}
