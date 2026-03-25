package com.padelplay.server.session;

import java.time.Instant;

public class SessionData {

    private Long usuarioId;
    private String nombre;
    private String email;
    private Instant createdAt;
    private Instant expiresAt;

    public SessionData(Long usuarioId, String nombre, String email) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.email = email;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}