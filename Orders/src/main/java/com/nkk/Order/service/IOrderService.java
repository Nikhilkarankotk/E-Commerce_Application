package com.nkk.Order.service;

import com.nkk.Order.dto.OrderDTO;
import com.nkk.Order.dto.PaymentConfirmationDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IOrderService {

    /**
     *
     * @param token
     * @return
     */
        OrderDTO createOrder(String token);

    /**
     *
     * @param orderId
     * @return
     */
    OrderDTO getOrderById(Long orderId);

    /**
     *
     * @param token
     * @return
     */
    List<OrderDTO> getOrdersByUserId(String token);

    /**
     *
     * @param orderId
     * @param status
     * @return
     */
    OrderDTO updateOrderStatus(Long orderId, String status);


    OrderDTO confirmPayment(Long orderId, PaymentConfirmationDTO paymentConfirmationDTO);
}

