package com.padelplay.client.proxies;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TestService {

    public String getMensajeServidor() {

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/api/test";

        return restTemplate.getForObject(url, String.class);
    }
}