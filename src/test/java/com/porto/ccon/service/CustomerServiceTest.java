package com.porto.ccon.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.porto.ccon.exception.CustomerClientException;
import com.porto.ccon.exception.NotFoundException;
import com.porto.ccon.exception.UnprocessableEntityException;
import com.porto.ccon.model.domain.CustomerDomain;
import com.porto.ccon.model.dto.request.CustomerClientPostCustomerRequest;
import com.porto.ccon.model.dto.request.PostCustomerRequest;
import com.porto.ccon.model.dto.response.CustomerClientPostCustomerResponse;
import com.porto.ccon.repository.CustomerRepository;
import com.porto.ccon.service.client.CustomerClient;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerClient customerClient;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomerServiceSuccess() throws Exception {
        givenValidPostCustomerRequest();
        givenCustomerRepositoryFindByDocumentReturnsEmpty();
        givenCustomerClientCreateCustomerReturnsNewCustomer();
        whenCallCreateCustomerService();
        thenExpectCustomerRepositoryFindByDocumentCalledOnce();
        thenExpectCustomerClientCreateCustomerCalledOnce();
        thenExpectCustomerRepositorySaveCalledOnce();
    }

    @Test
    void createCustomerServiceDocumentAlreadyExists() {
        givenValidPostCustomerRequest();
        givenCustomerRepositoryFindByDocumentReturns();
        whenCallCreateCustomerServiceThrowsUnprocessableEntityException();
    }

    @Test
    void createCustomerServiceClientException() throws Exception {
        givenValidPostCustomerRequest();
        givenCustomerRepositoryFindByDocumentReturnsEmpty();
        givenCustomerClientCreateCustomerThrowsCustomerClientException();
        whenCallCreateCustomerServiceThrowsUnprocessableEntityException();
    }

    @Test
    void findByIdCustomerNotFound() {
        givenCustomerRepositoryFindByIdReturnsEmpty();
        whenCallCreateCustomerServiceThrowsNotFoundException();
    }

    // Given
    private void givenCustomerRepositoryFindByDocumentReturns() {
        doReturn(Optional.of(new CustomerDomain())).when(customerRepository).findByDocument(anyString());
    }

    private PostCustomerRequest givenValidPostCustomerRequest() {
        return new PostCustomerRequest("John Doe", "12345678999", LocalDate.now(), "1234567890",
                "john.doe@example.com");
    }

    private void givenCustomerRepositoryFindByDocumentReturnsEmpty() {
        doReturn(Optional.empty()).when(customerRepository).findByDocument(anyString());
    }

    private void givenCustomerRepositoryFindByIdReturnsEmpty(){
        when(customerRepository.findById(anyString())).thenReturn(Optional.empty());
    }

    private void givenCustomerClientCreateCustomerReturnsNewCustomer() throws CustomerClientException {
        doReturn(new CustomerClientPostCustomerResponse("124")).when(customerClient).createCustomer(any(
                CustomerClientPostCustomerRequest.class));
    }

    private void givenCustomerClientCreateCustomerThrowsCustomerClientException() throws CustomerClientException {
        doThrow(CustomerClientException.class).when(customerClient).createCustomer(any(CustomerClientPostCustomerRequest.class));
    }

    // When
    private void whenCallCreateCustomerServiceThrowsUnprocessableEntityException() {
        assertThrows(UnprocessableEntityException.class, () -> customerService.createCustomerService(givenValidPostCustomerRequest()));
    }

    private void whenCallCreateCustomerServiceThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> customerService.findById("123"));
    }

    private void whenCallCreateCustomerService() {
        customerService.createCustomerService(givenValidPostCustomerRequest());
    }

    // Then

    private void thenExpectCustomerRepositoryFindByDocumentCalledOnce() {
        verify(customerRepository).findByDocument(anyString());
    }

    private void thenExpectCustomerClientCreateCustomerCalledOnce() throws CustomerClientException {
        verify(customerClient).createCustomer(any(CustomerClientPostCustomerRequest.class));
    }

    private void thenExpectCustomerRepositorySaveCalledOnce() {
        verify(customerRepository).save(any(CustomerDomain.class));
    }
}
