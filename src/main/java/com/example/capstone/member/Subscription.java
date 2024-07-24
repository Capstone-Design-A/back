package com.example.capstone.member;

import com.example.capstone.common.BaseEntity;
import com.example.capstone.seller.Seller;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "FROM_MEMBER_AND_TO_MEMBER_UNIQUE",
                columnNames = {"fromMemberId", "toMemberId"}
        )})
public class Subscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id")
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id")
    private Seller toMember;
}
