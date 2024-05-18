package com.example.capstone.apiPayload.code.status;

import com.example.capstone.apiPayload.code.BaseErrorCode;
import com.example.capstone.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러, 관리자에게 문의 바랍니다."),

    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON_400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON_401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "금지된 요청입니다."),

    // Member 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER_400_1", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER_400_2", "닉네임은 필수 입니다."),

    // paging
    PAGE_NUMBER_BAD_REQUEST(HttpStatus.BAD_REQUEST, "PAGE_NUMBER_400_1", "페이지 번호는 1 이상이어야 합니다."),

    // 카테고리
    ITEM_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_400_1", "상품 카테고리 id는 1 이상 ? 이하입니다."),

    // 구독
    ALREADY_SUBSCRIBED(HttpStatus.BAD_REQUEST, "SUBSCRIBED_400_1", "이미 구독한 사용자입니다."),
    ALREADY_UNSUBSCRIBED(HttpStatus.BAD_REQUEST, "SUBSCRIBED_400_2", "이미 구독을 취소한 사용자입니다."),

    //item
    ITEM_NOT_FOUND(HttpStatus.BAD_REQUEST, "ITEM_400_1", "해당되는 상품을 조회할 수 없습니다."),

    //Token
    ACCESS_TOKEN_NOT_ACCEPTED(HttpStatus.UNAUTHORIZED, "Jwt_400_1", "Access Token이 존재하지 않습니다."),
    ACCESS_TOKEN_BADTYPE(HttpStatus.UNAUTHORIZED, "Jwt_400_2", "Access Token의 타입이 bearer가 아닙니다."),
    MALFORMED_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "JWT_400_3", "Access Token의 값이 올바르게 설정되지 않았습니다. "),
    BAD_SIGNED_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "JWT_400_4", "Access Token의 서명이 올바르지 않습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "JWT_400_5", "Access Token이 만료되었습니다."),
    REFRESH_TOKEN_NOT_ACCEPTED(HttpStatus.UNAUTHORIZED, "Jwt_400_6", "Refresh Token이 존재하지 않습니다."),
    MALFORMED_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "JWT_400_7", "Refresh Token의 값이 올바르게 설정되지 않았습니다. "),
    EXPIRED_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "JWT_400_8", "Refresh Token이 만료되었습니다.")
            ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .message(message)
                .code(code)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .message(message)
                .code(code)
                .httpStatus(httpStatus)
                .build();
    }
}
