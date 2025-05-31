package br.com.compass.challenge3SpringBoot.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequestDTO {
    private Long productId;
    
    @Min(value = 1, message = "A quantidade deve ser maior ou igual a 1.")
    private int quantity;
}
