package com.example.capstone.member;

import com.example.capstone.common.BaseEntity;
import com.example.capstone.member.common.MemberType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Alarm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    private Boolean isConfirmed;

    @NotNull
    private String title;

    @NotNull
    private String content;

    public void changeConfirmation() {
        this.isConfirmed = !isConfirmed;
    }

}
