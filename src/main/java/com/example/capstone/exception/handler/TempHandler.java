package com.example.capstone.exception.handler;


import com.example.capstone.apiPayload.code.BaseErrorCode;
import com.example.capstone.exception.GeneralException;

public class TempHandler extends GeneralException {

    public TempHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}