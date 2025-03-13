package com.nkk.Order.service.Impl;

import com.nkk.Order.dto.CartItemDTO;
import com.nkk.Order.dto.ProductDTO;
import com.nkk.Order.entity.Order;
import com.nkk.Order.entity.OrderItem;
import com.nkk.Order.repository.OrderItemRepository;
import com.nkk.Order.service.IOrderItemService;
import com.nkk.Order.service.client.ProductsFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderItemServiceImpl implements IOrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ProductsFeignClient productsFeignClient;

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, ProductsFeignClient productsFeignClient) {
        this.orderItemRepository = orderItemRepository;
        this.productsFeignClient = productsFeignClient;
    }

//    @Transactional
//    public OrderItem createOrderItem(Order order, Long productId, Integer quantity, Double price) {
//        CartItemDTO cartItemDTO= new CartItemDTO();
//        // Create a new OrderItem
//        OrderItem orderItem = new OrderItem();
//        orderItem.setProductId(productId);
//        orderItem.setQuantity(quantity);
//        orderItem.setProductPrice(price);
//        orderItem.setOrder(order);
//        order.getItems().add(orderItem);
//        // Save the OrderItem
//        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
//        // Update product stock (optional)
//        ProductDTO productDTO = new ProductDTO();
//        productDTO.setProductId(productId);
//        productDTO.setStockQuantity(-quantity); // Reduce stock quantity
//        productsFeignClient.updateProductStock(productDTO);
//        return savedOrderItem;
//    }
    @Transactional
    public void deleteOrderItem(Long orderItemId) {
        orderItemRepository.deleteById(orderItemId);
    }
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrder_OrderId(orderId);
    }
}
