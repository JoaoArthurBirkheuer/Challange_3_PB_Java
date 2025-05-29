package br.com.compass.challenge3SpringBoot.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import br.com.compass.challenge3SpringBoot.dto.general.ErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors()
                              .stream()
                              .map(FieldError::getDefaultMessage)
                              .collect(Collectors.joining("; "));
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(), "Dados inválidos: " + errors, request.getDescription(false)));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolations(ConstraintViolationException ex, WebRequest request) {
        String errors = ex.getConstraintViolations()
                              .stream()
                              .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                              .collect(Collectors.joining("; "));
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(), "Dados inválidos: " + errors, request.getDescription(false)));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(), "Formato de mídia não suportado: " + ex.getContentType(), request.getDescription(false)));
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailDuplicado(EmailJaCadastradoException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(), ex.getMessage(), request.getDescription(false)));
    }

    @ExceptionHandler(PasswordResetTokenInvalidException.class)
    public ResponseEntity<ErrorResponseDTO> handleTokenInvalido(PasswordResetTokenInvalidException ex, WebRequest request) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(), "Token inválido: " + ex.getMessage(), request.getDescription(false)));
    }

    @ExceptionHandler(PasswordUpdateException.class)
    public ResponseEntity<ErrorResponseDTO> handleErroAtualizacaoSenha(PasswordUpdateException ex, WebRequest request) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(), "Erro ao atualizar a senha: " + ex.getMessage(), request.getDescription(false)));
    }

    @ExceptionHandler({
    	EntityNotFoundException.class,
        br.com.compass.challenge3SpringBoot.exception.EntityNotFoundException.class,
        jakarta.persistence.EntityNotFoundException.class,
        ResourceNotFoundException.class,
        OrderNotFoundException.class,
        CartNotFoundException.class,
        CartItemNotFoundException.class,
        NoDataFoundException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleNotFound(RuntimeException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(), request.getDescription(false)));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrity(DataIntegrityViolationException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(), "Operação não permitida: integridade de dados violada", request.getDescription(false)));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDTO> handleRegraNegocio(BusinessRuleException ex, WebRequest request) {
        String msg = ex.getMessage().toLowerCase();
        if (msg.contains("permissão") || msg.contains("acesso negado")) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(),
                            HttpStatus.FORBIDDEN.getReasonPhrase(), ex.getMessage(), request.getDescription(false)));
        }
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(), ex.getMessage(), request.getDescription(false)));
    }

    @ExceptionHandler(UserWithActiveOrdersException.class)
    public ResponseEntity<ErrorResponseDTO> handleUsuarioComPedidosAtivos(UserWithActiveOrdersException ex, WebRequest request) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage(), request.getDescription(false)));
    }

    @ExceptionHandler(InvalidOrderStatusUpdateException.class)
    public ResponseEntity<ErrorResponseDTO> handleStatusInvalido(InvalidOrderStatusUpdateException ex, WebRequest request) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(), "Atualização de status inválida: " + ex.getMessage(), request.getDescription(false)));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationError(AuthenticationException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(), "Não autenticado: " + ex.getMessage(), request.getDescription(false)));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleSpringSecurityAccessDenied(org.springframework.security.access.AccessDeniedException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(),
                        HttpStatus.FORBIDDEN.getReasonPhrase(), "Acesso negado: " + ex.getMessage(), request.getDescription(false)));
    }

    @ExceptionHandler(ReportBadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleReportBadRequest(ReportBadRequestException ex, WebRequest request) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage(), request.getDescription(false)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex, WebRequest request) {
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Erro interno no servidor.", request.getDescription(false)));
    }
}