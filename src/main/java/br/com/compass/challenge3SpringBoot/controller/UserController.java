package br.com.compass.challenge3SpringBoot.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.challenge3SpringBoot.dto.UserResponseDTO;
import br.com.compass.challenge3SpringBoot.dto.UserUpdateRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.general.PageResponseDTO;
import br.com.compass.challenge3SpringBoot.entity.Usuario;
import br.com.compass.challenge3SpringBoot.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponseDTO<UserResponseDTO>> listarTodos(
        @RequestParam(required = false) Boolean incluirDeletados,
        Pageable pageable) {
        
        if (Boolean.TRUE.equals(incluirDeletados)) {
            return ResponseEntity.ok(usuarioService.listarTodosIncluindoDeletados(pageable));
        }
        return ResponseEntity.ok(usuarioService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and #id == authentication.principal.id)")
    public ResponseEntity<UserResponseDTO> atualizarDadosUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((Usuario) authentication.getPrincipal()).getId();
        
        if (!authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")) 
                && !id.equals(userId)) {
            throw new AccessDeniedException("Acesso negado: você só pode atualizar seu próprio perfil");
        }

        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and #id == authentication.principal.id)")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((Usuario) authentication.getPrincipal()).getId();
        
        if (!authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")) 
                && !id.equals(userId)) {
            throw new AccessDeniedException("Acesso negado: você só pode deletar seu próprio perfil");
        }

        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
