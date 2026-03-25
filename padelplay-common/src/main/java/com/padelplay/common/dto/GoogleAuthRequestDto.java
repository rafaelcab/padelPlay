package com.padelplay.common.dto;

/**
 * DTO para recibir el ID Token de Google desde el frontend.
 */
public class GoogleAuthRequestDto {

    private String credential;

    public GoogleAuthRequestDto() {
    }

    public GoogleAuthRequestDto(String credential) {
        this.credential = credential;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }
}
