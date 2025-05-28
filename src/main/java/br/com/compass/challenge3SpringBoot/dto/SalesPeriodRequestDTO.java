package br.com.compass.challenge3SpringBoot.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class SalesPeriodRequestDTO {
    @NotBlank String from; 
    @NotBlank String to;
    int topN = 5;
}
