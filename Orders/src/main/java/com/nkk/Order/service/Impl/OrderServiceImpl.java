package com.nkk.Order.service.Impl;

import com.nkk.Order.dto.*;
import com.nkk.Order.entity.Order;
import com.nkk.Order.entity.OrderItem;
import com.nkk.Order.exception.ResourceNotFoundException;
import com.nkk.Order.mapper.OrderMapper;
import com.nkk.Order.repository.OrderRepository;
import com.nkk.Order.service.IOrderItemService;
import com.nkk.Order.service.IOrderService;
import com.nkk.Order.service.client.CartsFeignClient;
//import com.nkk.Order.service.client.MessagingFeignClient;
import com.nkk.Order.service.client.UsersFeignClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.aspectj.weaver.ast.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements IOrderService {

//    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartsFeignClient cartsFeignClient;
    @Autowired
    private IOrderItemService orderItemService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UsersFeignClient usersFeignClient;
//    @Autowired
//    private StreamBridge streamBridge;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static final String SECRET_KEY = "MySuperSecretKeyThatShouldBeVeryLong";


    public static Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    @Transactional
        public OrderDTO createOrder(String token) {
        // Extract the user ID from the token
        String email = extractEmailFromToken(token);
        Long userId = usersFeignClient.getUserIdByEmail(email).getBody();
        // Step 1: Fetch the cart for the user
        CartDTO cartDTO = cartsFeignClient.getCartByUserId(token);
//       logger.info("CartDto data info: {}",cartDTO);
        // Step 2: Create an order from the cart
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(cartDTO.getTotalAmount());
        order.setOrderStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        // Step 3: Map cart items to order items
//        logger.info("Order details: {}",order);
        for (CartItemDTO cartItemDTO : cartDTO.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItemDTO.getProductId());
            orderItem.setQuantity(cartItemDTO.getQuantity());
            orderItem.setProductPrice(cartItemDTO.getProductPrice());
            orderItem.setOrder(order);
            order.getItems().add(orderItem);
        }
        // Save the order
        Order savedOrder = orderRepository.save(order);
//        // Publish "Order Created" event
//        OrderEvent orderEvent = new OrderEvent();
//        orderEvent.setOrderId(savedOrder.getOrderId());
//        orderEvent.setTotalAmount(savedOrder.getTotalAmount());
//        streamBridge.send("orderCreated-out-0", orderEvent);
        // Step 7: Return the order DTO
        return orderMapper.mapToOrderDTO(savedOrder);
    }
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return orderMapper.mapToOrderDTO(order);
    }
    public List<OrderDTO> getOrdersByUserId(String token) {
        String email = extractEmailFromToken(token);
        Long userId = usersFeignClient.getUserIdByEmail(email).getBody();
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(orderMapper::mapToOrderDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        order.setOrderStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.mapToOrderDTO(updatedOrder);
    }

    @Transactional
    public OrderDTO confirmPayment(Long orderId, PaymentConfirmationDTO paymentConfirmationDTO) {
        // Fetch the order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        // Update the order status based on payment confirmation
        if ("SUCCESS".equals(paymentConfirmationDTO.getPaymentStatus())) {
            order.setOrderStatus("PAYMENT_SUCCESS");
        } else {
            order.setOrderStatus("PAYMENT_FAILED");
        }
        // Save the updated order
        Order savedOrder = orderRepository.save(order);
        // Map to DTO and return
        return orderMapper.mapToOrderDTO(savedOrder);
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
