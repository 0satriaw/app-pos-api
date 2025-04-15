package sawi.saas.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Long totalUsers;
    private Long totalStores;
    private Long totalOrders;
    private Long totalProducts;
    private Double totalSalesToday;
    private Long totalOrdersToday;
}
