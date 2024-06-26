package com.example.capstone.order.service;

import com.example.capstone.apiPayload.code.status.ErrorStatus;
import com.example.capstone.exception.GeneralException;
import com.example.capstone.exception.handler.ExceptionHandler;
import com.example.capstone.item.Item;
import com.example.capstone.item.repository.ItemRepository;
import com.example.capstone.member.Member;
import com.example.capstone.member.repository.MemberRepository;
import com.example.capstone.order.Cart;
import com.example.capstone.order.converter.CartConverter;
import com.example.capstone.order.dto.CartRequestDTO;
import com.example.capstone.order.dto.CartResponseDTO;
import com.example.capstone.order.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.capstone.order.converter.CartConverter.toCartResponseDTO;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    public Integer countAllItemInCart(Long memberId){

        return cartRepository.countAllItemInCart(memberId);
    }

    @Override
    public void saveItemInCart(CartRequestDTO.RequestedCart cartRequestDTO) {
        Cart cart;

        Optional<Cart> searchedCart = cartRepository.searchCartByMemberAndItem(cartRequestDTO.getItemId(), cartRequestDTO.getMemberId());

        if(searchedCart.isPresent()){
            cart = searchedCart.get();
            Integer sumQuantity = cartRequestDTO.getQuantity() + cart.getQuantity();
            searchedCart.get().changeQuantity(sumQuantity);
            cartRepository.save(cart);

        } else {

            Item item = itemRepository.findById(cartRequestDTO.getItemId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));

            Member member = memberRepository.findById(cartRequestDTO.getMemberId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

            cart = Cart.builder()
                    .item(item)
                    .member(member)
                    .quantity(cartRequestDTO.getQuantity())
                    .build();

            cartRepository.save(cart);
        }
        log.info("saveItemInCart service success.......");
    }

    @Override
    public CartResponseDTO.CartResult getItemsInCart(Long memberId) {

        List<Cart> cartList = cartRepository.searchItemInCart(memberId);

        List<CartResponseDTO.Cart> carts = cartList.stream().map(CartConverter::toCartResponseDTO).collect(Collectors.toList());


        Map<Long, List<CartResponseDTO.Cart>> cartGroup = carts.stream()
                .collect(Collectors.groupingBy(cart -> cart.getItem().getSellerId()));

        //판매자가 같으면 묶음 배송 처리
        Integer sum = 0;
        for (Long key : cartGroup.keySet()) {
            sum += cartGroup.get(key).get(0).getItem().getDeliveryCharge();
        }

        return CartResponseDTO.CartResult.builder()
                .carts(carts)
                .sumDeliveryCharge(sum)
                .build();
    }

    @Override
    public void deleteItem(Long memberId, Long cartId) {

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ExceptionHandler(ErrorStatus.ITEM_NOT_FOUND));
        if (!cart.getMember().getId().equals(memberId)){
            throw new ExceptionHandler(ErrorStatus.CART_NOT_MATCH_MEMBER);
        }

        cartRepository.deleteById(cartId);
        log.info("deleteItemInCart Success.............");

    }

    @Override
    public void deleteItemsInCart(Long memberId) {

        cartRepository.deleteItemsInCart(memberId);

        log.info("deleteItemsInCart Success.............");

    }
    @Override
    public CartResponseDTO.Cart changeQuantity(Long cartId, Integer askedQuantity) {

        Cart cart = cartRepository.findById(cartId).orElseThrow(()->new ExceptionHandler(ErrorStatus.CART_NOT_FOUND));
        cart.changeQuantity(askedQuantity);
        Cart savedCart = cartRepository.save(cart);

        return toCartResponseDTO(savedCart);
    }

}
