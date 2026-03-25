package com.padelplay.server.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Usuario con soporte para registro manual y autenticación Google OAuth.
 * Combina funcionalidades de registro tradicional con login social.
 * Soporta multi-perfil (Jugador/Entrenador).
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String nombre;

    private String pictureUrl;

    // Password para autenticación local (obligatorio para registro manual, null si solo usa Google)
    private String password;

    // ID único de Google (sub claim del ID token) - null para registro manual
    private String googleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider = AuthProvider.LOCAL;

    // === MULTI-PERFIL ===
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoRol rolActivo;

    @Column(nullable = false)
    private boolean tienePerfilJugador = false;

    @Column(nullable = false)
    private boolean tienePerfilEntrenador = false;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerfilJugador> perfilesJugador = new ArrayList<>();

    // Constructores
    
    public Usuario() {}

    // Constructor para registro manual (conserva compatibilidad con main)
    public Usuario(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.authProvider = AuthProvider.LOCAL;
    }

    // Constructor para autenticación Google
    public Usuario(String email, String nombre, String pictureUrl, String googleId, AuthProvider authProvider) {
        this.email = email;
        this.nombre = nombre;
        this.pictureUrl = pictureUrl;
        this.googleId = googleId;
        this.authProvider = authProvider;
    }

    // Getters y Setters básicos (mantiene compatibilidad con main)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() { 
        return nombre; 
    }

    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }

    public String getEmail() { 
        return email; 
    }

    public void setEmail(String email) { 
        this.email = email; 
    }

    public String getPassword() { 
        return password; 
    }

    public void setPassword(String password) { 
        this.password = password; 
    }

    // Getters y Setters adicionales para Google Auth

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    /**
     * Vincula una cuenta de Google a un usuario registrado manualmente.
     */
    public void vincularGoogle(String googleId, String pictureUrl) {
        this.googleId = googleId;
        if (pictureUrl != null) {
            this.pictureUrl = pictureUrl;
        }
        if (this.authProvider == AuthProvider.LOCAL) {
            this.authProvider = AuthProvider.MIXED;
        }
    }

    // === MULTI-PERFIL: Getters y Setters ===

    public TipoRol getRolActivo() {
        return rolActivo;
    }

    public void setRolActivo(TipoRol rolActivo) {
        this.rolActivo = rolActivo;
    }

    public boolean isTienePerfilJugador() {
        return tienePerfilJugador;
    }

    public void setTienePerfilJugador(boolean tienePerfilJugador) {
        this.tienePerfilJugador = tienePerfilJugador;
    }

    public boolean isTienePerfilEntrenador() {
        return tienePerfilEntrenador;
    }

    public void setTienePerfilEntrenador(boolean tienePerfilEntrenador) {
        this.tienePerfilEntrenador = tienePerfilEntrenador;
    }

    public List<PerfilJugador> getPerfilesJugador() {
        return perfilesJugador;
    }

    public void setPerfilesJugador(List<PerfilJugador> perfilesJugador) {
        this.perfilesJugador = perfilesJugador;
    }

    /**
     * Verifica si el usuario necesita seleccionar un perfil inicial.
     */
    public boolean requiereSeleccionPerfil() {
        return !tienePerfilJugador && !tienePerfilEntrenador;
    }

    /**
     * Verifica si el usuario puede cambiar entre perfiles.
     */
    public boolean puedeAlternarPerfil() {
        return tienePerfilJugador && tienePerfilEntrenador;
    }

    /**
     * Activa el perfil de jugador.
     */
    public void activarPerfilJugador() {
        this.tienePerfilJugador = true;
        this.rolActivo = TipoRol.JUGADOR;
    }

    /**
     * Activa el perfil de entrenador.
     */
    public void activarPerfilEntrenador() {
        this.tienePerfilEntrenador = true;
        this.rolActivo = TipoRol.ENTRENADOR;
    }
}
