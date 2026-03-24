package com.padelplay.server.entity;

import jakarta.persistence.*;

/**
 * Entidad Usuario con soporte para autenticación local y Google OAuth.
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

    // Password para autenticación local (null si solo usa Google)
    private String password;

    // ID único de Google (sub claim del ID token)
    private String googleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider = AuthProvider.LOCAL;

    public Usuario() {
    }

    public Usuario(String email, String nombre, String pictureUrl, String googleId, AuthProvider authProvider) {
        this.email = email;
        this.nombre = nombre;
        this.pictureUrl = pictureUrl;
        this.googleId = googleId;
        this.authProvider = authProvider;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
     * Vincula una cuenta de Google a un usuario existente.
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
}
