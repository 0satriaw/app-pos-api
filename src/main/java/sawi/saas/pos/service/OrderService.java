package sawi.saas.pos.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sawi.saas.pos.dto.OrderRequest;
import sawi.saas.pos.dto.OrderResponse;
import sawi.saas.pos.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    OrderResponse getOrderById(String orderId);

    Page<OrderResponse> getOrdersByStore(String storeId, String status, Pageable pageable);

    List<OrderResponse> getOrdersByUser(String userId);

    List<OrderResponse> getOrdersByOwner(UUID ownerId);

    OrderResponse updateOrderStatus(String orderId, String status);

    List <OrderResponse> getRecentOrders();
    List <OrderResponse> getRecentOrdersByOwner(UUID ownerId);
    List <OrderResponse> getRecentOrdersByUser(UUID userId);
}
