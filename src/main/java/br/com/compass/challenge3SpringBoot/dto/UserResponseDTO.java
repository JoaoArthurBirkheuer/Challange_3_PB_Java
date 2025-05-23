package br.com.compass.challenge3SpringBoot.dto;

import br.com.compass.challenge3SpringBoot.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private Set<Role> roles;
}
