package sawi.saas.pos.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sawi.saas.pos.dto.ApiResponse;
import sawi.saas.pos.dto.CategoryResponse;
import sawi.saas.pos.dto.DashboardStatsResponse;
import sawi.saas.pos.service.DashboardService;

import java.util.UUID;

@RestController
@RequestMapping("api/dashboard")
@AllArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardAdminStats() {
        DashboardStatsResponse dashboardStatsResponse = dashboardService.getAdminDashboardStats();

        return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard admin fetch successfully", dashboardStatsResponse));
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/owner/{ownerID}")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardOwnerStats(@PathVariable String ownerID) {
        DashboardStatsResponse dashboardStatsResponse = dashboardService.getOwnerDashboardStats(UUID.fromString(ownerID));

        return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard owner fetch successfully", dashboardStatsResponse));
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/cashier/{userId}")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardCashierStats(@PathVariable String userId) {
        DashboardStatsResponse dashboardStatsResponse = dashboardService.getCashierDashboardStats(UUID.fromString(userId));

        return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard cashier fetch successfully", dashboardStatsResponse));
    }


}
