package com.padelplay.cliente.proxies;

import com.padelplay.common.dto.RegistrarResultadoPartidoRequestDto;
import com.padelplay.common.dto.ResultadoPartidoDto;
import com.padelplay.common.dto.ResultadoPartidoGestionCreadorDto;
import com.padelplay.common.dto.ResultadoPartidoPendienteValidacionDto;
import com.padelplay.common.dto.ValidarResultadoPartidoRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ResultadoPartidoProxy {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public ResultadoPartidoProxy(@Value("${server.api.url:http://localhost:8080}") String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.serverUrl = serverUrl;
    }

    public ResultadoPartidoDto obtenerResultado(String token, Long partidoId) {
        HttpEntity<Void> entity = new HttpEntity<>(crearHeaders(token));

        ResponseEntity<ResultadoPartidoDto> response = restTemplate.exchange(
                serverUrl + "/api/partidos/" + partidoId + "/resultado",
                HttpMethod.GET,
                entity,
                ResultadoPartidoDto.class
        );

        return response.getBody();
    }

    public ResultadoPartidoDto registrarResultado(String token,
                                                  Long partidoId,
                                                  RegistrarResultadoPartidoRequestDto request) {
        HttpEntity<RegistrarResultadoPartidoRequestDto> entity = new HttpEntity<>(request, crearHeaders(token));

        ResponseEntity<ResultadoPartidoDto> response = restTemplate.exchange(
                serverUrl + "/api/partidos/" + partidoId + "/resultado",
                HttpMethod.POST,
                entity,
                ResultadoPartidoDto.class
        );

        return response.getBody();
    }

    public ResultadoPartidoDto validarResultado(String token,
                                                Long partidoId,
                                                ValidarResultadoPartidoRequestDto request) {
        HttpEntity<ValidarResultadoPartidoRequestDto> entity = new HttpEntity<>(request, crearHeaders(token));

        ResponseEntity<ResultadoPartidoDto> response = restTemplate.exchange(
                serverUrl + "/api/partidos/" + partidoId + "/resultado/validaciones",
                HttpMethod.POST,
                entity,
                ResultadoPartidoDto.class
        );

        return response.getBody();
    }

    public List<ResultadoPartidoPendienteValidacionDto> obtenerPendientesValidacion(String token) {
        HttpEntity<Void> entity = new HttpEntity<>(crearHeaders(token));

        ResponseEntity<List<ResultadoPartidoPendienteValidacionDto>> response = restTemplate.exchange(
                serverUrl + "/api/resultados-partido/pendientes-validacion",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    public List<ResultadoPartidoGestionCreadorDto> obtenerResultadosGestionCreador(String token) {
        HttpEntity<Void> entity = new HttpEntity<>(crearHeaders(token));

        ResponseEntity<List<ResultadoPartidoGestionCreadorDto>> response = restTemplate.exchange(
                serverUrl + "/api/resultados-partido/mis-registros",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    private HttpHeaders crearHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }
}
