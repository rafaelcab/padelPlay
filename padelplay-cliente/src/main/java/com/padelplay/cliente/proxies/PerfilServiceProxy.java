package com.padelplay.cliente.proxies;

import com.padelplay.common.dto.CertificacionDto;
import com.padelplay.common.dto.DetallesTecnicosDto;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.EvolucionEloDto;
import com.padelplay.common.dto.PartidoDto;
import com.padelplay.common.dto.PerfilEntrenadorDto;
import com.padelplay.common.dto.PerfilJugadorDto;
import com.padelplay.common.dto.SeleccionRolDto;
import com.padelplay.common.dto.SolicitudEntrenamientoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Proxy para comunicación con la API de perfiles del servidor.
 */
@Service
public class PerfilServiceProxy {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public PerfilServiceProxy(@Value("${server.api.url:http://localhost:8080}") String serverUrl) {
        this.restTemplate = new RestTemplate();
        this.serverUrl = serverUrl;
    }

    /**
     * Obtiene el estado actual de los perfiles del usuario.
     */
    public EstadoPerfilDto obtenerEstadoPerfil(String token) {
        HttpHeaders headers = crearHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<EstadoPerfilDto> response = restTemplate.exchange(
                serverUrl + "/api/perfil/estado",
                HttpMethod.GET,
                entity,
                EstadoPerfilDto.class);

        return response.getBody();
    }

    /**
     * Selecciona el rol inicial del usuario.
     */
    public EstadoPerfilDto seleccionarRol(String token, String rol) {
        HttpHeaders headers = crearHeaders(token);
        SeleccionRolDto request = new SeleccionRolDto(rol);
        HttpEntity<SeleccionRolDto> entity = new HttpEntity<>(request, headers);

        ResponseEntity<EstadoPerfilDto> response = restTemplate.exchange(
                serverUrl + "/api/perfil/seleccionar-rol",
                HttpMethod.POST,
                entity,
                EstadoPerfilDto.class);

        return response.getBody();
    }

    /**
     * Cambia el rol activo del usuario.
     */
    public EstadoPerfilDto cambiarRol(String token, String rol) {
        HttpHeaders headers = crearHeaders(token);
        SeleccionRolDto request = new SeleccionRolDto(rol);
        HttpEntity<SeleccionRolDto> entity = new HttpEntity<>(request, headers);

        ResponseEntity<EstadoPerfilDto> response = restTemplate.exchange(
                serverUrl + "/api/perfil/cambiar-rol",
                HttpMethod.POST,
                entity,
                EstadoPerfilDto.class);

        return response.getBody();
    }

    /**
     * Crea un perfil adicional.
     */
    public EstadoPerfilDto crearPerfilAdicional(String token, String rol) {
        HttpHeaders headers = crearHeaders(token);
        SeleccionRolDto request = new SeleccionRolDto(rol);
        HttpEntity<SeleccionRolDto> entity = new HttpEntity<>(request, headers);

        ResponseEntity<EstadoPerfilDto> response = restTemplate.exchange(
                serverUrl + "/api/perfil/crear-perfil",
                HttpMethod.POST,
                entity,
                EstadoPerfilDto.class);

        return response.getBody();
    }

    /**
     * Obtiene el perfil de jugador.
     */
    public PerfilJugadorDto obtenerPerfilJugador(String token) {
        HttpHeaders headers = crearHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<PerfilJugadorDto> response = restTemplate.exchange(
                serverUrl + "/api/perfil/jugador",
                HttpMethod.GET,
                entity,
                PerfilJugadorDto.class);

        return response.getBody();
    }

    /**
     * Obtiene la evolución ELO del jugador autenticado.
     */
    public List<EvolucionEloDto> obtenerEvolucionEloJugador(String token) {
        HttpHeaders headers = crearHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<EvolucionEloDto[]> response = restTemplate.exchange(
                serverUrl + "/api/perfil/jugador/evolucion-elo",
                HttpMethod.GET,
                entity,
                EvolucionEloDto[].class);

        return response.getBody() != null ? java.util.Arrays.asList(response.getBody()) : List.of();
    }

    /**
     * Actualiza el perfil de jugador.
     */
    public PerfilJugadorDto actualizarPerfilJugador(String token, PerfilJugadorDto perfil) {
        HttpHeaders headers = crearHeaders(token);
        HttpEntity<PerfilJugadorDto> entity = new HttpEntity<>(perfil, headers);

        ResponseEntity<PerfilJugadorDto> response = restTemplate.exchange(
                serverUrl + "/api/perfil/jugador",
                HttpMethod.PUT,
                entity,
                PerfilJugadorDto.class);

        return response.getBody();
    }

    /**
     * Actualiza los detalles técnicos del jugador.
     */
    public DetallesTecnicosDto actualizarDetallesTecnicos(String token, DetallesTecnicosDto detalles) {
        HttpHeaders headers = crearHeaders(token);
        HttpEntity<DetallesTecnicosDto> entity = new HttpEntity<>(detalles, headers);

        ResponseEntity<DetallesTecnicosDto> response = restTemplate.exchange(
                serverUrl + "/api/perfil/jugador/detalles-tecnicos",
                HttpMethod.PUT,
                entity,
                DetallesTecnicosDto.class);

        return response.getBody();
    }

