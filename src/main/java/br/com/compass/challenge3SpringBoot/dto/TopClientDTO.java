package br.com.compass.challenge3SpringBoot.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class TopClientDTO {
    private Long clientId;
    private String email;
    private long ordersCount;
    private BigDecimal totalSpent;
}
