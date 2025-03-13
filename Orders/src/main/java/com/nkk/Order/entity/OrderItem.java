package com.nkk.Order.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;
    private Long productId;
    private Integer quantity;
    private Double productPrice;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
