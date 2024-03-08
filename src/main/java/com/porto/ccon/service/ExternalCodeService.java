package com.porto.ccon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalCodeService {
    private final RestTemplate restTemplate;

    public String fetchExternalCode() {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://run.mocky.io/v3/d9cc6fc0-826f-4174-9d34-619491eab252",
                null,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("external_code")) {
                return String.valueOf(responseBody.get("external_code"));
            }
        }


        return null;
    }
}
