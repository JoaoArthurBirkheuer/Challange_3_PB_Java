package br.com.compass.challenge3SpringBoot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.compass.challenge3SpringBoot.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByEmailAndDeletedFalse(String email);

	Page<Usuario> findByNomeContainingIgnoreCaseAndDeletedFalse(String nome, Pageable pageable);

    @Query("SELECT u, COUNT(p) as totalPedidos " +
           "FROM Usuario u JOIN u.pedidos p " +
           "WHERE u.deleted = false " +
           "GROUP BY u.id " +
           "ORDER BY totalPedidos DESC")
    Page<Object[]> findTopClientesByPedidosCount(Pageable pageable);

    @Query("SELECT u, SUM(p.total) as totalGasto " +
           "FROM Usuario u JOIN u.pedidos p " +
           "WHERE u.deleted = false AND p.deleted = false " +
           "GROUP BY u.id " +
           "ORDER BY totalGasto DESC")
    Page<Object[]> findTopClientesByTotalGasto(Pageable pageable);

    @Query("SELECT COUNT(p) > 0 FROM Pedido p WHERE p.cliente.id = :usuarioId AND p.deleted = false")
    boolean hasPedidosAtivos(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT u FROM Usuario u WHERE u.deleted = true")
    List<Usuario> findAllDeleted();
    
    Optional<Usuario>findByIdAndDeletedFalse(Long id);

	Page<Usuario> findAllByDeletedFalse(Pageable pageable);
}