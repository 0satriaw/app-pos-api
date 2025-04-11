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

    Page<OrderResponse> getOrdersByUser(String userId, Pageable pageable);

    List<OrderResponse> getOrdersByOwner(UUID ownerId);

    OrderResponse updateOrderStatus(String orderId, String status);
}
