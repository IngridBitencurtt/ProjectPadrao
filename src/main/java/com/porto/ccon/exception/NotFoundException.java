package com.porto.ccon.exception;

import org.springframework.http.HttpStatus;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotFoundException extends RestException {

    private static final long serialVersionUID = -4546342692615580312L;

    private String responseBodyCode;

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    public NotFoundException(String responseBodyCode) {
        this.responseBodyCode = responseBodyCode;
    }

    @Override
    public String getResponseBodyCode() {
        return responseBodyCode;
    }

}
