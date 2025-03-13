package com.nkk.Cart.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;
    private Long productId;
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
}