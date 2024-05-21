package com.example.capstone.seller.converter;

import com.example.capstone.member.Member;
import com.example.capstone.seller.Post;
import com.example.capstone.seller.PostImage;
import com.example.capstone.seller.Seller;
import com.example.capstone.seller.dto.PostResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PostConverter {

    public static PostResponseDTO.Post toPost(Post post) {
        return PostResponseDTO.Post.builder()
                .postId(post.getId())
                .createdAt(LocalDate.from(post.getCreatedAt()))
                .imageList(post.getPostImages().stream()
                        .map(PostImage::getImageUrl)
                        .toList())
                .content(post.getContent())
                .build();
    }

    public static PostResponseDTO.PostPreview toPostPreview(Post post) {
        Optional<String> imageUrl = Optional.ofNullable(post.getPostImages().get(0).getImageUrl());

        return PostResponseDTO.PostPreview.builder()
                .postId(post.getId())
                .imageList(imageUrl.orElse(""))
                .build();
    }

    public static PostResponseDTO.PostPreviews toPostPreviews(List<Post> postList, Member member) {
        List<PostResponseDTO.PostPreview> postPreviewList = postList.stream()
                .map(PostConverter::toPostPreview)
                .toList();

        Seller seller = member.getSeller();

        return PostResponseDTO.PostPreviews.builder()
                .memberId(member.getId())
                .memberImageUrl(seller.getImageUrl())
                .memberName(member.getName())
                .simpleIntro(seller.getIntroduction())
                .detailIntro(seller.getDetails())
                .numberPosts(seller.getPostList().size())
                .postPreviews(postPreviewList)
                .build();
    }
}
