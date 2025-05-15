package com.nkk.Shippings.service.Impl;

import com.nkk.Shippings.dto.OrderDTO;
import com.nkk.Shippings.dto.ShippingDTO;
import com.nkk.Shippings.dto.ShippingStatus;
import com.nkk.Shippings.entity.Shipping;
import com.nkk.Shippings.exception.ResourceNotFoundException;
import com.nkk.Shippings.mapper.ShippingMapper;
import com.nkk.Shippings.repository.ShippingRepository;
import com.nkk.Shippings.service.IShippingService;
import com.nkk.Shippings.service.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

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
        if(orderStatus(orderId)) {
            // Create a new shipping entry
            Shipping shipping = new Shipping();
            shipping.setOrderId(orderId);
            shipping.setShippingAddress(shippingAddress);
            shipping.setTrackingNumber(generateTrackingId());
            shipping.setStatus(String.valueOf(ShippingStatus.PENDING));
            shipping.setCreatedAt(LocalDateTime.now());
            shipping.setUpdatedAt(LocalDateTime.now());
            // Save the shipping entry
            Shipping savedShipping = shippingRepository.save(shipping);
            // Map to DTO and return
            return shippingMapper.mapToShippingDTO(savedShipping);
        } else {
        throw new IllegalStateException("Order is not eligible for shipping. Status must be PAYMENT_SUCCESS.");
        }

    }
    public ShippingDTO getShippingByOrderId(Long orderId) {
        Shipping shipping = shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping", "orderId", orderId));
        return shippingMapper.mapToShippingDTO(shipping);
    }
    @Transactional
    public ShippingDTO updateShippingStatus(Long shippingId, ShippingStatus status) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping","ShippingId", shippingId));
        shipping.setStatus(String.valueOf(status));
        shipping.setUpdatedAt(LocalDateTime.now());
        Shipping updatedShipping = shippingRepository.save(shipping);
        return shippingMapper.mapToShippingDTO(updatedShipping);
    }
    public String generateTrackingId() {
        String prefix = "NKK";
        long timestamp = System.currentTimeMillis();
        int random = new Random().nextInt(90000) + 10000;
        return prefix + "-" + timestamp + "-" + random;
    }

    public boolean orderStatus(Long orderId) {
        try {
            OrderDTO orderDTO = orderFeignClient.getOrderById(orderId);
            return orderDTO != null && "PAYMENT_SUCCESS".equals(orderDTO.getOrderStatus());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to verify order status for orderId: " + orderId, e);
        }
    }

}
