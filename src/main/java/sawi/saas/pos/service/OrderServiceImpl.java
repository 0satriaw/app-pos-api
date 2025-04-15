package sawi.saas.pos.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sawi.saas.pos.dto.OrderItemRequest;
import sawi.saas.pos.dto.OrderItemResponse;
import sawi.saas.pos.dto.OrderRequest;
import sawi.saas.pos.dto.OrderResponse;
import sawi.saas.pos.entity.*;
import sawi.saas.pos.repository.OrderRepository;
import sawi.saas.pos.repository.ProductRepository;
import sawi.saas.pos.repository.StoreRepository;
import sawi.saas.pos.repository.UserRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final UserService userService;


    @Override
    @Transactional
    public OrderResponse updateOrderStatus(String orderId, String status) {
        User user = userService.getCurrentUser();

        validateOrderStatus(status);

        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if(user.getRole().getName().equals("OWNER")) {
            if(!order.getStore().getOwner().getId().equals(user.getId())) {
                throw new AccessDeniedException("You don't have permission to update this order");
            }
        }

        order.setStatus(status);

        Order updatedOrder = orderRepository.save(order);

        return mapToOrderResponse(updatedOrder);
    }

    @Override
    public List<OrderResponse> getOrdersByUser(String userId) {
        User user =  userService.getCurrentUser();

        List<Order> orders = orderRepository.findByUserId(user.getId());

        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByOwner(UUID ownerId) {
        User user = userService.getCurrentUser();
        List<Order> orders = orderRepository.findByOwnerId(ownerId);

        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());

    }


    @Override
    public Page<OrderResponse> getOrdersByStore(String storeId, String status, Pageable pageable){
        User user =  userService.getCurrentUser();

        Store store = storeRepository.findById(UUID.fromString(storeId)).
                orElseThrow(() -> new EntityNotFoundException("Store not found"));

        if(!user.getRole().getName().equals("ADMIN")) {
            if(!store.getOwner().getId().equals(user.getId())) {
                throw new AccessDeniedException("You do not have permission to access this store");
            }
        }

        Page<Order> orders;
        if(status != null && !status.isEmpty()) {
            orders = orderRepository.findByStoreIdAndStatus(store.getId(), status, pageable);
        }else{
            orders = orderRepository.findByStoreId(store.getId(), pageable);
        }

        return orders.map(this::mapToOrderResponse);

    }

    @Override
    public OrderResponse getOrderById(String orderId) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        User user =  userService.getCurrentUser();

        if (user.getRole().getName().equals("CASHIER") ||
        user.getRole().getName().equals("OWNER")) {
            if (user.getRole().getName().equals("OWNER")
            && !order.getStore().getOwner().getId().equals(user.getId())) {
                throw new AccessDeniedException("You can only view order for your own store");
            }

            if(user.getRole().getName().equals("CASHIER")
            && !order.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You can only view order you created");
            }

        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Store store = storeRepository.findById(UUID.fromString(request.getStoreId()))
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

        User currentUser = userService.getCurrentUser();

        if(!currentUser.getRole().getName().equals("CASHIER") &&
        !currentUser.getRole().getName().equals("OWNER")) {
            throw new AccessDeniedException("You are not authorized to add products to this store");
        }

        Date today = new Date();

        //Create Order
        Order order = new Order();
        order.setStatus("PENDING");
        order.setStore(store);
        order.setUser(currentUser);
        order.setOrderDate(today);

        //Calculate total price
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (OrderItemRequest item : request.getItems()) {
            Product product = productRepository.findById(UUID.fromString(item.getProductId()))
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            if(!product.getStore().getId().equals(UUID.fromString(request.getStoreId()))) {
                throw new IllegalArgumentException("Product does not belong to this store");
            }

            if(product.getStock()< item.getQuantity()) {
                throw new IllegalArgumentException("Product does not have enough stock");
            }

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(product);
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setOrder(order);
            orderDetail.setPrice(product.getPrice());

            BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(item.getQuantity()));
            totalPrice = totalPrice.add(subtotal);

            orderDetail.setTotalPrice(subtotal);
            orderDetails.add(orderDetail);

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

        }

        order.setTotalPrice(totalPrice);
        order.setOrderDetails(orderDetails);


        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getRecentOrders() {
        List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getRecentOrdersByOwner(UUID ownerId) {
        User user = userService.getCurrentUser();
        List<Order> orders = orderRepository.findByStore_OwnerIdOrderByCreatedAtDesc(ownerId);

        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getRecentOrdersByUser(UUID userId) {
        User user = userService.getCurrentUser();
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId().toString());
        response.setStoreId(order.getStore().getId().toString());
        response.setStoreName(order.getStore().getName());
        response.setUserId(order.getUser().getId().toString());
        response.setUserName(order.getUser().getName());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemResponse> items = order.getOrderDetails()
                .stream().map(detail -> {
                    OrderItemResponse item = new OrderItemResponse();
                    item.setQuantity(detail.getQuantity());
                    item.setProductId(detail.getProduct().getId().toString());
                    item.setProductName(detail.getProduct().getName());
                    item.setProductDescription(detail.getProduct().getDescription());
                    item.setProductImageUrl(detail.getProduct().getImageUrl());
                    item.setId(detail.getId().toString());
                    item.setPrice(detail.getProduct().getPrice());
                    item.setTotalPrice(detail.getTotalPrice());
                    return item;
                }).collect(Collectors.toList());

        response.setItems(items);

        return response;
    }

    private void validateOrderStatus(String status) {
        List <String> validStatus = Arrays.asList("PENDING", "PROCESSING", "COMPLETED", "CANCELLED");
        if(!validStatus.contains(status)) {
            throw new IllegalArgumentException("Invalid order status. Valid options are: " +
                    String.join(", ", validStatus));
        }
    }


}
