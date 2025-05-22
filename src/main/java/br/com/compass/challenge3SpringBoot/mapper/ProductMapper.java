package br.com.compass.challenge3SpringBoot.mapper;

import org.springframework.stereotype.Component;

import br.com.compass.challenge3SpringBoot.dto.ProductRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.ProductResponseDTO;
import br.com.compass.challenge3SpringBoot.entity.Produto;

@Component
public class ProductMapper {

    public Produto toEntity(ProductRequestDTO dto) {
        if (dto == null) return null;

        return Produto.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .preco(dto.getPreco())
                .estoque(dto.getEstoque())
                .ativo(dto.getAtivo())
                .build();
    }

    public void updateEntityFromDto(ProductRequestDTO dto, Produto produto) {
        if (dto == null || produto == null) return;

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setEstoque(dto.getEstoque());
        produto.setAtivo(dto.getAtivo());
    }

    public ProductResponseDTO toResponseDTO(Produto produto) {
        if (produto == null) return null;

        return ProductResponseDTO.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .descricao(produto.getDescricao())
                .preco(produto.getPreco())
                .estoque(produto.getEstoque())
                .ativo(produto.getAtivo())
                .build();
    }
}
