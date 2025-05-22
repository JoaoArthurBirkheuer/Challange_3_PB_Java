package br.com.compass.challenge3SpringBoot.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.challenge3SpringBoot.dto.ProductRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.ProductResponseDTO;
import br.com.compass.challenge3SpringBoot.dto.general.PageResponseDTO;
import br.com.compass.challenge3SpringBoot.service.ProductService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDTO criar(@RequestBody ProductRequestDTO dto) {
        return productService.criar(dto);
    }

    @GetMapping
    public PageResponseDTO<ProductResponseDTO> listar(
            @RequestParam(required = false) String nome,
            Pageable pageable) {
        return productService.listar(nome, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO buscarPorId(@PathVariable Long id) {
        return productService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDTO atualizar(@PathVariable Long id, @RequestBody ProductRequestDTO dto) {
        return productService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletar(@PathVariable Long id) {
        productService.deletar(id);
    }

    @PatchMapping("/{id}/inactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public void inativar(@PathVariable Long id) {
        productService.inativar(id);
    }
}
