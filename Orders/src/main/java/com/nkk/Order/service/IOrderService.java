package com.nkk.Order.service;

import com.nkk.Order.dto.OrderDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IOrderService {

    /**
     *
     * @param userId
     * @return
     */
    OrderDTO createOrder(Long userId);

    /**
     *
     * @param orderId
     * @return
     */
    OrderDTO getOrderById(Long orderId);

    /**
     *
     * @param userId
     * @return
     */
    List<OrderDTO> getOrdersByUserId(Long userId);

    /**
     *
     * @param orderId
     * @param status
     * @return
     */
    OrderDTO updateOrderStatus(Long orderId, String status);


}

