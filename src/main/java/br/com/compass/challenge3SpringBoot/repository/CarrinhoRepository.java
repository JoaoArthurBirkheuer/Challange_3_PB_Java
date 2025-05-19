package br.com.compass.challenge3SpringBoot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.compass.challenge3SpringBoot.entity.Carrinho;
import br.com.compass.challenge3SpringBoot.entity.Usuario;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    Optional<Carrinho> findByUsuarioAndDeletedFalse(Usuario usuario);

    @Query("SELECT COUNT(c) > 0 FROM Carrinho c WHERE c.usuario.id = :usuarioId AND c.deleted = false")
    boolean existsByUsuarioId(@Param("usuarioId") Long usuarioId);
}
