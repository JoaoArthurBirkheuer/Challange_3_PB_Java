package br.com.compass.challenge3SpringBoot.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

// 2.1 SalesReportDTO
@Data @Builder
public class SalesReportDTO {
    private LocalDateTime from;
    private LocalDateTime to;
    private long totalSalesCount;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private List<MostSoldProductDTO> topProducts;
}
