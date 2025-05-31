package br.com.compass.challenge3SpringBoot.repository;
import br.com.compass.challenge3SpringBoot.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Page<Pedido> findByClienteAndDeletedFalse(Usuario cliente, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.data BETWEEN :inicio AND :fim AND p.deleted = false ORDER BY p.data DESC")
    Page<Pedido> findVendasPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.data BETWEEN :inicio AND :fim AND p.deleted = false")
    BigDecimal calcularTotalVendasPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COALESCE(SUM(ip.quantidade * ip.precoUnitario), 0) FROM ItemPedido ip JOIN ip.pedido p WHERE p.data BETWEEN :inicio AND :fim AND p.deleted = false")
    BigDecimal calcularLucroPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(p) > 0 FROM Pedido p WHERE p.cliente.id = :usuarioId AND p.deleted = false AND p.status IN ('PENDENTE', 'PROCESSANDO', 'ENVIADO')")
    boolean existsActiveOrdersByClienteId(@Param("usuarioId") Long usuarioId);
    
    Page<Pedido> findByCliente(Usuario cliente, Pageable pageable);
}