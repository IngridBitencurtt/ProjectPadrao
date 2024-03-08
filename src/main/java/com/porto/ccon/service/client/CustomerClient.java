package com.porto.ccon.service.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.porto.ccon.exception.CustomerClientException;
import com.porto.ccon.model.dto.request.CustomerClientPostCustomerRequest;
import com.porto.ccon.model.dto.response.CustomerClientPostCustomerResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerClient {
    private final RestTemplate restTemplate;

    public CustomerClientPostCustomerResponse createCustomer(CustomerClientPostCustomerRequest customerClientPostCustomerRequest)
            throws CustomerClientException {
        try {
            return restTemplate.postForEntity(
                    "https://run.mocky.io/v3/d9cc6fc0-826f-4174-9d34-619491eab252",
                    customerClientPostCustomerRequest,
                    CustomerClientPostCustomerResponse.class).getBody();

        } catch (HttpStatusCodeException e) {
            throw new CustomerClientException(e.getMessage(), "ONB-0019", (HttpStatus) e.getStatusCode());
        }

    }
}
