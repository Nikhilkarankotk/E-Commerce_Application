package com.nkk.Cart.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
public class Cart {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long cartId;
       private Long userId;

       @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
       private List<CartItem> items = new ArrayList<>();


}

   