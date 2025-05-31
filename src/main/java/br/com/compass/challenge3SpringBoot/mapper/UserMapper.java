package br.com.compass.challenge3SpringBoot.mapper;

import org.springframework.stereotype.Component;

import br.com.compass.challenge3SpringBoot.dto.UserResponseDTO;
import br.com.compass.challenge3SpringBoot.dto.UserUpdateRequestDTO;
import br.com.compass.challenge3SpringBoot.entity.Usuario;

@Component
public class UserMapper {

    public UserResponseDTO toDTO(Usuario usuario) {
    	UserResponseDTO dto = new UserResponseDTO();
    	dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setRoles(usuario.getRoles());
        return dto;
    }

    public void updateFromDto(UserUpdateRequestDTO dto, Usuario usuario) {
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
    }
}