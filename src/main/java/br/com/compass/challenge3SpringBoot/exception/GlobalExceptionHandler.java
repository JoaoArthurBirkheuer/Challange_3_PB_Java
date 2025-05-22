package br.com.compass.challenge3SpringBoot.exception;

import br.com.compass.challenge3SpringBoot.dto.RegisterResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<RegisterResponseDTO> handleEmailDuplicado(EmailJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(new RegisterResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(PasswordResetTokenInvalidException.class)
    public ResponseEntity<RegisterResponseDTO> handleTokenInvalido(PasswordResetTokenInvalidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new RegisterResponseDTO("Token inválido: " + ex.getMessage()));
    }

    @ExceptionHandler(PasswordUpdateException.class)
    public ResponseEntity<RegisterResponseDTO> handleErroAtualizacaoSenha(PasswordUpdateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new RegisterResponseDTO("Erro ao atualizar a senha: " + ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RegisterResponseDTO> handleEntidadeNaoEncontrada(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(new RegisterResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<RegisterResponseDTO> handleRegraNegocio(BusinessRuleException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                             .body(new RegisterResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RegisterResponseDTO> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new RegisterResponseDTO("Erro interno: " + ex.getMessage()));
    }
}
