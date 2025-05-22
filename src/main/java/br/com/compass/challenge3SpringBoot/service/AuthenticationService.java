package br.com.compass.challenge3SpringBoot.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.compass.challenge3SpringBoot.dto.LoginRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.LoginResponseDTO;
import br.com.compass.challenge3SpringBoot.dto.PasswordUpdateDTO;
import br.com.compass.challenge3SpringBoot.entity.Carrinho;
import br.com.compass.challenge3SpringBoot.entity.PasswordResetToken;
import br.com.compass.challenge3SpringBoot.entity.Role;
import br.com.compass.challenge3SpringBoot.entity.Usuario;
import br.com.compass.challenge3SpringBoot.exception.PasswordResetTokenInvalidException;
import br.com.compass.challenge3SpringBoot.exception.PasswordUpdateException;
import br.com.compass.challenge3SpringBoot.repository.PasswordResetTokenRepository;
import br.com.compass.challenge3SpringBoot.repository.UsuarioRepository;
import br.com.compass.challenge3SpringBoot.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(user.getUsername(), user.getAuthorities());
        return new LoginResponseDTO(token);
    }

    public boolean emailExiste(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    public Usuario cadastrarUsuarioCliente(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.addRole(Role.ROLE_CLIENTE);

        Carrinho carrinho = new Carrinho();
        carrinho.setUsuario(usuario);
        usuario.setCarrinho(carrinho);

        return usuarioRepository.save(usuario);
    }
    
    public Usuario cadastrarUsuarioAdmin(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.getRoles().clear(); 
        usuario.addRole(Role.ROLE_ADMIN);
        return usuarioRepository.save(usuario);
    }
    
    public PasswordResetToken gerarTokenRedefinicao(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        PasswordResetToken token = PasswordResetToken.builder()
            .usuario(usuario)
            .expiresAt(LocalDateTime.now().plusMinutes(30))
            .build();

        return tokenRepository.save(token);
    }

    public void atualizarSenhaComToken(PasswordUpdateDTO dto) {
        if (dto.getSenhaAtual() == null || dto.getNovaSenha() == null) {
            throw new PasswordUpdateException("Senha atual e nova senha são obrigatórias.");
        }

        if (dto.getSenhaAtual().equals(dto.getNovaSenha())) {
            throw new PasswordUpdateException("A nova senha deve ser diferente da atual.");
        }

        PasswordResetToken token = tokenRepository.findById(UUID.fromString(dto.getToken()))
            .orElseThrow(() -> new PasswordResetTokenInvalidException("Token inválido."));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new PasswordResetTokenInvalidException("Token expirado.");
        }

        Usuario usuario = token.getUsuario();

        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            throw new PasswordUpdateException("Senha atual incorreta.");
        }

        usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        usuarioRepository.save(usuario);

        tokenRepository.delete(token);
    }

    // No futuro incluir aqui:
    // - reset de senha
    // - confirmação de e-mail
    // - envio de link por email
}
