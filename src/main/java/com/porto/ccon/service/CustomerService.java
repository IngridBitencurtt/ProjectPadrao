package com.porto.ccon.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.porto.ccon.exception.CustomerClientException;
import com.porto.ccon.exception.NotFoundException;
import com.porto.ccon.exception.UnprocessableEntityException;
import com.porto.ccon.model.domain.CustomerDomain;
import com.porto.ccon.model.dto.request.CustomerClientPostCustomerRequest;
import com.porto.ccon.model.dto.request.PostCustomerRequest;
import com.porto.ccon.model.dto.response.CustomerClientPostCustomerResponse;
import com.porto.ccon.model.dto.response.GetCustomerByIdResponse;
import com.porto.ccon.repository.CustomerRepository;
import com.porto.ccon.service.client.CustomerClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final RestTemplate restTemplate;
    private final CustomerRepository customerRepository;
    private final CustomerClient customerClient;

    public String createCustomerService(PostCustomerRequest postCustomerRequest) {
        try {
            Optional<CustomerDomain> findDocument = this.customerRepository.findByDocument(postCustomerRequest.getDocument());
            if (findDocument.isPresent()) {
                throw new UnprocessableEntityException("422.002");
            }

            CustomerClientPostCustomerResponse customerClientPostCustomerResponse = customerClient
                    .createCustomer(CustomerClientPostCustomerRequest.valueOf(postCustomerRequest));
            CustomerDomain customerDomain = CustomerDomain.valueOf(postCustomerRequest);
            customerDomain.setExternalCode(customerClientPostCustomerResponse.getExternalId());
            customerRepository.save(customerDomain);
            return customerDomain.getId();

        } catch (CustomerClientException e) {
            throw new UnprocessableEntityException("422.001");
        }

    }

    public GetCustomerByIdResponse findById(String id) {
        CustomerDomain customerDomain = customerRepository.findById(id).orElseThrow(NotFoundException::new);
        return GetCustomerByIdResponse.valueOf(customerDomain);
    }

}
