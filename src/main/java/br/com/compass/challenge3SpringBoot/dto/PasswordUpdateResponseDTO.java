package br.com.compass.challenge3SpringBoot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordUpdateResponseDTO {
    private String token;
}