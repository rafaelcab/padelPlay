package com.padelplay.cliente.proxies;

import com.padelplay.common.dto.PartidoPendienteReporteDto;
import com.padelplay.common.dto.ParticipantePendienteReporteDto;
import com.padelplay.common.dto.ReporteExperienciaDto;
import com.padelplay.common.dto.ReporteExperienciaRequestDto;
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
public class ReporteExperienciaProxy {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public ReporteExperienciaProxy(@Value("${server.api.url:http://localhost:8080}") String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.serverUrl = serverUrl;
    }

    public List<PartidoPendienteReporteDto> obtenerPartidosJugados(String token) {
        HttpEntity<Void> entity = new HttpEntity<>(crearHeaders(token));

        ResponseEntity<List<PartidoPendienteReporteDto>> response = restTemplate.exchange(
                serverUrl + "/api/reportes-experiencia/partidos-jugados",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    public List<ParticipantePendienteReporteDto> obtenerParticipantesPendientes(String token, Long partidoId) {
        HttpEntity<Void> entity = new HttpEntity<>(crearHeaders(token));

        ResponseEntity<List<ParticipantePendienteReporteDto>> response = restTemplate.exchange(
                serverUrl + "/api/partidos/" + partidoId + "/reportes-experiencia/pendientes",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    public List<String> obtenerMotivosReporte() {
        ResponseEntity<List<String>> response = restTemplate.exchange(
                serverUrl + "/api/reportes-experiencia/motivos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    public ReporteExperienciaDto crearReporte(String token, Long partidoId, ReporteExperienciaRequestDto request) {
        HttpEntity<ReporteExperienciaRequestDto> entity = new HttpEntity<>(request, crearHeaders(token));

        ResponseEntity<ReporteExperienciaDto> response = restTemplate.exchange(
                serverUrl + "/api/partidos/" + partidoId + "/reportes-experiencia",
                HttpMethod.POST,
                entity,
                ReporteExperienciaDto.class
        );

        return response.getBody();
    }

    public void confirmarResultado(String token, Long partidoId) {
        HttpEntity<Void> entity = new HttpEntity<>(crearHeaders(token));
        restTemplate.exchange(
                serverUrl + "/api/partidos/" + partidoId + "/confirmar-resultado",
                HttpMethod.POST,
                entity,
                Void.class
        );
    }

    public void rechazarResultado(String token, Long partidoId) {
        HttpEntity<Void> entity = new HttpEntity<>(crearHeaders(token));
        restTemplate.exchange(
                serverUrl + "/api/partidos/" + partidoId + "/rechazar-resultado",
                HttpMethod.POST,
                entity,
                Void.class
        );
    }

    private HttpHeaders crearHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }
}
