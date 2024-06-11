package com.example.capstone.inquiry;

import com.example.capstone.common.BaseEntity;
import com.example.capstone.inquiry.common.InquiryStatus;
import com.example.capstone.item.Item;
import com.example.capstone.member.Member;
import com.example.capstone.seller.Seller;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id")
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id")
    private Seller toMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    private String content;

    private String answer;

    public void setItem(Item item) {
        this.item = item;
    }
}
