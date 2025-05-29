package br.com.compass.challenge3SpringBoot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.challenge3SpringBoot.dto.LowStockProductDTO;
import br.com.compass.challenge3SpringBoot.dto.MostSoldProductDTO;
import br.com.compass.challenge3SpringBoot.dto.SalesReportDTO;
import br.com.compass.challenge3SpringBoot.dto.TopClientDTO;
import br.com.compass.challenge3SpringBoot.dto.general.PageResponseDTO;
import br.com.compass.challenge3SpringBoot.service.ReportService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales")
    public ResponseEntity<SalesReportDTO> salesReport(
        @RequestParam String from,
        @RequestParam String to,
        @RequestParam(defaultValue = "5") int topN) {
    
    SalesReportDTO dto = reportService.salesReport(from, to, topN);
    return ResponseEntity.ok(dto);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<PageResponseDTO<LowStockProductDTO>> lowStock(
            @RequestParam int threshold,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reportService.lowStock(threshold, page, size));
    }

    @GetMapping("/most-sold")
    public ResponseEntity<PageResponseDTO<MostSoldProductDTO>> mostSold(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reportService.mostSoldProducts(page, size));
    }

    @GetMapping("/top-clients/orders")
    public ResponseEntity<PageResponseDTO<TopClientDTO>> topClientsByOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reportService.topClientsByOrders(page, size));
    }

    @GetMapping("/top-clients/spending")
    public ResponseEntity<PageResponseDTO<TopClientDTO>> topClientsBySpending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reportService.topClientsBySpending(page, size));
    }
}
