package br.com.compass.challenge3SpringBoot.service;

import br.com.compass.challenge3SpringBoot.dto.ProductRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.ProductResponseDTO;
import br.com.compass.challenge3SpringBoot.dto.general.PageResponseDTO;
import br.com.compass.challenge3SpringBoot.entity.Produto;
import br.com.compass.challenge3SpringBoot.exception.BusinessRuleException;
import br.com.compass.challenge3SpringBoot.exception.EntityNotFoundException;
import br.com.compass.challenge3SpringBoot.mapper.ProductMapper;
import br.com.compass.challenge3SpringBoot.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProdutoRepository produtoRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponseDTO criar(ProductRequestDTO dto) {
        Produto produto = productMapper.toEntity(dto);
        Produto salvo = produtoRepository.save(produto);
        return productMapper.toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<ProductResponseDTO> listar(String nome, Pageable pageable) {
        Page<Produto> pagina;
        if (nome == null || nome.isBlank()) {
            pagina = produtoRepository.findByAtivoTrueAndDeletedFalse(pageable);
        } else {
            pagina = produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrueAndDeletedFalse(nome, pageable);
        }

        return PageResponseDTO.<ProductResponseDTO>builder()
                .content(pagina.getContent().stream().map(productMapper::toResponseDTO).collect(Collectors.toList()))
                .page(pagina.getNumber())
                .size(pagina.getSize())
                .totalElements(pagina.getTotalElements())
                .totalPages(pagina.getTotalPages())
                .first(pagina.isFirst())
                .last(pagina.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        return productMapper.toResponseDTO(produto);
    }

    @Transactional
    public ProductResponseDTO atualizar(Long id, ProductRequestDTO dto) {
        Produto produto = produtoRepository.findById(id)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setEstoque(dto.getEstoque());
        produto.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : produto.getAtivo());

        return productMapper.toResponseDTO(produtoRepository.save(produto));
    }

    @Transactional
    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        boolean emPedidos = produtoRepository.existsInPedidosAtivos(id);
        if (emPedidos) {
            throw new BusinessRuleException("Produto já vinculado a pedidos, só pode ser inativado");
        }

        produto.setDeleted(true);
        produtoRepository.save(produto);
    }

    @Transactional
    public void inativar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        produto.setAtivo(false);
        produtoRepository.save(produto);
    }
}
