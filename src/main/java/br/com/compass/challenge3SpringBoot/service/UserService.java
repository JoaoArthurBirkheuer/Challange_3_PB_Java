package br.com.compass.challenge3SpringBoot.service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication; 
import org.springframework.security.core.context.SecurityContextHolder; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.compass.challenge3SpringBoot.dto.UserResponseDTO;
import br.com.compass.challenge3SpringBoot.dto.UserUpdateRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.general.PageResponseDTO;
import br.com.compass.challenge3SpringBoot.entity.Role;
import br.com.compass.challenge3SpringBoot.entity.Usuario;
import br.com.compass.challenge3SpringBoot.exception.EmailJaCadastradoException;
import br.com.compass.challenge3SpringBoot.exception.ResourceNotFoundException;
import br.com.compass.challenge3SpringBoot.exception.UserWithActiveOrdersException;
import br.com.compass.challenge3SpringBoot.mapper.UserMapper;
import br.com.compass.challenge3SpringBoot.repository.PedidoRepository;
import br.com.compass.challenge3SpringBoot.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final UserMapper usuarioMapper;

    public PageResponseDTO<UserResponseDTO> listarTodos(boolean incluirDeletados, Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                              .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Role.ROLE_ADMIN.name()));

        if (incluirDeletados && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso negado: apenas administradores podem incluir usuários deletados.");
        }

        Page<Usuario> pagina;
        if (incluirDeletados) {
            pagina = usuarioRepository.findAll(pageable);
        } else {
            pagina = usuarioRepository.findAllByDeletedFalse(pageable);
        }

        return PageResponseDTO.<UserResponseDTO>builder()
                .content(pagina.getContent().stream()
                        .map(usuarioMapper::toDTO)
                        .collect(Collectors.toList()))
                .page(pagina.getNumber())
                .size(pagina.getSize())
                .totalElements(pagina.getTotalElements())
                .totalPages(pagina.getTotalPages())
                .first(pagina.isFirst())
                .last(pagina.isLast())
                .build();
    }

    public UserResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    public UserResponseDTO atualizar(Long id, UserUpdateRequestDTO dto) {

        Usuario usuario = usuarioRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        validarEmailUnico(usuario, dto.getEmail());

        usuarioMapper.updateFromDto(dto, usuario);
        usuarioRepository.save(usuario);

        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = usuarioRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        validarPedidosAtivos(usuario.getId());

        usuario.setDeleted(true);
        usuario.setDeletedAt(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    private void validarEmailUnico(Usuario usuario, String novoEmail) {
        if (!usuario.getEmail().equals(novoEmail) &&
            usuarioRepository.findByEmailAndDeletedFalse(novoEmail).isPresent()) {
            throw new EmailJaCadastradoException("E-mail já está em uso por outro usuário");
        }
    }

    private void validarPedidosAtivos(Long usuarioId) {
        if (pedidoRepository.existsActiveOrdersByClienteId(usuarioId)) {
            throw new UserWithActiveOrdersException("Usuário possui pedidos em andamento e não pode ser deletado.");
        }
    }

    public java.util.Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmailAndDeletedFalse(email);
    }
}