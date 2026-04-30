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
import com.nkk.Order.service.client.UsersFeignClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements IOrderService {

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
    @Autowired
    private StreamBridge streamBridge;  // for publishing events

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
        // Step 2: Create an order from the cart
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(cartDTO.getTotalAmount());
        order.setOrderStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        // Step 3: Map cart items to order items
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
        logger.info("Order saved with ID: {}", savedOrder.getOrderId());

        // Publish OrderCreated event
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(savedOrder.getOrderId());
        event.setUserId(savedOrder.getUserId());
        event.setTotalAmount(savedOrder.getTotalAmount());
        event.setOrderStatus(savedOrder.getOrderStatus());
        event.setCreatedAt(savedOrder.getCreatedAt());

        List<OrderItemEvent> itemEvents = savedOrder.getItems().stream()
                .map(item -> {
                    OrderItemEvent itemEvent = new OrderItemEvent();
                    itemEvent.setProductId(item.getProductId());
                    itemEvent.setQuantity(item.getQuantity());
                    itemEvent.setProductPrice(item.getProductPrice());
                    return itemEvent;
                })
                .collect(Collectors.toList());
        event.setItems(itemEvents);

        // Send to the functional bean's output binding
        boolean sent = streamBridge.send("orderCreatedSupplier-out-0", event);
        if (sent) {
            logger.info("OrderCreated event published for order ID: {}", savedOrder.getOrderId());
        } else {
            logger.error("Failed to publish OrderCreated event for order ID: {}", savedOrder.getOrderId());
        }

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

    public String orderStatusById(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        return order.get().getOrderStatus();
    }
}