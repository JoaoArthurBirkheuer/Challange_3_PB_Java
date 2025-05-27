package br.com.compass.challenge3SpringBoot.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartResponseDTO {
    private Long id;
    private List<CartItemResponseDTO> items;
    private BigDecimal totalAmount;
}
