package br.com.compass.challenge3SpringBoot.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.compass.challenge3SpringBoot.dto.LowStockProductDTO;
import br.com.compass.challenge3SpringBoot.dto.MostSoldProductDTO;
import br.com.compass.challenge3SpringBoot.dto.SalesReportDTO;
import br.com.compass.challenge3SpringBoot.dto.TopClientDTO;
import br.com.compass.challenge3SpringBoot.dto.general.PageResponseDTO;
import br.com.compass.challenge3SpringBoot.entity.Produto;
import br.com.compass.challenge3SpringBoot.entity.Usuario;
import br.com.compass.challenge3SpringBoot.exception.ReportBadRequestException;
import br.com.compass.challenge3SpringBoot.repository.ItemPedidoRepository;
import br.com.compass.challenge3SpringBoot.repository.PedidoRepository;
import br.com.compass.challenge3SpringBoot.repository.ProdutoRepository;
import br.com.compass.challenge3SpringBoot.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final PedidoRepository pedidoRepo;
    private final ItemPedidoRepository itemPedidoRepo;
    private final ProdutoRepository produtoRepo;
    private final UsuarioRepository usuarioRepo;

    public SalesReportDTO salesReport(String fromStr, String toStr, int topN) {
        LocalDate fromDate, toDate;
        try {
        	fromDate = LocalDate.parse(fromStr);
            toDate = LocalDate.parse(toStr);
        } catch (Exception e) {
            throw new ReportBadRequestException("Datas inválidas, use YYYY-MM-DD");
        }
        if (toDate.isBefore(fromDate)) {
            throw new ReportBadRequestException("'to' deve ser maior ou igual a 'from'");
        }
        LocalDateTime inicio = fromDate.atStartOfDay();
        LocalDateTime fim = toDate.plusDays(1).atStartOfDay().minusNanos(1);

        long salesCount = pedidoRepo.findVendasPorPeriodo(inicio, fim, PageRequest.of(0, 1))
                .getTotalElements();
        var totalRevenue = pedidoRepo.calcularTotalVendasPorPeriodo(inicio, fim);
        var totalProfit = pedidoRepo.calcularLucroPorPeriodo(inicio, fim);

        var rawTop = itemPedidoRepo.findProdutosMaisVendidosPorPeriodo(inicio, fim)
                .stream()
                .map(arr -> {
                    var p = (Produto) arr[0];
                    var qty = ((Number) arr[1]).longValue();
                    return MostSoldProductDTO.builder()
                            .productId(p.getId())
                            .name(p.getNome())
                            .quantitySold(qty)
                            .build();
                })
                .limit(topN)
                .collect(Collectors.toList());

        return SalesReportDTO.builder()
                .from(inicio)
                .to(fim)
                .totalSalesCount(salesCount)
                .totalRevenue(totalRevenue)
                .totalProfit(totalProfit)
                .topProducts(rawTop)
                .build();
    }

    public PageResponseDTO<LowStockProductDTO> lowStock(int threshold, int page, int size) {
        if (threshold < 0) {
            throw new ReportBadRequestException("Threshold deve ser >= 0");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Produto> prods = produtoRepo.findProdutosComEstoqueBaixo(threshold, pageable);

        List<LowStockProductDTO> content = prods.getContent().stream()
                .map(p -> LowStockProductDTO.builder()
                        .productId(p.getId())
                        .name(p.getNome())
                        .stock(p.getEstoque())
                        .build())
                .toList();

        return buildPageResponse(content, prods);
    }

    public PageResponseDTO<MostSoldProductDTO> mostSoldProducts(int page, int size) {
        Page<Object[]> result = produtoRepo.findProdutosMaisVendidos(PageRequest.of(page, size));

        List<MostSoldProductDTO> content = result.getContent().stream()
                .map(arr -> {
                    Produto p = (Produto) arr[0];
                    long qty = ((Number) arr[1]).longValue();
                    return MostSoldProductDTO.builder()
                            .productId(p.getId())
                            .name(p.getNome())
                            .quantitySold(qty)
                            .build();
                }).toList();

        return buildPageResponse(content, result);
    }

    public PageResponseDTO<TopClientDTO> topClientsByOrders(int page, int size) {
        Page<Object[]> result = usuarioRepo.findTopClientesByPedidosCount(PageRequest.of(page, size));

        List<TopClientDTO> content = result.getContent().stream()
                .map(arr -> {
                    Usuario u = (Usuario) arr[0];
                    long cnt = ((Number) arr[1]).longValue();
                    return TopClientDTO.builder()
                            .clientId(u.getId())
                            .email(u.getEmail())
                            .ordersCount(cnt)
                            .build();
                }).toList();

        return buildPageResponse(content, result);
    }

    public PageResponseDTO<TopClientDTO> topClientsBySpending(int page, int size) {
        Page<Object[]> result = usuarioRepo.findTopClientesByTotalGasto(PageRequest.of(page, size));

        List<TopClientDTO> content = result.getContent().stream()
                .map(arr -> {
                    Usuario u = (Usuario) arr[0];
                    var spent = (java.math.BigDecimal) arr[1];
                    return TopClientDTO.builder()
                            .clientId(u.getId())
                            .email(u.getEmail())
                            .totalSpent(spent)
                            .build();
                }).toList();

        return buildPageResponse(content, result);
    }

    private <T> PageResponseDTO<T> buildPageResponse(List<T> content, Page<?> page) {
        return PageResponseDTO.<T>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
