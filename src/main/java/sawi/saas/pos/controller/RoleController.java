package sawi.saas.pos.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sawi.saas.pos.dto.ApiResponse;
import sawi.saas.pos.dto.CategoryResponse;
import sawi.saas.pos.dto.RoleResponse;
import sawi.saas.pos.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("api/roles")
@AllArgsConstructor
public class RoleController {
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllCategories (){

        List<RoleResponse> roleResponses = roleService.getAllRoles();
        return ResponseEntity.ok(new ApiResponse<>(true, "Success fetched all roles", roleResponses));
    }
}
