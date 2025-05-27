package br.com.compass.challenge3SpringBoot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import br.com.compass.challenge3SpringBoot.dto.CartItemRequestDTO;
import br.com.compass.challenge3SpringBoot.dto.CartResponseDTO;
import br.com.compass.challenge3SpringBoot.dto.general.MessageResponseDTO;
import br.com.compass.challenge3SpringBoot.entity.Usuario;
import br.com.compass.challenge3SpringBoot.service.CartService;
import br.com.compass.challenge3SpringBoot.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService; 

    private Long getAuthenticatedUserId(Authentication authentication) {
        String email = authentication.getName(); 
        Usuario user = userService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado"));
        return user.getId();
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<MessageResponseDTO> addItem(@RequestBody CartItemRequestDTO dto,
                                                      Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        MessageResponseDTO response = cartService.adicionarItem(userId, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<MessageResponseDTO> removeItem(@PathVariable Long itemId,
                                                         Authentication authentication) {
        MessageResponseDTO response = cartService.removerItem(itemId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<MessageResponseDTO> clearCart(Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        MessageResponseDTO response = cartService.limparCarrinho(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE') ")
    public ResponseEntity<CartResponseDTO> viewCart(Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        CartResponseDTO response = cartService.visualizarCarrinho(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<MessageResponseDTO> checkout(Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        MessageResponseDTO response = cartService.finalizarCompra(userId);
        return ResponseEntity.ok(response);
    }
}
