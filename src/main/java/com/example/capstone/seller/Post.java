package com.example.capstone.seller;

import com.example.capstone.common.BaseEntity;
import com.example.capstone.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    @OneToMany(mappedBy = "post")
    @Builder.Default
    private List<PostImage> postImages = new ArrayList<>();

    public void addItemImage(PostImage postImage) {
        postImages.add(postImage);
        postImage.setItem(this);
    }

    public void setMember(Member member) {
        this.member = member;
    }
}