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

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByOwner(@PathVariable UUID ownerId) {
        List<OrderResponse> orders = orderService.getOrdersByOwner(ownerId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Orders fetched successfully", orders));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CASHIER','OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable String orderId) {
        OrderResponse orderResponse = orderService.getOrderById(orderId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Order found", orderResponse));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrdersByStore(
            @PathVariable String storeId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "status") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderResponse> orders = orderService.getOrdersByStore(storeId, status, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "All orders fetched successfully", orders));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CASHIER','OWNER','CASHIER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByUser(
            @PathVariable String userId
    ){

        List<OrderResponse> orders = orderService.getOrdersByUser(userId);
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

    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllRecentOrders(
    ){

        List<OrderResponse> orders = orderService.getRecentOrders();
        return ResponseEntity.ok(new ApiResponse<>(true, "All recent orders fetched successfully", orders));
    }

    @GetMapping("/recent/owner/{ownerId}")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getRecentOrdersByOwner(
            @PathVariable String ownerId
    ){

        List<OrderResponse> orders = orderService.getRecentOrdersByOwner(UUID.fromString(ownerId));
        return ResponseEntity.ok(new ApiResponse<>(true, "All recent orders by owner fetched successfully", orders));
    }

    @GetMapping("/recent/cashier/{userId}")
    @PreAuthorize("hasAnyRole('CASHIER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getRecentOrdersByCashier(
            @PathVariable String userId
    ){

        List<OrderResponse> orders = orderService.getRecentOrdersByUser(UUID.fromString(userId));
        return ResponseEntity.ok(new ApiResponse<>(true, "All recent orders by cashier fetched successfully", orders));
    }



}
