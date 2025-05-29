package br.com.compass.challenge3SpringBoot.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.compass.challenge3SpringBoot.dto.CartItemRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.CartItemResponseDTO;
import br.com.compass.challenge3SpringBoot.dto.CartResponseDTO;
import br.com.compass.challenge3SpringBoot.dto.general.MessageResponseDTO;
import br.com.compass.challenge3SpringBoot.entity.Carrinho;
import br.com.compass.challenge3SpringBoot.entity.ItemCarrinho;
import br.com.compass.challenge3SpringBoot.entity.ItemPedido;
import br.com.compass.challenge3SpringBoot.entity.Pedido;
import br.com.compass.challenge3SpringBoot.entity.Produto;
import br.com.compass.challenge3SpringBoot.entity.StatusPedido;
import br.com.compass.challenge3SpringBoot.entity.Usuario;
import br.com.compass.challenge3SpringBoot.exception.BusinessRuleException;
import br.com.compass.challenge3SpringBoot.exception.CartItemNotFoundException;
import br.com.compass.challenge3SpringBoot.exception.CartNotFoundException;
import br.com.compass.challenge3SpringBoot.exception.ResourceNotFoundException;
import br.com.compass.challenge3SpringBoot.repository.CarrinhoRepository;
import br.com.compass.challenge3SpringBoot.repository.ItemCarrinhoRepository;
import br.com.compass.challenge3SpringBoot.repository.PedidoRepository;
import br.com.compass.challenge3SpringBoot.repository.ProdutoRepository;
import br.com.compass.challenge3SpringBoot.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CarrinhoRepository cartRepository;
    private final ItemCarrinhoRepository itemRepository;
    private final ProdutoRepository productRepository;
    private final UsuarioRepository userRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional
    public MessageResponseDTO adicionarItem(Long userId, CartItemRequestDTO dto) {
        Usuario user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        Produto product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        if (dto.getQuantity() <= 0) {
            throw new BusinessRuleException("A quantidade deve ser maior que zero."); 
        }

        if (product.getEstoque() < dto.getQuantity()) {
            throw new BusinessRuleException("Estoque insuficiente para o produto: " + product.getNome()); 
        }

        Carrinho cart = cartRepository.findByUsuarioAndDeletedFalse(user)
            .orElseGet(() -> {
                Carrinho novo = new Carrinho();
                novo.setUsuario(user);
                return cartRepository.save(novo);
            });

        ItemCarrinho item = itemRepository.findByCarrinhoAndProdutoAndDeletedFalse(cart, product)
            .orElse(null);

        if (item != null) {
            int novaQuantidade = item.getQuantidade() + dto.getQuantity();

            if (product.getEstoque() < novaQuantidade) {
                throw new BusinessRuleException("Estoque insuficiente para aumentar a quantidade deste produto no carrinho."); // Changed from RuntimeException
            }

            item.setQuantidade(novaQuantidade);
        } else {
            item = new ItemCarrinho();
            item.setCarrinho(cart);
            item.setProduto(product);
            item.setQuantidade(dto.getQuantity());
        }

        itemRepository.save(item);

        return new MessageResponseDTO("Produto adicionado ao carrinho com sucesso.");
    }

    @Transactional
    public MessageResponseDTO removerItem(Long itemId) {
        ItemCarrinho item = itemRepository.findById(itemId)
            .orElseThrow(() -> new CartItemNotFoundException("Item do carrinho não encontrado"));

        item.setDeleted(true);
        itemRepository.save(item);

        return new MessageResponseDTO("Item removido do carrinho com sucesso.");
    }

    @Transactional
    public MessageResponseDTO limparCarrinho(Long userId) {
        Usuario user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        Carrinho cart = cartRepository.findByUsuarioAndDeletedFalse(user)
            .orElseThrow(() -> new CartNotFoundException("Carrinho não encontrado"));

        List<ItemCarrinho> items = itemRepository.findByCarrinhoAndDeletedFalse(cart);

        for (ItemCarrinho item : items) {
            item.setDeleted(true);
            itemRepository.save(item);
        }

        return new MessageResponseDTO("Carrinho esvaziado com sucesso.");
    }

    @Transactional(readOnly = true)
    public CartResponseDTO visualizarCarrinho(Long userId) {
        Usuario user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado")); 
        Carrinho cart = cartRepository.findByUsuarioAndDeletedFalse(user)
            .orElseThrow(() -> new CartNotFoundException("Carrinho não encontrado"));

        List<ItemCarrinho> items = itemRepository.findByCarrinhoAndDeletedFalse(cart);

        List<CartItemResponseDTO> itemDTOs = items.stream().map(item -> {
            CartItemResponseDTO dto = new CartItemResponseDTO();
            dto.setId(item.getId());
            dto.setProductId(item.getProduto().getId());
            dto.setProductName(item.getProduto().getNome());
            dto.setQuantity(item.getQuantidade());
            BigDecimal unitPrice = item.getProduto().getPreco();
            dto.setUnitPrice(unitPrice);
            dto.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(item.getQuantidade())));
            return dto;
        }).collect(Collectors.toList());

        BigDecimal total = itemDTOs.stream()
            .map(CartItemResponseDTO::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartResponseDTO response = new CartResponseDTO();
        response.setId(cart.getId());
        response.setItems(itemDTOs);
        response.setTotalAmount(total);

        return response;
    }

    @Transactional
    public MessageResponseDTO finalizarCompra(Long userId) {
        Usuario user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        Carrinho cart = cartRepository.findByUsuarioAndDeletedFalse(user)
            .orElseThrow(() -> new CartNotFoundException("Carrinho não encontrado"));

        List<ItemCarrinho> items = itemRepository.findByCarrinhoAndDeletedFalse(cart);

        if (items.isEmpty()) {
            throw new BusinessRuleException("Carrinho está vazio.");
        }

        BigDecimal total = BigDecimal.ZERO;
        List<ItemPedido> itensPedido = new ArrayList<>();

        Pedido pedido = Pedido.builder()
            .cliente(user)
            .data(LocalDateTime.now())
            .status(StatusPedido.PENDENTE)
            .build();

        for (ItemCarrinho item : items) {
            Produto produto = item.getProduto();
            if (produto.getEstoque() < item.getQuantidade()) {
                throw new BusinessRuleException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            produto.setEstoque(produto.getEstoque() - item.getQuantidade());
            productRepository.save(produto);

            BigDecimal subtotal = produto.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));
            total = total.add(subtotal);

            ItemPedido itemPedido = ItemPedido.builder()
                .pedido(pedido) 
                .produto(produto)
                .quantidade(item.getQuantidade())
                .precoUnitario(produto.getPreco())
                .build();

            itensPedido.add(itemPedido);
        }

        pedido.setTotal(total);
        pedido.setItens(itensPedido);
        pedidoRepository.save(pedido);
        itemRepository.deleteAll(items);

        return new MessageResponseDTO("Pedido realizado com sucesso.");
    }
}