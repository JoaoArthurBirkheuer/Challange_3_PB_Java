package br.com.compass.challenge3SpringBoot.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.compass.challenge3SpringBoot.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByTokenAndExpiresAtAfter(UUID token, LocalDateTime now);

    void deleteByExpiresAtBefore(LocalDateTime now);

    @Query("SELECT COUNT(t) > 0 FROM PasswordResetToken t WHERE t.usuario.id = :usuarioId AND t.expiresAt > :now")
    boolean existsValidTokenByUsuarioId(@Param("usuarioId") Long usuarioId, @Param("now") LocalDateTime now);
}
