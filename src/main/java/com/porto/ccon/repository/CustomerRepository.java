package com.porto.ccon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.porto.ccon.model.domain.CustomerDomain;

public interface CustomerRepository extends JpaRepository<CustomerDomain, String> {
    Optional<CustomerDomain> findByDocument(String document);
}
