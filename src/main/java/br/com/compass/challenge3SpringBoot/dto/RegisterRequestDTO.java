package br.com.compass.challenge3SpringBoot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {

    @Email(message = "Email inválido")
    @NotNull(message = "Email não pode ser nulo")
    private String email;

    @NotNull(message = "Nome não pode ser nulo")
    @Size(min = 10, max = 50, message = "Nome deve ter entre 10 e 50 caracteres")
    private String nome;

    @NotBlank(message = "Senha não pode estar em branco")
    private String senha;

}
