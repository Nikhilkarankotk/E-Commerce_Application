package com.nkk.Shippings.service;

import com.nkk.Shippings.dto.ShippingDTO;

public interface IShippingService {

    /**
     *
     * @param orderId
     * @param shippingAddress
     * @return
     */
    public ShippingDTO createShipping(Long orderId, String shippingAddress);

    /**
     *
     * @param orderId
     * @return
     */
    public ShippingDTO getShippingByOrderId(Long orderId);

    /**
     *
     * @param shippingId
     * @param status
     * @return
     */
    public ShippingDTO updateShippingStatus(Long shippingId, String status);
}
