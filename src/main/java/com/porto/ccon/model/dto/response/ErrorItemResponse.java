package com.porto.ccon.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorItemResponse {

    private String field;

    private String message;

    private String title;

    public ErrorItemResponse(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
