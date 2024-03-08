package com.porto.ccon.model.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.porto.ccon.model.domain.CustomerDomain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetCustomerByIdResponse {

    private String name;

    private String externalCode;

    private String document;

    private LocalDate birthDate;

    private String phone;

    private String email;

    private LocalDate customerSince;

    private LocalDate lastUpdate;

    public static GetCustomerByIdResponse valueOf(CustomerDomain customerDomain) {
        return GetCustomerByIdResponse.builder()
                .name(customerDomain.getName())
                .externalCode(customerDomain.getExternalCode())
                .document(customerDomain.getDocument())
                .birthDate(customerDomain.getBirthDate())
                .phone(customerDomain.getPhone())
                .email(customerDomain.getEmail())
                .customerSince(customerDomain.getCustomerSince())
                .lastUpdate(customerDomain.getLastUpdate())
                .build();
    }
}