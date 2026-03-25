package com.padelplay.server.entity;

/**
 * Tipos de proveedor de autenticación soportados.
 */
public enum AuthProvider {
    LOCAL,      // Solo password
    GOOGLE,     // Solo Google OAuth
    MIXED       // Ambos métodos vinculados
}
