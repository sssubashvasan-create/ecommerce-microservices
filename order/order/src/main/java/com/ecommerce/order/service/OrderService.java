package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderItemDTO;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.entities.CartItem;
import com.ecommerce.order.entities.Order;
import com.ecommerce.order.entities.OrderItem;
import com.ecommerce.order.enums.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final OrderRepository orderRepository;

    public Optional<OrderResponse> createOrder(String userId){
        List<CartItem> cartItems = cartService.getCart(userId);

        if(cartItems.isEmpty()){
            return Optional.empty();
        }

//        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
//
//        if(userOpt.isEmpty()){
//            return Optional.empty();
//        }
//        User user = userOpt.get();

        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();

        order.setUserID(userId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> new OrderItem(
                        null,
                        cartItem.getProductId(),
                        cartItem.getQuantity(),
                        cartItem.getPrice(),
                        order
                ))
                .toList();

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(userId);

        return Optional.of(mapToOrderResponse(savedOrder));
    }



    private OrderResponse mapToOrderResponse(Order order){
           return OrderResponse.builder()
                    .id(order.getId())
                    .totalAmount(order.getTotalAmount())
                    .status(order.getStatus())
                    .createdAt(order.getCreatedAt())
                    .items(order.getItems().stream()
                            .map(orderItem -> OrderItemDTO.builder()
                                    .id(orderItem.getId())
                                    .price(orderItem.getPrice())
                                    .productId(orderItem.getProductId())
                                    .quantity(orderItem.getQuantity())
                                    .subTotal(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                                    .build()
                            )
                            .toList()
                    )
                    .build();
    }
}
