package br.com.compass.challenge3SpringBoot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.compass.challenge3SpringBoot.dto.CartItemRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.CartResponseDTO;
import br.com.compass.challenge3SpringBoot.dto.general.MessageResponseDTO;
import br.com.compass.challenge3SpringBoot.service.CartService;
import br.com.compass.challenge3SpringBoot.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final SecurityUtil securityUtil; 

    @PostMapping("/items")
    public ResponseEntity<MessageResponseDTO> addItem(@RequestBody CartItemRequestDTO dto) {
        Long userId = securityUtil.getAuthenticatedUserId(); 
        MessageResponseDTO response = cartService.adicionarItem(userId, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<MessageResponseDTO> removeItem(@PathVariable Long itemId) {
        MessageResponseDTO response = cartService.removerItem(itemId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<MessageResponseDTO> clearCart() {
        Long userId = securityUtil.getAuthenticatedUserId();
        MessageResponseDTO response = cartService.limparCarrinho(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> viewCart() {
        Long userId = securityUtil.getAuthenticatedUserId(); 
        CartResponseDTO response = cartService.visualizarCarrinho(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<MessageResponseDTO> checkout() {
        Long userId = securityUtil.getAuthenticatedUserId();
        MessageResponseDTO response = cartService.finalizarCompra(userId);
        return ResponseEntity.ok(response);
    }
}
