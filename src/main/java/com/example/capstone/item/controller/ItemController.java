package com.example.capstone.item.controller;

import com.example.capstone.apiPayload.ApiResponse;
import com.example.capstone.item.dto.ItemRequestDTO;
import com.example.capstone.item.dto.ItemResponseDTO;
import com.example.capstone.item.service.ItemQueryService;
import com.example.capstone.item.service.ItemService;
import com.example.capstone.security.dto.MemberSecurityDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class ItemController {
    private final ItemQueryService itemQueryService;
    private final ItemService itemService;

    @GetMapping("/item")
    public ApiResponse<ItemResponseDTO.ItemList> getItemList(@Valid @Positive @RequestParam(name = "category-id") Long categoryId,
                                                             @Min(1) @RequestParam(name = "page") Integer page,
                                                             @Positive @RequestParam(name = "size") Integer size) {

        return ApiResponse.onSuccess(itemService.getItemList(categoryId, page - 1, size));
    }

    @GetMapping("/item/deadline")
    public ApiResponse<ItemResponseDTO.ItemList> getImminentItemList(@Min(1) @RequestParam(name = "page") Integer page,
                                                             @Positive @RequestParam(name = "size") Integer size) {

        return ApiResponse.onSuccess(itemService.getImminentItemList(page - 1, size));
    }
    @GetMapping("/item/ranking")
    public ApiResponse<ItemResponseDTO.ItemList> getPopularItemList(@Min(1) @RequestParam(name = "page") Integer page,
                                                                     @Positive @RequestParam(name = "size") Integer size) {

        return ApiResponse.onSuccess(itemService.getPopularItemList(page - 1, size));
    }

    @GetMapping("/auth/item/subscription")
    public ApiResponse<ItemResponseDTO.ItemList> getSubscribedItemList(@AuthenticationPrincipal MemberSecurityDTO member,
                                                                       @Min(1) @RequestParam(name = "page") Integer page,
                                                                       @Positive @RequestParam(name = "size") Integer size) {

        return ApiResponse.onSuccess(itemService.getSubscribedItemList(member.getId(), page - 1, size));

    }
    @GetMapping("/item/subscription")
    public ApiResponse<ItemResponseDTO.ItemList> getAllItemList(
                                                                       @Min(1) @RequestParam(name = "page") Integer page,
                                                                       @Positive @RequestParam(name = "size") Integer size) {

        return ApiResponse.onSuccess(itemService.getAllItemList(page - 1, size));
    }


    @GetMapping("/item/search")
    public ApiResponse<ItemResponseDTO.ItemList> searchItemList(@Valid @RequestParam(name = "keyword") String keyword,
                                                                @Min(1) @RequestParam(name = "page") Integer page,
                                                                @Positive @RequestParam(name = "size") Integer size) {

        return ApiResponse.onSuccess(itemQueryService.searchItemList(keyword, page - 1, size));

    }

    @GetMapping("/item/{itemId}")
    public ApiResponse<ItemResponseDTO.DetailsOfItem> searchDetailOfItem(@PathVariable Long itemId) {

        return ApiResponse.onSuccess(itemService.getDetailOfItem(itemId));

    }

    @PostMapping("/auth/item")
    public ApiResponse<ItemResponseDTO.ItemUpload> uploadItem(
            @AuthenticationPrincipal MemberSecurityDTO member,
            @RequestPart @Valid ItemRequestDTO.ItemUpload request,
            @RequestPart(required = false) List<MultipartFile> itemImages,
            @RequestPart(required = false) MultipartFile itemDetailsImage
            ) {
        Long memberId = member.getId();
        ItemResponseDTO.ItemUpload response = itemService.uploadItem(memberId, request, itemImages, itemDetailsImage);

        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/auth/v1/item")
    public ApiResponse<ItemResponseDTO.ItemUpload> uploadItemV1(
            @AuthenticationPrincipal MemberSecurityDTO member,
            @Valid ItemRequestDTO.ItemUpload request
    ) {
        Long memberId = member.getId();
        ItemResponseDTO.ItemUpload response = itemService.uploadItemV1(memberId, request);

        return ApiResponse.onSuccess(response);
    }

}
