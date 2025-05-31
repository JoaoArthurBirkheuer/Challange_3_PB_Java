package br.com.compass.challenge3SpringBoot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.challenge3SpringBoot.dto.RegisterRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.general.MessageResponseDTO;
import br.com.compass.challenge3SpringBoot.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> registrarNovoAdmin(@Valid @RequestBody RegisterRequestDTO request) {
        authService.cadastrarUsuarioAdmin(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new MessageResponseDTO("Administrador cadastrado com sucesso."));
    }
}