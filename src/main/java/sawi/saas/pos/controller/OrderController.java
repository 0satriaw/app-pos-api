package sawi.saas.pos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sawi.saas.pos.dto.ApiResponse;
import sawi.saas.pos.dto.OrderRequest;
import sawi.saas.pos.dto.OrderResponse;
import sawi.saas.pos.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CASHIER', 'OWNER')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestBody @Valid OrderRequest request) {
        OrderResponse orderResponse = orderService.createOrder(request);

        return ResponseEntity.ok(new ApiResponse<>(true, "Order created successfully", orderResponse));

    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CASHIER','OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable String orderId) {
        OrderResponse orderResponse = orderService.getOrderById(orderId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Order found", orderResponse));
    }

    @GetMapping("/store/{storeId}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrdersByStore(
            @PathVariable String storeId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderResponse> orders = orderService.getOrdersByStore(storeId, status, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "All orders fetched successfully", orders));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CASHIER','OWNER')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrdersByUser(
            @PathVariable String userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderResponse> orders = orderService.getOrdersByUser(userId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "All orders by user fetched successfully", orders));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('OWNER','CASHIER')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String status
    ){
        OrderResponse orderResponse = orderService.updateOrderStatus(orderId, status);

        return ResponseEntity.ok(new ApiResponse<>(true, "Order updated successfully", orderResponse));
    }
}
