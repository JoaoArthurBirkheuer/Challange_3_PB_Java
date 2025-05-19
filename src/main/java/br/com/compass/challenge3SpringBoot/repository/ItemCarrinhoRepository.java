package br.com.compass.challenge3SpringBoot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.compass.challenge3SpringBoot.entity.Carrinho;
import br.com.compass.challenge3SpringBoot.entity.ItemCarrinho;
import br.com.compass.challenge3SpringBoot.entity.Produto;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {
    Optional<ItemCarrinho> findByCarrinhoAndProdutoAndDeletedFalse(Carrinho carrinho, Produto produto);

    List<ItemCarrinho> findByCarrinhoAndDeletedFalse(Carrinho carrinho);
}
