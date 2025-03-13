package com.nkk.Order.service.Impl;

import com.nkk.Order.dto.CartDTO;
import com.nkk.Order.dto.CartItemDTO;
import com.nkk.Order.dto.OrderDTO;
import com.nkk.Order.entity.Order;
import com.nkk.Order.entity.OrderItem;
import com.nkk.Order.mapper.OrderMapper;
import com.nkk.Order.repository.OrderRepository;
import com.nkk.Order.service.IOrderItemService;
import com.nkk.Order.service.IOrderService;
import com.nkk.Order.service.client.CartsFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public OrderDTO createOrder(Long userId) {
        // Step 1: Fetch the cart for the user
        CartDTO cartDTO = cartsFeignClient.getCartByUserId(userId);
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
//            orderItemService.createOrderItem(
//                    order,
//                    cartItemDTO.getProductId(),
//                    cartItemDTO.getQuantity(),
//                    cartItemDTO.getProductPrice()
//            );
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItemDTO.getProductId());
            orderItem.setQuantity(cartItemDTO.getQuantity());
            orderItem.setProductPrice(cartItemDTO.getProductPrice());
            orderItem.setOrder(order);
            order.getItems().add(orderItem);
        }
        // Save the order
        Order savedOrder = orderRepository.save(order);
//        logger.info("savedOrder from order: {}",savedOrder);
        // Step 6: Clear the cart (optional)
        // Step 7: Return the order DTO
        return orderMapper.mapToOrderDTO(savedOrder);
    }
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        return orderMapper.mapToOrderDTO(order);
    }
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(orderMapper::mapToOrderDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        order.setOrderStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.mapToOrderDTO(updatedOrder);
    }
}
