package br.com.compass.challenge3SpringBoot.dto;

import java.time.LocalDateTime;

public class PasswordResetTokenResponseDTO {

    private String token;
    private LocalDateTime expiresAt;

    public PasswordResetTokenResponseDTO() {
    }

    public PasswordResetTokenResponseDTO(String token, LocalDateTime expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
