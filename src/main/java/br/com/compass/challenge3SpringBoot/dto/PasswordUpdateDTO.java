package br.com.compass.challenge3SpringBoot.dto;

import jakarta.validation.constraints.NotBlank;

public class PasswordUpdateDTO {

    @NotBlank(message = "O token é obrigatório.")
    private String token;

    @NotBlank(message = "A senha atual é obrigatória.")
    private String senhaAtual;

    @NotBlank(message = "A nova senha é obrigatória.")
    private String novaSenha;

    public PasswordUpdateDTO() {
    }

    public PasswordUpdateDTO(String token, String senhaAtual, String novaSenha) {
        this.token = token;
        this.senhaAtual = senhaAtual;
        this.novaSenha = novaSenha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }
}
