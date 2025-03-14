package com.nkk.Shippings.service.Impl;

import com.nkk.Shippings.dto.OrderDTO;
import com.nkk.Shippings.dto.ShippingDTO;
import com.nkk.Shippings.entity.Shipping;
import com.nkk.Shippings.mapper.ShippingMapper;
import com.nkk.Shippings.repository.ShippingRepository;
import com.nkk.Shippings.service.IShippingService;
import com.nkk.Shippings.service.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingRepository shippingRepository;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private ShippingMapper shippingMapper;
    @Transactional
    public ShippingDTO createShipping(Long orderId, String shippingAddress) {
        // Fetch order details
        OrderDTO orderDTO = orderFeignClient.getOrderById(orderId);
        // Create a new shipping entry
        Shipping shipping = new Shipping();
        shipping.setShippingId(shipping.getShippingId());
        shipping.setOrderId(orderId);
        shipping.setShippingAddress(shippingAddress);
        shipping.setStatus("PROCESSING");
        shipping.setCreatedAt(LocalDateTime.now());
        shipping.setUpdatedAt(LocalDateTime.now());
        // Save the shipping entry
        Shipping savedShipping = shippingRepository.save(shipping);
        // Map to DTO and return
        return shippingMapper.mapToShippingDTO(savedShipping);
    }
    public ShippingDTO getShippingByOrderId(Long orderId) {
        Shipping shipping = shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Shipping not found for order: " + orderId));
        return shippingMapper.mapToShippingDTO(shipping);
    }
    @Transactional
    public ShippingDTO updateShippingStatus(Long shippingId, String status) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new RuntimeException("Shipping not found with id: " + shippingId));
        shipping.setStatus(status);
        shipping.setUpdatedAt(LocalDateTime.now());
        Shipping updatedShipping = shippingRepository.save(shipping);
        return shippingMapper.mapToShippingDTO(updatedShipping);
    }
}
