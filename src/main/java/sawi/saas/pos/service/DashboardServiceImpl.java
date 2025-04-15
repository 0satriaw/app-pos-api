package sawi.saas.pos.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sawi.saas.pos.dto.DashboardStatsResponse;
import sawi.saas.pos.repository.OrderRepository;
import sawi.saas.pos.repository.ProductRepository;
import sawi.saas.pos.repository.StoreRepository;
import sawi.saas.pos.repository.UserRepository;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;


    @Override
    public DashboardStatsResponse getAdminDashboardStats() {
        long totalUsers = userRepository.count();
        long totalStores = storeRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();


        return new DashboardStatsResponse(totalUsers, totalStores, totalOrders,totalProducts, 0.0, 0L);

    }

    @Override
    public DashboardStatsResponse getOwnerDashboardStats(UUID ownerId) {
        long myStores = storeRepository.countByOwnerId(ownerId);

        long totalOrdersToday = orderRepository.countByStore_OwnerIdAndCreatedAtBetween(ownerId, LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());

        Double totalSalesToday = orderRepository.sumTotalPriceByStoreOwnerIdAndCreatedAtBetween(ownerId, LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());

        if(totalSalesToday == null) {
            totalSalesToday = 0.0;
        }

        return new DashboardStatsResponse(0L, myStores, 0L,0L, totalSalesToday, totalOrdersToday);
    }

    @Override
    public DashboardStatsResponse getCashierDashboardStats(UUID userId) {
        logger.info("Fetching dashboard stats for cashier {}", userId);
        logger.info(LocalDate.now().atStartOfDay().toString());
        logger.info(LocalDate.now().plusDays(1).atStartOfDay().toString());
        long totalOrdersToday =  orderRepository.countByUserIdAndCreatedAtBetween(userId, LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
        Double totalSalesToday = orderRepository.sumTotalPriceByUserIdAndCreatedAtBetween(userId, LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());

        if(totalSalesToday == null) {
            totalSalesToday = 0.0;
        }

        return new DashboardStatsResponse(0L, 0L, 0L,0L, totalSalesToday, totalOrdersToday);
    }
}
