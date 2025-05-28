package br.com.compass.challenge3SpringBoot.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.compass.challenge3SpringBoot.entity.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderSummaryDTO {
    private Long id;
    private LocalDateTime data;
    private StatusPedido status;
    private BigDecimal total;
    private Long clienteId;
    private String clienteEmail;
}
