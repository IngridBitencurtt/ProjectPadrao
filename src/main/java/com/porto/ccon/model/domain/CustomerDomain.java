package com.porto.ccon.model.domain;

import java.time.LocalDate;

import org.hibernate.annotations.GenericGenerator;

import com.porto.ccon.model.dto.request.PostCustomerRequest;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
public class CustomerDomain {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "external_code")
    private String externalCode;

    @Column(name = "document")
    private String document;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "customer_since")
    private LocalDate customerSince;

    @Column(name = "last_update")
    private LocalDate lastUpdate;

    public static CustomerDomain valueOf(PostCustomerRequest postCustomerRequest) {
        CustomerDomain customerDomain = new CustomerDomain();
        customerDomain.setName(postCustomerRequest.getName());
        customerDomain.setDocument(postCustomerRequest.getDocument());
        customerDomain.setBirthDate(postCustomerRequest.getBirthDate());
        customerDomain.setPhone(postCustomerRequest.getPhone());
        customerDomain.setEmail(postCustomerRequest.getEmail());
        customerDomain.setCustomerSince(LocalDate.now());
        customerDomain.setLastUpdate(LocalDate.now());
        return customerDomain;
    }

}