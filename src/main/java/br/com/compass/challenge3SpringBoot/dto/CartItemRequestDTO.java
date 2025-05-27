package br.com.compass.challenge3SpringBoot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequestDTO {
    private Long productId;
    private int quantity;
}
