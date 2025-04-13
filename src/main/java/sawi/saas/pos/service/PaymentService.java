package sawi.saas.pos.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sawi.saas.pos.dto.*;
import sawi.saas.pos.entity.*;
import sawi.saas.pos.repository.OrderRepository;
import sawi.saas.pos.repository.PaymentRepository;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final MidtransService midtransService;
    private final UserService userService;

    @Transactional
    public PaymentResponse createPayment(UUID orderId, String grossAmount) {
        String transactionId;


        Order order = orderRepository.findById(orderId).
                orElseThrow(() -> new EntityNotFoundException("Order not found"));

        User user = userService.getCurrentUser();
        if(!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Order does not belong to this user");
        }

        Optional<Payment> paymentCheck = paymentRepository.findByOrderId(orderId);

        if(paymentCheck.isPresent()) {
            throw new IllegalArgumentException("Payment with this order already exists");
        }


        MidtransResponse midtransResponse = new MidtransResponse();
        midtransResponse = midtransService.createTransaction(order, grossAmount);
        transactionId = midtransResponse.getToken();



        Payment payment = new Payment();
        payment.setTransactionId(transactionId);
        payment.setRedirectUrl(midtransResponse.getRedirect_url());
        payment.setPaymentMethod("MIDTRANS");
        payment.setOrder(order);
        payment.setAmount(order.getTotalPrice().setScale(0, RoundingMode.CEILING));
        payment.setCurrency("IDR");
        payment.setPaymentStatus(PaymentStatus.PENDING);
        order.setStatus("PROCESSING");

        Payment savedPayment = paymentRepository.save(payment);
        Order savedOrder = orderRepository.save(order);
        return mapToPaymentResponse(savedPayment, savedOrder);
    }

    @Transactional
    public PaymentResponse updatePaymentStatus(String orderId, String status) {
        Payment payment = paymentRepository.findByOrderId(UUID.fromString(orderId)).
                orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        Order order = orderRepository.findById(payment.getOrder().getId()).
                orElseThrow(() -> new EntityNotFoundException("Order not found"));

        User user = userService.getCurrentUser();

        if(!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Order does not belong to this user");
        }

        validatePaymentStatus(status.toUpperCase());
        payment.setPaymentStatus(PaymentStatus.valueOf(status.toUpperCase()));
        payment.setPaymentDate(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);
        order.setStatus("COMPLETED");
        Order savedOrder = orderRepository.save(order);

        return mapToPaymentResponse(savedPayment, savedOrder);

    }

    private void validatePaymentStatus(String paymentStatus) {
        List<String> validPayment = Arrays.asList(PaymentStatus.PENDING.toString(), PaymentStatus.FAILED.toString(), PaymentStatus.SETTLEMENT.toString());
        if(!validPayment.contains(paymentStatus)) {
            throw new IllegalArgumentException("Invalid order status. Valid options are: " +
                    String.join(", ", validPayment));
        }
    }

    private PaymentResponse mapToPaymentResponse(Payment payment, Order order) {
        OrderResponse orderResponse = mapToOrderResponse(order);
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setTransactionId(payment.getTransactionId());
        paymentResponse.setId(payment.getId().toString());
        paymentResponse.setPaymentMethod(payment.getPaymentMethod());
        paymentResponse.setPaymentStatus(payment.getPaymentStatus().toString());
        paymentResponse.setRedirectUrl(payment.getRedirectUrl());
        paymentResponse.setOrder(orderResponse);
        paymentResponse.setPaymentAmount(order.getTotalPrice().setScale(0, RoundingMode.CEILING).toString());
        paymentResponse.setCreatedAt(LocalDateTime.now());
        paymentResponse.setUpdatedAt(LocalDateTime.now());

        return paymentResponse;
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
                    item.setId(detail.getId().toString());
                    item.setPrice(detail.getProduct().getPrice());
                    item.setTotalPrice(detail.getTotalPrice());
                    return item;
                }).collect(Collectors.toList());

        response.setItems(items);

        return response;
    }
}
