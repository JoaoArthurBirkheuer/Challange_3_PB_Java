package br.com.compass.challenge3SpringBoot.exception;

import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import br.com.compass.challenge3SpringBoot.dto.RegisterResponseDTO;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RegisterResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors()
                          .stream()
                          .map(FieldError::getDefaultMessage)
                          .collect(Collectors.joining("; "));
        return ResponseEntity
                .badRequest()
                .body(new RegisterResponseDTO("Dados inválidos: " + errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RegisterResponseDTO> handleConstraintViolations(ConstraintViolationException ex) {
        String errors = ex.getConstraintViolations()
                          .stream()
                          .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                          .collect(Collectors.joining("; "));
        return ResponseEntity
                .badRequest()
                .body(new RegisterResponseDTO("Dados inválidos: " + errors));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<RegisterResponseDTO> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new RegisterResponseDTO("Formato de mídia não suportado: " + ex.getContentType()));
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<RegisterResponseDTO> handleEmailDuplicado(EmailJaCadastradoException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new RegisterResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(PasswordResetTokenInvalidException.class)
    public ResponseEntity<RegisterResponseDTO> handleTokenInvalido(PasswordResetTokenInvalidException ex) {
        return ResponseEntity
                .badRequest()
                .body(new RegisterResponseDTO("Token inválido: " + ex.getMessage()));
    }

    @ExceptionHandler(PasswordUpdateException.class)
    public ResponseEntity<RegisterResponseDTO> handleErroAtualizacaoSenha(PasswordUpdateException ex) {
        return ResponseEntity
                .badRequest()
                .body(new RegisterResponseDTO("Erro ao atualizar a senha: " + ex.getMessage()));
    }

    @ExceptionHandler({
        EntityNotFoundException.class,
        ResourceNotFoundException.class,
        OrderNotFoundException.class
    })
    public ResponseEntity<RegisterResponseDTO> handleNotFound(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RegisterResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RegisterResponseDTO> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new RegisterResponseDTO("Operação não permitida: integridade de dados violada"));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<RegisterResponseDTO> handleRegraNegocio(BusinessRuleException ex) {
        String msg = ex.getMessage().toLowerCase();
        if (msg.contains("permissão") || msg.contains("acesso negado")) {
           
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new RegisterResponseDTO(ex.getMessage()));
        }
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new RegisterResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(UserWithActiveOrdersException.class)
    public ResponseEntity<RegisterResponseDTO> handleUsuarioComPedidosAtivos(UserWithActiveOrdersException ex) {
        return ResponseEntity
                .badRequest()
                .body(new RegisterResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(InvalidOrderStatusUpdateException.class)
    public ResponseEntity<RegisterResponseDTO> handleStatusInvalido(InvalidOrderStatusUpdateException ex) {
        return ResponseEntity
                .badRequest()
                .body(new RegisterResponseDTO("Atualização de status inválida: " + ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RegisterResponseDTO> handleAuthenticationError(AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new RegisterResponseDTO("Não autenticado: " + ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RegisterResponseDTO> handleAcessoNegado(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new RegisterResponseDTO("Acesso negado: " + ex.getMessage()));
    }
    
    @ExceptionHandler(ReportBadRequestException.class)
    public ResponseEntity<RegisterResponseDTO> handleReportBadRequest(ReportBadRequestException ex) {
        return ResponseEntity
                .badRequest()
                .body(new RegisterResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<RegisterResponseDTO> handleNoData(NoDataFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RegisterResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RegisterResponseDTO> handleGeneric(Exception ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RegisterResponseDTO("Erro interno: " + ex.getMessage()));
    }
}
