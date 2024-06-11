package com.example.capstone.item.converter;

import com.example.capstone.item.Category;
import com.example.capstone.item.Item;
import com.example.capstone.item.ItemImage;
import com.example.capstone.item.common.ImageType;
import com.example.capstone.item.common.ItemType;
import com.example.capstone.item.dto.ItemRequestDTO;
import com.example.capstone.item.dto.ItemResponseDTO;
import com.example.capstone.order.Order;
import com.example.capstone.order.OrderItem;
import com.example.capstone.seller.Seller;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.capstone.item.converter.ItemImageConverter.toItemImageList;

public class ItemConverter {

    public static ItemResponseDTO.ItemStatus toItemStatusDTO(OrderItem orderItem) {
        Item item = orderItem.getItem();

        return ItemResponseDTO.ItemStatus.builder()
                .orderItemId(orderItem.getId())
                .itemId(item.getId())
                .itemName(item.getName())
                .itemPrice(item.getPrice())
                .quantity(orderItem.getQuantity())
                .totalPrice(orderItem.getQuantity() * item.getPrice())
                .deliveryCharge(item.getDeliveryCharge())
                .orderStatus(orderItem.getStatus())
                .build();
    }

    public static ItemResponseDTO.ItemStatusList toItemStatusListDTO(Order order) {
        List<ItemResponseDTO.ItemStatus> itemStatusList = order.getOrderItemList().stream()
                .map(ItemConverter::toItemStatusDTO)
                .collect(Collectors.toList());

        return ItemResponseDTO.ItemStatusList.builder()
                .orderId(order.getId())
                .itemStatusList(itemStatusList)
                .build();
    }

    public static ItemResponseDTO.Item toItemResponseDTO(Item item) {
        return ItemResponseDTO.Item.builder()
                .id(item.getId())
                .name(item.getName())
                .category(item.getCategory().getName())
                .stock(item.getStock())
                .price(item.getPrice())
                .discountPrice(0)   // TODO: 할인 어떻게?
                .imageUrl(item.getItemImages().isEmpty() ? null : item.getItemImages().get(0).getImageUrl())
                .deadline(item.getDeadline())
                .build();
    }

    public static ItemResponseDTO.ItemList toItemList (Page<Item> itemPage) {
        List<ItemResponseDTO.Item> itemList = itemPage.stream()
                .map(ItemConverter::toItemResponseDTO)
                .toList();

        return ItemResponseDTO.ItemList.builder()
                .listSize(itemPage.getSize())
                .page(itemPage.getTotalPages())
                .totalElement(itemPage.getTotalElements())
                .isFirst(itemPage.isFirst())
                .isLast(itemPage.isLast())
                .itemList(itemList)
                .build();
    }

    /**
     * 임시처리로 하드코딩 되어 있으므로 추후에 수정 되어야 함!
     */
    public static ItemResponseDTO.DetailsOfItem toDetailsOfItemResponseDTO(Item item) {
        return ItemResponseDTO.DetailsOfItem.builder()
                .id(item.getId())
                .memberId(item.getSeller().getMember().getId())
                .name(item.getName())
                .category(item.getCategory().getName())
                .stock(item.getStock())
                .price(item.getPrice())
                .discountPrice(0)   // TODO: 할인 어떻게?
                .ItemDetailsImageUrl(item.getItemDetailsImageUrl())
                .imageUrl(toItemImageList(filterCommonImage(item.getItemImages())))
                .deadline(item.getDeadline())
                .build();
    }

    /**
     * ImageType.COMMON인 ItemImage만 filter하는 메소드
     * @param itemImageList
     * @return
     */
    private static List<ItemImage> filterCommonImage(List<ItemImage> itemImageList) {
        return itemImageList.stream()
                .filter(i -> i.getImageType().equals(ImageType.COMMON))
                .collect(Collectors.toList());
    }

    public static ItemResponseDTO.DetailsOfItem toTempDetailsOfItemResponseDTO(Item item) {
        return ItemResponseDTO.DetailsOfItem.builder()
                .id(item.getId())
                .memberId(item.getSeller().getMember().getId())
                .name(item.getName())
                .category(item.getCategory().getName())
                .stock(item.getStock())
                .price(item.getPrice())
                .discountPrice(0)   // TODO: 할인 어떻게?
                .ItemDetailsImageUrl(item.getItemDetailsImageUrl())
                .imageUrl(toItemImageList(filterCommonImage(item.getItemImages())))
                .deadline(item.getDeadline())
                .build();
    }


    public static ItemResponseDTO.DetailsOfItemWithSeller toDetailsOfItemWithSellerResponseDTO(Item item) {
        return ItemResponseDTO.DetailsOfItemWithSeller.builder()
                .id(item.getId())
                .name(item.getName())
                .sellerId(item.getSeller().getId())
                .category(item.getCategory().getName())
                .price(item.getPrice())
                .discountPrice(0)   // TODO: 할인 어떻게?
                .ItemDetailsImageUrl(item.getItemDetailsImageUrl())
                .imageUrl(toItemImageList(filterCommonImage(item.getItemImages())))
                .deadline(item.getDeadline())
                .build();
    }

    public static Item toItemEntity(Seller seller, ItemRequestDTO.ItemUpload request, Category category) {
        return Item.builder()
                .seller(seller)
                .category(category)
                .type(ItemType.COMMON)
                .name(request.getItemName())
                .simpleExplanation(request.getSimpleExplanation())
                .price(request.getPrice())
                .deliveryCharge(request.getDeliveryPrice())
                .stock(request.getStock())
                .deadline(request.getDeadLine())
                .build();
    }

    public static ItemResponseDTO.ItemUpload toItemUpload(Item item) {
        ItemResponseDTO.DetailsOfItem itemDTO = toDetailsOfItemResponseDTO(item);

        if (item.getGroupPurchaseItem() == null) {
            return ItemResponseDTO.ItemUpload.builder()
                    .item(itemDTO)
                    .groupPurchaseInfo(null)
                    .build();
        }

        ItemResponseDTO.GroupPurchaseInfo groupPurchaseInfo = ItemResponseDTO.GroupPurchaseInfo.builder()
                .targetQuantity(item.getGroupPurchaseItem().getTargetQuantity())
                .discountPrice(item.getGroupPurchaseItem().getDiscountPrice())
                .build();

        return ItemResponseDTO.ItemUpload.builder()
                .item(itemDTO)
                .groupPurchaseInfo(groupPurchaseInfo)
                .build();
    }

    public static ItemResponseDTO.ItemWithSeller toItemResponseDTOWithSeller(Item item) {
        return ItemResponseDTO.ItemWithSeller.builder()
                .id(item.getId())
                .name(item.getName())
                .stock(item.getStock())
                .price(item.getPrice())
                .discountPrice(0)   // TODO: 할인 어떻게?
                .imageUrl(item.getItemImages().isEmpty() ? null : item.getItemImages().get(0).getImageUrl())
                .deliveryCharge(item.getDeliveryCharge())
                .sellerId(item.getSeller().getId())
                .build();
    }
}
