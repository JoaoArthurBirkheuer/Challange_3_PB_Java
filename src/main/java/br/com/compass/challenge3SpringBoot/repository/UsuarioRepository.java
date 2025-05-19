package br.com.compass.challenge3SpringBoot.repository;

import br.com.compass.challenge3SpringBoot.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    Page<Usuario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

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
}