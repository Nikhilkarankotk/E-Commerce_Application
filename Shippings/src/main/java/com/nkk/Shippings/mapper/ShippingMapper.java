package com.nkk.Shippings.mapper;

import com.nkk.Shippings.dto.ShippingDTO;
import com.nkk.Shippings.entity.Shipping;
import org.springframework.stereotype.Component;
@Component
public class ShippingMapper {

    public ShippingDTO mapToShippingDTO(Shipping shipping) {
        ShippingDTO shippingDTO = new ShippingDTO();
        shippingDTO.setShippingId(shipping.getShippingId());
        shippingDTO.setOrderId(shipping.getOrderId());
        shippingDTO.setShippingAddress(shipping.getShippingAddress());
        shippingDTO.setStatus(shipping.getStatus());
        shippingDTO.setCreatedAt(shipping.getCreatedAt());
        shippingDTO.setUpdatedAt(shipping.getUpdatedAt());
        return shippingDTO;
    }
    public Shipping mapToShipping(ShippingDTO shippingDTO) {
        Shipping shipping = new Shipping();
        shipping.setShippingId(shippingDTO.getShippingId());
        shipping.setOrderId(shippingDTO.getOrderId());
        shipping.setShippingAddress(shippingDTO.getShippingAddress());
        shipping.setStatus(shippingDTO.getStatus());
        shipping.setCreatedAt(shippingDTO.getCreatedAt());
        shipping.setUpdatedAt(shippingDTO.getUpdatedAt());
        return shipping;
    }
}
