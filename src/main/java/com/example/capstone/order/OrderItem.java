package com.example.capstone.order;

import com.example.capstone.common.BaseEntity;
import com.example.capstone.item.Item;
import com.example.capstone.item.Review;
import com.example.capstone.order.common.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Min(0)
    private Integer quantity;

    @OneToOne(mappedBy = "orderItem")
    private Review review;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;
}
