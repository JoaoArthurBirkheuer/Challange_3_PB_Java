package br.com.compass.challenge3SpringBoot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.challenge3SpringBoot.dto.RegisterRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.RegisterResponseDTO;
import br.com.compass.challenge3SpringBoot.entity.Usuario;
import br.com.compass.challenge3SpringBoot.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegisterResponseDTO> registrarNovoAdmin(@Valid @RequestBody RegisterRequestDTO request) {
        if (authService.emailExiste(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .body(new RegisterResponseDTO("E-mail já cadastrado."));
        }

        Usuario novoAdmin = new Usuario();
        novoAdmin.setEmail(request.getEmail());
        novoAdmin.setNome(request.getNome());
        novoAdmin.setSenha(request.getSenha());

        authService.cadastrarUsuarioAdmin(novoAdmin);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new RegisterResponseDTO("Administrador cadastrado com sucesso."));
    }
}
