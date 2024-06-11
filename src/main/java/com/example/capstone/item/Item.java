package com.example.capstone.item;

import com.example.capstone.common.BaseEntity;
import com.example.capstone.inquiry.Inquiry;
import com.example.capstone.item.common.ItemType;
import com.example.capstone.order.OrderItem;
import com.example.capstone.seller.Seller;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    private ItemType type;

    @NotBlank
    private String name;

    private String simpleExplanation;

    @NotNull(message = "상품 가격은 필수입니다. 0원 이상입니다.")
    @Min(0)
    private Integer price;

    @NotNull(message = "배달비는 필수입니다. 0원 이상입니다.")
    @Min(0)
    private Integer deliveryCharge;

    @NotNull(message = "수량은 필수입니다. 0개 이상입니다.")
    @Min(0)
    private Integer stock;

//    @NotNull(message = "상품 상세 이미지는 필수입니다.")
    private String itemDetailsImageUrl;

    @NotNull
    @Future
    @Builder.Default
    private LocalDateTime deadline = LocalDateTime.now().plusDays(14);

    @OneToOne
    @JoinColumn(name = "group_purchase_item_id")
    private GroupPurchaseItem groupPurchaseItem;


    @OneToMany(mappedBy = "item",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    private List<ItemImage> itemImages = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    @Builder.Default
    private List<Inquiry> inquiries = new ArrayList<>();

    @OneToMany(mappedBy = "item",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addItemDetailsImageUrl(String url) {
        itemDetailsImageUrl = url;
    }

    public void addItemImage(ItemImage itemImage) {
        itemImages.add(itemImage);
        itemImage.setItem(this);
    }

    public void addItemInquiry(Inquiry inquiry) {
        inquiries.add(inquiry);
        inquiry.setItem(this);
    }

    public void addGroupPurchaseItem(GroupPurchaseItem groupPurchaseItem) {
        this.groupPurchaseItem = groupPurchaseItem;
    }
}
