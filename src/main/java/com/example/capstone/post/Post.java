package com.example.capstone.post;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    @OneToMany(mappedBy = "post")
    @Builder.Default
    private List<PostImage> postImageList = new ArrayList<>();

    public void addMember(Member member) {
        this.member = member;
        member.getPostList().add(this);
    }

    public void addImage(PostImage postImage) {
        postImageList.add(postImage);
        postImage.setItem(this);
    }

    public void addImage(int index, PostImage postImage) {
        postImageList.remove(index);
        postImageList.add(index, postImage);
        postImage.setItem(this);
    }

    public void setMember(Member member) {
        this.member = member;
    }
}