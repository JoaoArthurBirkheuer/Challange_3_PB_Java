package br.com.compass.challenge3SpringBoot.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class MostSoldProductDTO {
    private Long productId;
    private String name;
    private long quantitySold;
}
