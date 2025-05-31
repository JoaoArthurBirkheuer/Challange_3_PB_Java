package br.com.compass.challenge3SpringBoot.controller;

import org.springframework.http.HttpStatus;
import br.com.compass.challenge3SpringBoot.dto.general.MessageResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.challenge3SpringBoot.dto.LoginRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.LoginResponseDTO;
import br.com.compass.challenge3SpringBoot.dto.PasswordResetRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.PasswordUpdateDTO;
import br.com.compass.challenge3SpringBoot.dto.RegisterRequestDTO;
import br.com.compass.challenge3SpringBoot.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        authService.cadastrarUsuarioCliente(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new MessageResponseDTO("Usuário registrado com sucesso."));
    }

    @PostMapping("/update-password-request")
    public ResponseEntity<MessageResponseDTO> solicitarReset(@RequestBody PasswordResetRequestDTO request) {
        authService.gerarTokenRedefinicao(request.getEmail());
        return ResponseEntity.ok(new MessageResponseDTO("Token enviado por e-mail com sucesso."));
    }

    @PostMapping("/update-password")
    public ResponseEntity<MessageResponseDTO> atualizarSenha(@Valid @RequestBody PasswordUpdateDTO dto) {
        authService.atualizarSenhaComToken(dto);
        return ResponseEntity.ok(new MessageResponseDTO("Senha atualizada com sucesso."));
    }
}