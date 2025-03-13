package com.nkk.Order.mapper;

import com.nkk.Order.dto.OrderDTO;
import com.nkk.Order.dto.OrderItemDTO;
import com.nkk.Order.dto.ProductDTO;
import com.nkk.Order.entity.Order;
import com.nkk.Order.entity.OrderItem;
import com.nkk.Order.service.client.ProductsFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductsFeignClient productsFeignClient;

    public OrderDTO mapToOrderDTO(Order order){
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setUserId(order.getUserId());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setOrderStatus(order.getOrderStatus());
        orderDTO.setCreatedAt(order.getCreatedAt());

        // Map OrderItems to OrderItemDTOs
        List<OrderItemDTO> orderItemDTOs = order.getItems().stream()
                .map(orderItem -> {
                    // Fetch product details from Product Service
                    ProductDTO product = productsFeignClient.getProductById(orderItem.getProductId());
                    // Map OrderItem to OrderItemDTO
                    OrderItemDTO orderItemDTO = orderItemMapper.mapToOrderItemDTO(orderItem);
                    orderItemDTO.setProductName(product.getName()); // Set productName
                    orderItemDTO.setTotalPrice(orderItem.getProductPrice() * orderItem.getQuantity()); // Calculate totalPrice
                    return orderItemDTO;
                })
                .collect(Collectors.toList());
        orderDTO.setItems(orderItemDTOs);
        return orderDTO;
    }
    public Order mapToOrder(OrderDTO orderDTO){
        Order order = new Order();
        order.setOrderId(orderDTO.getOrderId());
        order.setUserId(orderDTO.getUserId());
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setOrderStatus(orderDTO.getOrderStatus());
        order.setCreatedAt(orderDTO.getCreatedAt());

        // Map order Items
        List<OrderItem> orderItems =
        orderDTO.getItems().stream()
                .map(orderItemMapper::mapToOrderItem)
                .collect(Collectors.toList());
        order.setItems(orderItems);
        return order;
    }

}
