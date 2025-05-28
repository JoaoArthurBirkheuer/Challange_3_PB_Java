package br.com.compass.challenge3SpringBoot.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.compass.challenge3SpringBoot.dto.OrderDetailDTO;
import br.com.compass.challenge3SpringBoot.dto.OrderItemDTO;
import br.com.compass.challenge3SpringBoot.dto.OrderSummaryDTO;
import br.com.compass.challenge3SpringBoot.entity.ItemPedido;
import br.com.compass.challenge3SpringBoot.entity.Pedido;

@Component
public class OrderMapper {

    public OrderSummaryDTO toSummaryDTO(Pedido pedido) {
        return OrderSummaryDTO.builder()
            .id(pedido.getId())
            .data(pedido.getData())
            .status(pedido.getStatus())
            .total(pedido.getTotal())
            .clienteId(pedido.getCliente().getId())
            .clienteEmail(pedido.getCliente().getEmail())
            .build();
    }

    public OrderItemDTO toItemDTO(ItemPedido item) {
        return OrderItemDTO.builder()
            .produtoId(item.getProduto().getId())
            .nomeProduto(item.getProduto().getNome())
            .quantidade(item.getQuantidade())
            .precoUnitario(item.getPrecoUnitario())
            .build();
    }

    public OrderDetailDTO toDetailDTO(Pedido pedido) {
        List<OrderItemDTO> itens = pedido.getItens()
            .stream()
            .map(item -> toItemDTO(item))
            .collect(Collectors.toList());

        return OrderDetailDTO.builder()
            .id(pedido.getId())
            .data(pedido.getData())
            .status(pedido.getStatus())
            .total(pedido.getTotal())
            .itens(itens)
            .build();
    }
}
