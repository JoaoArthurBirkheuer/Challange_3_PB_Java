package br.com.compass.challenge3SpringBoot.controller;

import br.com.compass.challenge3SpringBoot.dto.OrderDetailDTO;
import br.com.compass.challenge3SpringBoot.dto.OrderSummaryDTO;
import br.com.compass.challenge3SpringBoot.dto.general.PageResponseDTO;
import br.com.compass.challenge3SpringBoot.entity.StatusPedido;
import br.com.compass.challenge3SpringBoot.entity.Usuario;
import br.com.compass.challenge3SpringBoot.service.OrderService;
import br.com.compass.challenge3SpringBoot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    private Usuario getAuthenticatedUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<PageResponseDTO<OrderSummaryDTO>> listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Usuario user = getAuthenticatedUser(authentication);

        if (user.isAdmin()) {
            return ResponseEntity.ok(orderService.listarTodos(page, size));
        } else {
            return ResponseEntity.ok(orderService.listarMeusPedidos(user.getId(), page, size));
        }
    }

    @GetMapping("/{userId}/{orderId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<OrderDetailDTO> getOrderById(@PathVariable Long userId,
                                                       @PathVariable Long orderId,
                                                       Authentication authentication) {
        Usuario user = getAuthenticatedUser(authentication);

        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(403).build(); 
        }

        OrderDetailDTO dto = orderService.buscarPorId(userId, orderId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id,
                                                  @RequestParam StatusPedido status) {
        orderService.atualizarStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}
