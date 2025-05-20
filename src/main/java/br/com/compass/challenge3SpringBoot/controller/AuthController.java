package br.com.compass.challenge3SpringBoot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.challenge3SpringBoot.dto.LoginRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.LoginResponseDTO;
import br.com.compass.challenge3SpringBoot.dto.RegisterRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.RegisterResponseDTO;
import br.com.compass.challenge3SpringBoot.entity.Usuario;
import br.com.compass.challenge3SpringBoot.exception.EmailJaCadastradoException;
// import br.com.compass.challenge3SpringBoot.security.JwtTokenUtil;
import br.com.compass.challenge3SpringBoot.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    // private final AuthenticationManager authenticationManager;
    // private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationService authService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequest) {
        return authService.login(loginRequest);
    }
    
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        if (authService.emailExiste(request.getEmail())) {
            throw new EmailJaCadastradoException("Email já cadastrado: " + request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setNome(request.getNome());
        usuario.setSenha(request.getSenha());

        authService.cadastrarUsuarioCliente(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new RegisterResponseDTO("Usuário registrado com sucesso!"));
    }

}
