package sawi.saas.pos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sawi.saas.pos.dto.ApiResponse;
import sawi.saas.pos.dto.ProductRequest;
import sawi.saas.pos.dto.ProductResponse;
import sawi.saas.pos.entity.User;
import sawi.saas.pos.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/stores/{storeId}/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @PathVariable("storeId") UUID storeId,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(storeId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product created successfully", response));

    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
            @PathVariable UUID storeId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ProductResponse> products = productService.getAllProducts(storeId, name, minPrice, maxPrice, inStock, pageable);

        return ResponseEntity.ok(new ApiResponse<>(true, "All products fetched successfully", products));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable UUID storeId,
            @PathVariable UUID productId
            ){
        ProductResponse response = productService.getProductById(
                productId
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "Product fetched successfully", response));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID storeId,
            @PathVariable UUID productId,
            @Valid @RequestBody ProductRequest productRequest
            ){
        ProductResponse response = productService.updateProduct(productId, productRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product updated successfully", response));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<ProductResponse>> deleteProduct(
            @PathVariable UUID productId,
            @PathVariable UUID storeId
            ){
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

}
