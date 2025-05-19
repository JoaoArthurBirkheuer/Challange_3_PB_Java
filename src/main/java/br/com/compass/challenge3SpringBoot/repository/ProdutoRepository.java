package br.com.compass.challenge3SpringBoot.repository;

import br.com.compass.challenge3SpringBoot.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByAtivoTrueAndDeletedFalse(Pageable pageable);

    Page<Produto> findByNomeContainingIgnoreCaseAndAtivoTrueAndDeletedFalse(String nome, Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE p.estoque < :estoqueMinimo AND p.ativo = true AND p.deleted = false")
    List<Produto> findProdutosComEstoqueBaixo(@Param("estoqueMinimo") Integer estoqueMinimo);

    @Query("SELECT p, SUM(ip.quantidade) as totalVendido " +
           "FROM Produto p JOIN p.itensPedido ip " +
           "WHERE p.deleted = false AND ip.pedido.deleted = false " +
           "GROUP BY p.id " +
           "ORDER BY totalVendido DESC")
    Page<Object[]> findProdutosMaisVendidos(Pageable pageable);

    @Query("SELECT COUNT(ip) > 0 FROM ItemPedido ip WHERE ip.produto.id = :produtoId AND ip.pedido.deleted = false")
    boolean existsInPedidosAtivos(@Param("produtoId") Long produtoId);
}
