package br.com.compass.challenge3SpringBoot.repository;

import br.com.compass.challenge3SpringBoot.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    @Query("SELECT ip.produto, SUM(ip.quantidade) as total FROM ItemPedido ip JOIN ip.pedido p WHERE p.data BETWEEN :inicio AND :fim AND p.deleted = false GROUP BY ip.produto.id ORDER BY total DESC")
    List<Object[]> findProdutosMaisVendidosPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
