package com.systems.Order.service;

import com.systems.Order.model.Item;
import com.systems.Order.model.Order;
import com.systems.Order.repository.OrderRepository;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${inventory-service.url}")
    private String inventoryServiceUrl;

    public Order placeOrder(Order order) {
        // Check if inventory is available
        String url = inventoryServiceUrl + "/inventory/" + order.getProductId();
        System.out.println(url);
        ResponseEntity<Item> response = restTemplate.exchange(url, HttpMethod.GET, null, Item.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Item item = response.getBody();
            if (item != null && item.getQuantity() >= order.getQuantity()) {
                orderRepository.save(order);
                // Decrease stock in Inventory Service
                restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(new Item(item.getId(), item.getName(), order.getQuantity(), item.getPrice())), Void.class);
                return order;
            } else {
                throw new RuntimeException("Not enough stock available");
            }
        }
        throw new RuntimeException("Inventory service unavailable");
    }
}
