package com.padelplay.cliente.proxies;

import com.padelplay.common.dto.CompletarEvaluacionDto;
import com.padelplay.common.dto.CrearSolicitudEvaluacionDto;
import com.padelplay.common.dto.EntrenadorDisponibleDto;
import com.padelplay.common.dto.ResponderSolicitudEvaluacionDto;
import com.padelplay.common.dto.SolicitudEvaluacionDto;
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

/**
 * Proxy para comunicación con la API de evaluaciones del servidor.
 */
@Service
public class EvaluacionServiceProxy {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public EvaluacionServiceProxy(@Value("${server.api.url:http://localhost:8080}") String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.serverUrl = serverUrl;
    }

    /**
     * Obtiene la lista de entrenadores disponibles para solicitar una evaluacion.
     */
    public List<EntrenadorDisponibleDto> obtenerEntrenadoresDisponibles(String token) {
        HttpEntity<Void> entity = new HttpEntity<>(crearHeaders(token));

        ResponseEntity<List<EntrenadorDisponibleDto>> response = restTemplate.exchange(
                serverUrl + "/api/evaluaciones/entrenadores",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    /**
     * Crea una solicitud de evaluacion para el jugador autenticado.
     */
    public SolicitudEvaluacionDto crearSolicitudEvaluacion(String token, CrearSolicitudEvaluacionDto request) {
        HttpEntity<CrearSolicitudEvaluacionDto> entity = new HttpEntity<>(request, crearHeaders(token));

        ResponseEntity<SolicitudEvaluacionDto> response = restTemplate.exchange(
                serverUrl + "/api/evaluaciones/solicitudes",
                HttpMethod.POST,
                entity,
                SolicitudEvaluacionDto.class
        );

        return response.getBody();
    }

    /**
     * Obtiene las solicitudes creadas por el jugador autenticado.
     */
    public List<SolicitudEvaluacionDto> obtenerMisSolicitudes(String token) {
        HttpEntity<Void> entity = new HttpEntity<>(crearHeaders(token));

        ResponseEntity<List<SolicitudEvaluacionDto>> response = restTemplate.exchange(
                serverUrl + "/api/evaluaciones/mis-solicitudes",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    /**
     * Obtiene las solicitudes recibidas por el entrenador autenticado.
     */
    public List<SolicitudEvaluacionDto> obtenerSolicitudesRecibidas(String token) {
        HttpEntity<Void> entity = new HttpEntity<>(crearHeaders(token));

        ResponseEntity<List<SolicitudEvaluacionDto>> response = restTemplate.exchange(
                serverUrl + "/api/evaluaciones/recibidas",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    /**
     * Acepta una solicitud pendiente recibida por el entrenador autenticado.
     */
    public SolicitudEvaluacionDto aceptarSolicitud(String token,
                                                   Long solicitudId,
                                                   ResponderSolicitudEvaluacionDto request) {
        HttpEntity<ResponderSolicitudEvaluacionDto> entity = new HttpEntity<>(request, crearHeaders(token));

        ResponseEntity<SolicitudEvaluacionDto> response = restTemplate.exchange(
                serverUrl + "/api/evaluaciones/solicitudes/" + solicitudId + "/aceptar",
                HttpMethod.POST,
                entity,
                SolicitudEvaluacionDto.class
        );

        return response.getBody();
    }

    /**
     * Rechaza una solicitud pendiente recibida por el entrenador autenticado.
     */
    public SolicitudEvaluacionDto rechazarSolicitud(String token,
                                                    Long solicitudId,
                                                    ResponderSolicitudEvaluacionDto request) {
        HttpEntity<ResponderSolicitudEvaluacionDto> entity = new HttpEntity<>(request, crearHeaders(token));

        ResponseEntity<SolicitudEvaluacionDto> response = restTemplate.exchange(
                serverUrl + "/api/evaluaciones/solicitudes/" + solicitudId + "/rechazar",
                HttpMethod.POST,
                entity,
                SolicitudEvaluacionDto.class
        );

        return response.getBody();
    }

    /**
     * Completa una evaluacion aceptada y asigna el nuevo ELO.
     */
    public SolicitudEvaluacionDto completarEvaluacion(String token,
                                                      Long solicitudId,
                                                      CompletarEvaluacionDto request) {
        HttpEntity<CompletarEvaluacionDto> entity = new HttpEntity<>(request, crearHeaders(token));

        ResponseEntity<SolicitudEvaluacionDto> response = restTemplate.exchange(
                serverUrl + "/api/evaluaciones/solicitudes/" + solicitudId + "/completar",
                HttpMethod.POST,
                entity,
                SolicitudEvaluacionDto.class
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
