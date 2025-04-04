package sawi.saas.pos.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sawi.saas.pos.dto.*;
import sawi.saas.pos.entity.Category;
import sawi.saas.pos.repository.CategoryRepository;
import sawi.saas.pos.service.CategoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Category created successfully", categoryResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories (){

        List<CategoryResponse> categoryResponses = categoryService.getAllCategory();
        return ResponseEntity.ok(new ApiResponse<>(true, "Success fetched all categories", categoryResponses));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable UUID categoryId) {
        CategoryResponse categoryResponse = categoryService.getCategory(categoryId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Category found found", categoryResponse));
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory
            (@PathVariable UUID categoryId,
             @Valid @RequestBody CategoryRequest categoryRequest
            )
    {
        CategoryResponse categoryResponse = categoryService.updateCategory(categoryRequest, categoryId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Category updated successfully", categoryResponse));
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> deleteCategoryById(@PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

}
