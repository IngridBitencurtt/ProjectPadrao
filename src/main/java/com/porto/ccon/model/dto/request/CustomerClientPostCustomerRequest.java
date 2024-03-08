package com.porto.ccon.model.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomerClientPostCustomerRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String document;

    @NotNull
    private LocalDate birthDate;

    @NotBlank
    private String phone;

    @NotBlank
    private String email;

    public static CustomerClientPostCustomerRequest valueOf(PostCustomerRequest postCustomerRequest) {
        return CustomerClientPostCustomerRequest.builder()
                .name(postCustomerRequest.getName())
                .document(postCustomerRequest.getDocument())
                .birthDate(postCustomerRequest.getBirthDate())
                .phone(postCustomerRequest.getPhone())
                .email(postCustomerRequest.getEmail())
                .build();

    }

}
