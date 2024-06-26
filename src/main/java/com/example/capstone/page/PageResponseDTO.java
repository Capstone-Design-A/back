package com.example.capstone.page;

import com.example.capstone.item.dto.ItemResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class PageResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Main {
        public Integer numberUnreadAlarms;
        public Integer numberItemsInCart;
        public List<ItemResponseDTO.Item> imminentItemListPreview;
        public List<ItemResponseDTO.Item> popularItemListPreview;
        public List<ItemResponseDTO.Item> subscribedItemListPreview;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostPreview {
        private Long postId;
        private String imageUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostPreviews {
        private Long memberId;
        private String memberImageUrl;
        private String memberName;
        private String simpleIntro;
        private String detailIntro;
        private Integer numberPosts;
        private List<PostPreview> postPreviews;
    }
}
