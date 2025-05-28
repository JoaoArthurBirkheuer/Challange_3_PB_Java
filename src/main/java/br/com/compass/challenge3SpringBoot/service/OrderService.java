package br.com.compass.challenge3SpringBoot.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.compass.challenge3SpringBoot.dto.OrderDetailDTO;
import br.com.compass.challenge3SpringBoot.dto.OrderSummaryDTO;
import br.com.compass.challenge3SpringBoot.dto.general.PageResponseDTO;
import br.com.compass.challenge3SpringBoot.entity.ItemPedido;
import br.com.compass.challenge3SpringBoot.entity.Pedido;
import br.com.compass.challenge3SpringBoot.entity.Produto;
import br.com.compass.challenge3SpringBoot.entity.StatusPedido;
import br.com.compass.challenge3SpringBoot.entity.Usuario;
import br.com.compass.challenge3SpringBoot.exception.BusinessRuleException;
import br.com.compass.challenge3SpringBoot.exception.OrderNotFoundException;
import br.com.compass.challenge3SpringBoot.mapper.OrderMapper;
import br.com.compass.challenge3SpringBoot.repository.PedidoRepository;
import br.com.compass.challenge3SpringBoot.repository.ProdutoRepository;
import br.com.compass.challenge3SpringBoot.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;

    private final OrderMapper orderMapper;

    // ADMIN
    public PageResponseDTO<OrderSummaryDTO> listarTodos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Pedido> pedidos = pedidoRepository.findAll(pageable);

        List<OrderSummaryDTO> dtos = pedidos.getContent().stream()
                .map(orderMapper::toSummaryDTO)
                .toList();

        return PageResponseDTO.<OrderSummaryDTO>builder()
                .content(dtos)
                .page(pedidos.getNumber())
                .size(pedidos.getSize())
                .totalElements(pedidos.getTotalElements())
                .totalPages(pedidos.getTotalPages())
                .first(pedidos.isFirst())
                .last(pedidos.isLast())
                .build();
    }

    // CLIENTE
    public PageResponseDTO<OrderSummaryDTO> listarMeusPedidos(Long userId, int page, int size) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Pedido> pedidos = pedidoRepository.findByCliente(usuario, pageable);

        List<OrderSummaryDTO> dtos = pedidos.getContent().stream()
                .map(orderMapper::toSummaryDTO)
                .toList();

        return PageResponseDTO.<OrderSummaryDTO>builder()
                .content(dtos)
                .page(pedidos.getNumber())
                .size(pedidos.getSize())
                .totalElements(pedidos.getTotalElements())
                .totalPages(pedidos.getTotalPages())
                .first(pedidos.isFirst())
                .last(pedidos.isLast())
                .build();
    }

    // CLIENTE
    public OrderDetailDTO buscarPorId(Long userId, Long orderId) {
        Pedido pedido = pedidoRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado"));

        if (!pedido.getCliente().getId().equals(userId)) {
            throw new BusinessRuleException("Você não tem permissão para acessar este pedido");
        }

        return orderMapper.toDetailDTO(pedido);
    }

    // ADMIN
    @Transactional
    public void atualizarStatus(Long orderId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado"));

        StatusPedido statusAtual = pedido.getStatus();

        if (statusAtual == novoStatus) {
            throw new BusinessRuleException("O pedido já está com o status informado.");
        }

        if (statusAtual != StatusPedido.CANCELADO && novoStatus == StatusPedido.CANCELADO) {
            // Repor estoque
            for (ItemPedido item : pedido.getItens()) {
                Produto produto = item.getProduto();
                produto.setEstoque(produto.getEstoque() + item.getQuantidade());
                produtoRepository.save(produto);
            }
        } else if (statusAtual == StatusPedido.CANCELADO && novoStatus != StatusPedido.CANCELADO) {
            // Retirar estoque novamente
            for (ItemPedido item : pedido.getItens()) {
                Produto produto = item.getProduto();
                if (produto.getEstoque() < item.getQuantidade()) {
                    throw new BusinessRuleException("Estoque insuficiente para reprocessar o pedido.");
                }
                produto.setEstoque(produto.getEstoque() - item.getQuantidade());
                produtoRepository.save(produto);
            }
        }

        pedido.setStatus(novoStatus);
        pedidoRepository.save(pedido);
    }
}
