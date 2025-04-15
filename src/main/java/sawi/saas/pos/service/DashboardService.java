package sawi.saas.pos.service;

import sawi.saas.pos.dto.DashboardStatsResponse;

import java.util.UUID;

public interface DashboardService {
    DashboardStatsResponse getAdminDashboardStats();
    DashboardStatsResponse getOwnerDashboardStats(UUID ownerId);
    DashboardStatsResponse getCashierDashboardStats(UUID userId);

}
