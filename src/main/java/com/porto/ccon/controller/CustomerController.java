package com.porto.ccon.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.porto.ccon.model.dto.request.PostCustomerRequest;
import com.porto.ccon.model.dto.response.GetCustomerByIdResponse;
import com.porto.ccon.service.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {

    public final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> customer(@RequestBody @Valid PostCustomerRequest postCustomerRequest) {

        String externalId = this.customerService.createCustomerService(postCustomerRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(externalId)
                .toUri();

        return ResponseEntity.created(location).build();

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public GetCustomerByIdResponse findCustomerId(@PathVariable("id") String id) {
        return this.customerService.findById(id);
    }

}
