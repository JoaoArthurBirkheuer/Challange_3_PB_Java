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
import org.springframework.transaction.annotation.Transactional;

import br.com.compass.challenge3SpringBoot.dto.LoginRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.LoginResponseDTO;
import br.com.compass.challenge3SpringBoot.dto.PasswordUpdateDTO;
import br.com.compass.challenge3SpringBoot.dto.RegisterRequestDTO; 
import br.com.compass.challenge3SpringBoot.entity.Carrinho;
import br.com.compass.challenge3SpringBoot.entity.PasswordResetToken;
import br.com.compass.challenge3SpringBoot.entity.Role;
import br.com.compass.challenge3SpringBoot.entity.Usuario;
import br.com.compass.challenge3SpringBoot.exception.BusinessRuleException;
import br.com.compass.challenge3SpringBoot.exception.EmailJaCadastradoException;
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
    private final EmailService emailService;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getSenha()
            )
        );

        User user = (User) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findByEmailAndDeletedFalse(user.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        String token = jwtTokenUtil.generateToken(usuario.getId(), user.getUsername(), user.getAuthorities());

        return new LoginResponseDTO(token);
    }

    public boolean emailExiste(String email) {
        return usuarioRepository.findByEmailAndDeletedFalse(email).isPresent();
    }

    @Transactional
    public Usuario cadastrarUsuarioCliente(RegisterRequestDTO request) {
        if (emailExiste(request.getEmail())) {
            throw new EmailJaCadastradoException("Email já cadastrado: " + request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setNome(request.getNome());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.addRole(Role.ROLE_CLIENTE);

        Carrinho carrinho = new Carrinho();
        carrinho.setUsuario(usuario);
        usuario.setCarrinho(carrinho);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario cadastrarUsuarioAdmin(RegisterRequestDTO request) { 
        if (emailExiste(request.getEmail())) {
            throw new EmailJaCadastradoException("E-mail já cadastrado: " + request.getEmail());
        }

        Usuario admin = new Usuario();
        admin.setEmail(request.getEmail());
        admin.setNome(request.getNome());
        admin.setSenha(passwordEncoder.encode(request.getSenha()));
        admin.getRoles().clear();
        admin.addRole(Role.ROLE_ADMIN);
        return usuarioRepository.save(admin);
    }

    @Transactional
    public void gerarTokenRedefinicao(String email) {
        Usuario usuario = usuarioRepository.findByEmailAndDeletedFalse(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        if (tokenRepository.existsValidTokenByUsuarioId(usuario.getId(), LocalDateTime.now())) {
            throw new BusinessRuleException("Já existe uma solicitação de redefinição de senha ativa para este usuário. Por favor, aguarde ou use o token existente.");
        }

        PasswordResetToken token = PasswordResetToken.builder()
            .usuario(usuario)
            .expiresAt(LocalDateTime.now().plusMinutes(30))
            .build();

        PasswordResetToken tokenSalvo = tokenRepository.save(token);

        emailService.sendPasswordResetEmail(usuario.getEmail(), tokenSalvo.getToken().toString());
    }

    @Transactional
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
}