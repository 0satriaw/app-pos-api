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
import sawi.saas.pos.dto.StoreRequest;
import sawi.saas.pos.dto.StoreResponse;
import sawi.saas.pos.service.StoreService;

import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<StoreResponse>> createStore(@Valid @RequestBody StoreRequest storeRequest) {
        StoreResponse store = storeService.createStore(storeRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Store created successfully", store));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<StoreResponse>>> getAlLStore (
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name")String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
        ){
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<StoreResponse> storeResponses = storeService.getAllStores(pageable);

        return ResponseEntity.ok(new ApiResponse<>(true, "Succes fetched all stores", storeResponses));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponse>> getStoreById(@PathVariable UUID storeId) {
        StoreResponse store = storeService.getStoreById(storeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Store found", store));
    }

    @PutMapping("/{storeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
            @Valid @RequestBody StoreRequest storeRequest,
        @PathVariable UUID storeId) {
        StoreResponse store = storeService.updateStore(storeId, storeRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Store updated successfully", store));
    }

    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<ApiResponse<StoreResponse>> deleteStoreById(@PathVariable UUID storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity.noContent().build();
    }
}
