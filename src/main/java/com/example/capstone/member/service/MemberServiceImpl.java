package com.example.capstone.member.service;

import com.example.capstone.apiPayload.code.status.ErrorStatus;
import com.example.capstone.aws.s3.AmazonS3Util;
import com.example.capstone.common.QueryService;
import com.example.capstone.exception.GeneralException;
import com.example.capstone.exception.handler.ExceptionHandler;
import com.example.capstone.item.converter.ItemConverter;
import com.example.capstone.item.dto.ItemResponseDTO;
import com.example.capstone.member.Address;
import com.example.capstone.member.Member;
import com.example.capstone.member.common.MemberType;
import com.example.capstone.member.converter.MemberConverter;
import com.example.capstone.member.dto.AddressDTO;
import com.example.capstone.member.dto.MemberRequestDTO;
import com.example.capstone.member.dto.MemberResponseDTO;
import com.example.capstone.member.repository.AddressRepository;
import com.example.capstone.member.repository.MemberRepository;
import com.example.capstone.order.Order;
import com.example.capstone.order.OrderItem;
import com.example.capstone.seller.Seller;
import com.example.capstone.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.capstone.member.converter.MemberConverter.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final SellerRepository sellerRepository;
    private final QueryService queryService;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final AmazonS3Util amazonS3Util;

    @Override
    public MemberResponseDTO.MemberState changeMemberRole(Long memberId, MemberRequestDTO.ToSeller request,
                                                          MultipartFile multipartFile) {
        Member member = queryService.findMember(memberId);

        if (member.getType() == MemberType.ROLE_SELLER) {
            throw new GeneralException(ErrorStatus.MEMBER_ALREADY_SELLER);
        }

        member.changeRole();

        UUID uuid = UUID.randomUUID();
        String path = amazonS3Util.generateSellerProfileImagePath(uuid, multipartFile);
        String url = amazonS3Util.uploadFile(path, multipartFile);

        Seller seller = Seller.builder()
                .details(request.getDetails())
                .introduction(request.getIntroduction())
                .imageUrl(url)
                .member(member)
                .build();

        seller = sellerRepository.save(seller);
        member.addSeller(seller);

        member = memberRepository.save(member);
        return MemberConverter.toMemberState(member);
    }

    @Override
    public MemberResponseDTO.DupCheckField checkField(MemberRequestDTO.DupCheckField dupCheckFields) {

        log.info("DupCheck service start............");
        Member member;

        if (dupCheckFields.getType().equals("loginId")) {

            member = checkLoginId(dupCheckFields);

        } else if (dupCheckFields.getType().equals("nickName")) {

            member = checkNickName(dupCheckFields);

        } else
            throw new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND);

        Member savedMember = memberRepository.save(member);
        log.info("DupCheck service end............");

        return MemberResponseDTO.DupCheckField.builder()
                .id(savedMember.getId())
                .type(dupCheckFields.getType())
                .loginId(savedMember.getLoginId())
                .nickName(savedMember.getNickName())
                .build();
    }

    @Override
    public void deleteTempMember(Long id) {

        log.info("deleteTempMember service start............");

        Member tempMember = memberRepository.findById(id).orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if (tempMember.getPassword() == null) {
            memberRepository.deleteById(id);
        } else {
            throw new ExceptionHandler(ErrorStatus.TEMP_MEMBER_NOT_FOUND);
        }


    }


    @Override
    public MemberResponseDTO.SignUpMember signUp(MemberRequestDTO.SignUpMember signUpMember) {

        log.info("signUp service start............");

        Member tempMember = memberRepository.findById(signUpMember.getId())
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if (!tempMember.getLoginId().equals(signUpMember.getLoginId())) {

            throw new ExceptionHandler(ErrorStatus.NOT_CHECKED_LOGINID);

        } else if (!tempMember.getNickName().equals(signUpMember.getNickName())) {

            throw new ExceptionHandler(ErrorStatus.NOT_CHECKED_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(signUpMember.getPassword());

        Member savedMember = memberRepository.save(toSignUpMember(signUpMember, encodedPassword));

        AddressDTO.Address address = AddressDTO.Address.builder()
                .member(toMemberResponseDTO(savedMember))
                .address(signUpMember.getAddress())
                .details(signUpMember.getDetails())
                .build();

        Address savedAddress = addressRepository.save(toAddress(address));

        log.info("signUp service end............");

        return MemberResponseDTO.SignUpMember.builder()
                .id(savedMember.getId())
                .loginId(savedMember.getLoginId())
                .password(savedMember.getPassword())
                .name(savedMember.getName())
                .nickName(savedMember.getNickName())
                .phone(savedMember.getPhone())
                .address(savedAddress.getAddress())
                .details(savedAddress.getDetails())
                .build();

    }

    @Override
    public MemberRequestDTO.ChangeableMemberData getMemberData(Long id) {

        Member member = memberRepository.findById(id).orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Address address = addressRepository.findByMember_Id(member.getId()).orElseThrow(() -> new ExceptionHandler(ErrorStatus.ADDRESS_NOT_FOUND));

        return MemberRequestDTO.ChangeableMemberData.builder()
                .nickName(member.getNickName())
                .password(member.getPassword())
                .phone(member.getPhone())
                .address(address.getAddress())
                .details(address.getDetails())
                .build();
    }

    @Override
    public MemberRequestDTO.ChangeableMemberData passwordCheck(Long id, String password) {

        Member member = memberRepository.findById(id).orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if(!passwordEncoder.matches(password, member.getPassword()))
            throw new ExceptionHandler(ErrorStatus.PASSWORD_NOT_MATCH);

        Address address = addressRepository.findByMember_Id(member.getId()).orElseThrow(() -> new ExceptionHandler(ErrorStatus.ADDRESS_NOT_FOUND));

        return MemberRequestDTO.ChangeableMemberData.builder()
                .nickName(member.getNickName())
                .password(member.getPassword())
                .phone(member.getPhone())
                .address(address.getAddress())
                .details(address.getDetails())
                .build();
    }

    @Override
    public MemberRequestDTO.ChangeableMemberData changeMemberData(Long id, MemberRequestDTO.ChangeableMemberData changeableMemberData) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        String changedPassword = passwordEncoder.encode(changeableMemberData.getPassword());
        String changedPhone = changeableMemberData.getPhone();

        member.changeMember(changedPassword, changedPhone);
        Member savedMember = memberRepository.save(member);

        Address address = addressRepository.findByMember_Id(savedMember.getId())
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.ADDRESS_NOT_FOUND));
        address.changeAddress(changeableMemberData.getAddress(), changeableMemberData.getDetails());
        Address savedAddress = addressRepository.save(address);

        return MemberRequestDTO.ChangeableMemberData.builder()
                .phone(savedMember.getPhone())
                .password(savedMember.getPassword())
                .address(savedAddress.getAddress())
                .details(savedAddress.getDetails())
                .build();
    }


    private Member checkLoginId(MemberRequestDTO.DupCheckField dupCheckField) {

        Optional<Member> memberByLoginId = memberRepository.findMemberByLoginId(dupCheckField.getLoginId());

        //중복된 로그인 ID가 존재하지 않을 때
        if (memberByLoginId.isEmpty()) {
            return getMemberByNickName(dupCheckField);

            //중복된 로그인 ID가 존재할 때
        } else
            throw new ExceptionHandler(ErrorStatus.DUPLICATED_LOGINID);
    }

    private Member checkNickName(MemberRequestDTO.DupCheckField dupCheckField) {

        Optional<Member> memberByNickName = memberRepository.findMemberByNickName(dupCheckField.getNickName());

        //중복된 닉네임이 존재하지 않을 때
        if (memberByNickName.isEmpty()) {
            return getMemberByLoginId(dupCheckField);

            //중복된 닉네임이 존재할 때
        } else
            throw new ExceptionHandler(ErrorStatus.DUPLICATED_NICKNAME);

    }

    private Member getMemberByNickName(MemberRequestDTO.DupCheckField dupCheckField) {
        if (dupCheckField.getId() == null)
            return Member.builder()
                    .loginId(dupCheckField.getLoginId())
                    .build();

        Member searchedMember = memberRepository.findById(dupCheckField.getId())
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        searchedMember.changeLoginId(dupCheckField.getLoginId());
        return searchedMember;
    }

    private Member getMemberByLoginId(MemberRequestDTO.DupCheckField dupCheckField) {

        if (dupCheckField.getId() == null)
            return Member.builder()
                    .nickName(dupCheckField.getNickName())
                    .build();

        Member searchedMember = memberRepository.findById(dupCheckField.getId())
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));

        searchedMember.changeNickName(dupCheckField.getNickName());

        return searchedMember;

    }

    public List<ItemResponseDTO.ItemStatusList> getOrderedItemStatus(Long memberId) {
        List<ItemResponseDTO.ItemStatusList> list = new ArrayList<>();
        Member member = queryService.findMember(memberId);

        List<Order> orderList = member.getOrderList();
        for (Order order : orderList) {
            ItemResponseDTO.ItemStatusList itemStatusListDTO = ItemConverter.toItemStatusListDTO(order);
            list.add(itemStatusListDTO);
        }

        return list;
    }
}
