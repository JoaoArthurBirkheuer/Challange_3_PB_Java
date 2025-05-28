package br.com.compass.challenge3SpringBoot.dto;

import br.com.compass.challenge3SpringBoot.entity.StatusPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusDTO {
    @NotNull
    private StatusPedido novoStatus;
}
