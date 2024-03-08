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
import org.springframework.http.HttpStatus;

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
        thenExpectFindByDocument();
        thenExpectCustomerCreationResponse();
        thenExpectCreateNewCustomer();
        thenExpectCustomerRepositoryFindByDocumentCalledOnce();
        thenExpectCustomerClientCreateCustomerCalledOnce();
        thenExpectCustomerRepositorySaveCalledOnce();
    }

    @Test
    void createCustomerServiceDocumentAlreadyExists() {
        givenValidPostCustomerRequest();
        thenExpectCustomerRepositoryWhenFindByDocument();
        unprocessableEntityExceptionWhenDocumentExists();
    }

    @Test
    void createCustomerServiceClientException() throws Exception {
        givenValidPostCustomerRequest();
        thenExpectFindByDocument();
        clientExceptionWhenCustomerClienteCreateCustomer();
        unprocessableEntityExceptionWhenDocumentExists();
    }

    @Test
    void findByIdCustomerNotFound() {
        thenExpectEmptyFindById();
        notFoundExceptionWhenFindByIdEmpty();
    }

    private void thenExpectCustomerRepositoryFindByDocumentCalledOnce() {
        verify(customerRepository).findByDocument(anyString());

    }

    private void thenExpectCustomerClientCreateCustomerCalledOnce() throws CustomerClientException {
        verify(customerClient).createCustomer(any(CustomerClientPostCustomerRequest.class));
    }

    private void thenExpectCustomerRepositorySaveCalledOnce() {
        verify(customerRepository).save(any(CustomerDomain.class));
    }

    private void thenExpectCustomerRepositoryWhenFindByDocument() {
        doReturn(Optional.of(new CustomerDomain())).when(customerRepository).findByDocument(anyString());

    }

    private PostCustomerRequest givenValidPostCustomerRequest() {
        return new PostCustomerRequest("John Doe", "12345678999", LocalDate.now(), "1234567890",
                "john.doe@example.com");
    }

    private void thenExpectFindByDocument() {
        doReturn(Optional.empty()).when(customerRepository).findByDocument(anyString());

    }

    private void thenExpectEmptyFindById(){
        when(customerRepository.findById(anyString())).thenReturn(Optional.empty());
    }

    private void thenExpectCustomerCreationResponse() throws CustomerClientException {
        doReturn(new CustomerClientPostCustomerResponse("124")).when(customerClient).createCustomer(any(
                CustomerClientPostCustomerRequest.class));
    }

    private void unprocessableEntityExceptionWhenDocumentExists() {
        assertThrows(UnprocessableEntityException.class, () -> customerService.createCustomerService(givenValidPostCustomerRequest()));

    }

    private void notFoundExceptionWhenFindByIdEmpty() {
        assertThrows(NotFoundException.class, () -> customerService.findById("123"));

    }

    private void thenExpectCreateNewCustomer() {
        customerService.createCustomerService(givenValidPostCustomerRequest());
    }

    private void clientExceptionWhenCustomerClienteCreateCustomer() throws CustomerClientException {

        doThrow(new CustomerClientException("422.001", "CLIENT_ERROR", HttpStatus.INTERNAL_SERVER_ERROR))
                .when(customerClient).createCustomer(any(CustomerClientPostCustomerRequest.class));
    }
}
