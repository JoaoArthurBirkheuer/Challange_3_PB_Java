package br.com.compass.challenge3SpringBoot.security;

import br.com.compass.challenge3SpringBoot.entity.Usuario;
import br.com.compass.challenge3SpringBoot.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UsuarioRepository usuarioRepository;

    public UserPrincipal getAuthenticatedUserPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal) {
            return (UserPrincipal) principal;
        }
        throw new UsernameNotFoundException("Principal de usuário não é do tipo esperado (UserPrincipal).");
    }

    public Long getAuthenticatedUserId() {
        return getAuthenticatedUserPrincipal().getId();
    }

    public Usuario getAuthenticatedUserEntity() {
        Long userId = getAuthenticatedUserId();
        return usuarioRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado no banco de dados."));
    }
}