    /**
     * Obtiene las opciones técnicas disponibles.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> obtenerOpcionesTecnicas() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                serverUrl + "/api/perfil/opciones-tecnicas",
                Map.class);

        return response.getBody();
    }

    // === MÉTODOS PARA ENTRENADOR ===

    /**
     * Obtiene el perfil de entrenador.
     */
    public PerfilEntrenadorDto obtenerPerfilEntrenador(String token) {
        HttpHeaders headers = crearHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<PerfilEntrenadorDto> response = restTemplate.exchange(
                serverUrl + "/api/perfil/entrenador",
                HttpMethod.GET,
                entity,
                PerfilEntrenadorDto.class);

        return response.getBody();
    }

    /**
     * Obtiene la lista pública de entrenadores.
     */
    public List<PerfilEntrenadorDto> obtenerEntrenadoresPublicos() {
        ResponseEntity<PerfilEntrenadorDto[]> response = restTemplate.getForEntity(
                serverUrl + "/api/perfil/entrenadores",
                PerfilEntrenadorDto[].class);

        return response.getBody() != null ? java.util.Arrays.asList(response.getBody()) : List.of();
    }

    /**
     * Actualiza el perfil de entrenador.
     */
    public PerfilEntrenadorDto actualizarPerfilEntrenador(String token, PerfilEntrenadorDto perfil) {
        HttpHeaders headers = crearHeaders(token);
        HttpEntity<PerfilEntrenadorDto> entity = new HttpEntity<>(perfil, headers);

        ResponseEntity<PerfilEntrenadorDto> response = restTemplate.exchange(
                serverUrl + "/api/perfil/entrenador",
                HttpMethod.PUT,
                entity,
                PerfilEntrenadorDto.class);

        return response.getBody();
    }

    /**
     * Añade una certificación al perfil del entrenador.
     */
    public PerfilEntrenadorDto agregarCertificacion(String token, CertificacionDto certificacion) {
        HttpHeaders headers = crearHeaders(token);
        HttpEntity<CertificacionDto> entity = new HttpEntity<>(certificacion, headers);

        ResponseEntity<PerfilEntrenadorDto> response = restTemplate.exchange(
                serverUrl + "/api/perfil/entrenador/certificaciones",
                HttpMethod.POST,
                entity,
                PerfilEntrenadorDto.class);

        return response.getBody();
    }

    /**
     * Elimina una certificación del perfil del entrenador.
     */
    public PerfilEntrenadorDto eliminarCertificacion(String token, Long certificacionId) {
        HttpHeaders headers = crearHeaders(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<PerfilEntrenadorDto> response = restTemplate.exchange(
                serverUrl + "/api/perfil/entrenador/certificaciones/" + certificacionId,
                HttpMethod.DELETE,
                entity,
                PerfilEntrenadorDto.class);

        return response.getBody();
    }

    /**
     * Actualiza todas las certificaciones del entrenador.
     */
    public PerfilEntrenadorDto actualizarCertificaciones(String token, List<CertificacionDto> certificaciones) {
        HttpHeaders headers = crearHeaders(token);
        HttpEntity<List<CertificacionDto>> entity = new HttpEntity<>(certificaciones, headers);

        ResponseEntity<PerfilEntrenadorDto> response = restTemplate.exchange(
                serverUrl + "/api/perfil/entrenador/certificaciones",
                HttpMethod.PUT,
                entity,
                PerfilEntrenadorDto.class);

        return response.getBody();
    }

    /**
     * Obtiene las opciones disponibles para entrenadores.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> obtenerOpcionesEntrenador() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                serverUrl + "/api/perfil/opciones-entrenador",
                Map.class);

        return response.getBody();
    }

    /**
     * Crea una solicitud de entrenamiento para un entrenador específico.
     */
    public SolicitudEntrenamientoDto crearSolicitudEntrenamiento(String token, Long entrenadorId, String mensaje) {
        HttpHeaders headers = crearHeaders(token);
        SolicitudEntrenamientoDto request = new SolicitudEntrenamientoDto();
        request.setMensaje(mensaje);

        HttpEntity<SolicitudEntrenamientoDto> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<SolicitudEntrenamientoDto> response = restTemplate.exchange(
                    serverUrl + "/api/perfil/entrenador/" + entrenadorId + "/solicitar",
                    HttpMethod.POST,
                    entity,
                    SolicitudEntrenamientoDto.class);
            return response.getBody();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            try {
                SolicitudEntrenamientoDto errorDto = e.getResponseBodyAs(SolicitudEntrenamientoDto.class);
                if (errorDto != null) {
                    return errorDto;
                }
            } catch (Exception ex) {
                // Ignore and fall through
            }
            return new SolicitudEntrenamientoDto("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    /**
     * Obtiene los partidos creados por los alumnos del entrenador autenticado.
     */
    public List<PartidoDto> obtenerPartidosDeAlumnos(String token) {
        HttpHeaders headers = crearHeaders(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<PartidoDto[]> response = restTemplate.exchange(
                    serverUrl + "/api/partidos/alumnos",
                    HttpMethod.GET,
                    entity,
                    PartidoDto[].class);

            if (response.getBody() != null) {
                return java.util.Arrays.asList(response.getBody());
            }
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error al obtener partidos de alumnos: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Obtiene los partidos de los alumnos por ID de usuario del entrenador (sin
     * necesidad de token).
     */
    public List<PartidoDto> obtenerPartidosDeAlumnosPorId(Long entrenadorUsuarioId) {
        ResponseEntity<PartidoDto[]> response = restTemplate.getForEntity(
                serverUrl + "/api/partidos/entrenador/" + entrenadorUsuarioId + "/alumnos",
                PartidoDto[].class);
        if (response.getBody() != null) {
            return new java.util.ArrayList<>(java.util.Arrays.asList(response.getBody()));
        }
        return new java.util.ArrayList<>();
    }

    private HttpHeaders crearHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }
}
