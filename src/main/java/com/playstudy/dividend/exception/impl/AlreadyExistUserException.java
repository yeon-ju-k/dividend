package com.playstudy.dividend.exception.impl;

import com.playstudy.dividend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class AlreadyExistUserException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();  // 400번대 - client 오류 코드
    }

    @Override
    public String getMessage() {
        return "이미 존재하는 사용자명입니다.";
    }

}


