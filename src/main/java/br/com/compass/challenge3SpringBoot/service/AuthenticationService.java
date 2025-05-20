package br.com.compass.challenge3SpringBoot.service;

import br.com.compass.challenge3SpringBoot.dto.LoginRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.LoginResponseDTO;
import br.com.compass.challenge3SpringBoot.entity.Usuario;
import br.com.compass.challenge3SpringBoot.repository.UsuarioRepository;
import br.com.compass.challenge3SpringBoot.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(user.getUsername());
        return new LoginResponseDTO(token);
    }

    public boolean emailExiste(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    public Usuario cadastrarUsuarioCliente(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        // Defina a role padrão, se necessário
        usuario.addRole(br.com.compass.challenge3SpringBoot.entity.Role.ROLE_CLIENTE);
        return usuarioRepository.save(usuario);
    }

    // No futuro incluir aqui:
    // - reset de senha
    // - confirmação de e-mail
    // - envio de link por email
}
