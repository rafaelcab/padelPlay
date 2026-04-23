package com.padelplay.cliente.proxies;

import com.padelplay.common.dto.AmigoPerfilDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AmigosServiceProxy {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public AmigosServiceProxy(@Value("${server.api.url:http://localhost:8080}") String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.serverUrl = serverUrl;
    }

    public List<AmigoPerfilDto> listarAmigos(String token) {
        HttpEntity<Void> entity = new HttpEntity<>(crearHeaders(token));

        ResponseEntity<List<AmigoPerfilDto>> response = restTemplate.exchange(
                serverUrl + "/api/amigos",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    public AmigoPerfilDto obtenerAmigo(String token, Long usuarioObjetivoId) {
        HttpEntity<Void> entity = new HttpEntity<>(crearHeaders(token));

        ResponseEntity<AmigoPerfilDto> response = restTemplate.exchange(
                serverUrl + "/api/amigos/" + usuarioObjetivoId,
                HttpMethod.GET,
                entity,
                AmigoPerfilDto.class
        );

        return response.getBody();
    }

    public void seguir(String token, Long usuarioObjetivoId) {
        HttpEntity<Void> entity = new HttpEntity<>(crearHeaders(token));
        restTemplate.exchange(
                serverUrl + "/api/amigos/" + usuarioObjetivoId + "/seguir",
                HttpMethod.POST,
                entity,
                Void.class
        );
    }

    private HttpHeaders crearHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}