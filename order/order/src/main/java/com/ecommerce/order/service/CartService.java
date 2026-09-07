package com.ecommerce.order.service;


import com.ecommerce.order.clients.ProductServiceClient;
import com.ecommerce.order.clients.UserServiceClient;
import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.entities.CartItem;
import com.ecommerce.order.repository.CartItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
//    private final ProductRepository productRepository;
//    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    @CircuitBreaker(name = "productService")
    public boolean addToCart(String userId, CartItemRequest request){
        ProductResponse productDetails = productServiceClient.getProductDetails(request.getProductId());

        if(productDetails == null || productDetails.getStockQuantity() < request.getQuantity()){
            return false;
        }

        UserResponse userDetails = userServiceClient.getUserDetails(userId);

        if(userDetails == null){
            return false;
        }

        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId());

        /*
        If cartItem is already present for that particular user and product then update the quantity and price
        else
        create a new cartItem
         */
        if(existingCartItem != null){
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(productDetails.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartItemRepository.save(existingCartItem);
        }
        else{
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(productDetails.getPrice());
            cartItemRepository.save(cartItem);
        }
        return true;
    }

    public boolean deleteItemFromCart(String userId, String productId){
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if(cartItem != null){
            cartItemRepository.delete(cartItem);
            return true;
        }
        return false;
    }

    public List<CartItem> getCart(String userId){
        return cartItemRepository.findByUserId(userId);
    }

    public void clearCart(String userId){
        cartItemRepository.deleteByUserId(userId);
    }
}
