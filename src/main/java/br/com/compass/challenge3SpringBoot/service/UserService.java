package br.com.compass.challenge3SpringBoot.service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.compass.challenge3SpringBoot.dto.UserResponseDTO;
import br.com.compass.challenge3SpringBoot.dto.UserUpdateRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.general.PageResponseDTO;
import br.com.compass.challenge3SpringBoot.entity.Role;
import br.com.compass.challenge3SpringBoot.entity.StatusPedido;
import br.com.compass.challenge3SpringBoot.entity.Usuario;
import br.com.compass.challenge3SpringBoot.exception.EmailJaCadastradoException;
import br.com.compass.challenge3SpringBoot.exception.EntityNotFoundException;
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

    public PageResponseDTO<UserResponseDTO> listarTodos(Pageable pageable) {
        Page<Usuario> pagina = usuarioRepository.findAllByDeletedFalse(pageable);

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

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponseDTO<UserResponseDTO> listarTodosIncluindoDeletados(Pageable pageable) {
        Page<Usuario> pagina = usuarioRepository.findAll(pageable);

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
        validarPermissaoAcessoAoUsuario(id);

        Usuario usuario = usuarioRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        validarEmailUnico(usuario, dto.getEmail());

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuarioRepository.save(usuario);

        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    public void deletar(Long id) {
        validarPermissaoAcessoAoUsuario(id);
        Usuario usuario = usuarioRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        validarPedidosAtivos(usuario.getId());

        usuario.setDeleted(true);
        usuario.setDeletedAt(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    private void validarPermissaoAcessoAoUsuario(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario autenticado = usuarioRepository.findByEmailAndDeletedFalse(email)
            .orElseThrow(() -> new EntityNotFoundException("Usuário autenticado não encontrado"));

        boolean ehAdmin = autenticado.getRoles().contains(Role.ROLE_ADMIN);
        boolean ehDonoDoRecurso = autenticado.getId().equals(id);

        if (!ehAdmin && !ehDonoDoRecurso) {
            throw new AccessDeniedException("Acesso negado: você só pode acessar seus próprios dados.");
        }
    }

    private void validarEmailUnico(Usuario usuario, String novoEmail) {
        if (!usuario.getEmail().equals(novoEmail) &&
            usuarioRepository.findByEmailAndDeletedFalse(novoEmail).isPresent()) {
            throw new EmailJaCadastradoException("E-mail já está em uso por outro usuário");
        }
    }

    private void validarPedidosAtivos(Long usuarioId) {
        if (pedidoRepository.existsByClienteIdAndStatusNotAndDeletedFalse(usuarioId, StatusPedido.ENTREGUE)) {
            throw new UserWithActiveOrdersException("Usuário possui pedidos em andamento e não pode ser deletado.");
        }
    }
    
    public java.util.Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmailAndDeletedFalse(email);
    }
}
