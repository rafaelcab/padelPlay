package com.padelplay.cliente.proxies;

import com.padelplay.common.dto.CertificacionDto;
import com.padelplay.common.dto.DetallesTecnicosDto;
import com.padelplay.common.dto.EstadoPerfilDto;
import com.padelplay.common.dto.PerfilEntrenadorDto;
import com.padelplay.common.dto.PerfilJugadorDto;
import com.padelplay.common.dto.SeleccionRolDto;
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
                EstadoPerfilDto.class
        );
        
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
                EstadoPerfilDto.class
        );
        
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
                EstadoPerfilDto.class
        );
        
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
                EstadoPerfilDto.class
        );
        
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
                PerfilJugadorDto.class
        );
        
        return response.getBody();
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
                PerfilJugadorDto.class
        );
        
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
                DetallesTecnicosDto.class
        );
        
        return response.getBody();
    }

    /**
     * Obtiene las opciones técnicas disponibles.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> obtenerOpcionesTecnicas() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                serverUrl + "/api/perfil/opciones-tecnicas",
                Map.class
        );
        
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
                PerfilEntrenadorDto.class
        );
        
        return response.getBody();
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
                PerfilEntrenadorDto.class
        );
        
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
                PerfilEntrenadorDto.class
        );
        
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
                PerfilEntrenadorDto.class
        );
        
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
                PerfilEntrenadorDto.class
        );
        
        return response.getBody();
    }

    /**
     * Obtiene las opciones disponibles para entrenadores.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> obtenerOpcionesEntrenador() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                serverUrl + "/api/perfil/opciones-entrenador",
                Map.class
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
