package com.systems.inventory.service;

import com.systems.inventory.model.Item;
import com.systems.inventory.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private ItemRepository itemRepository;

    // Get the available stock for a given item
    public Item getItemById(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if(item.isPresent()){
            return item.get();
        }else{
            throw new RuntimeException("Item not found");
        }
    }

    // Update the quantity of an item after an order
    public Item updateItemQuantity(Long itemId, int quantitySold) {
        Item item = getItemById(itemId);
        if (item != null && quantitySold<=item.getQuantity()) {
            item.setQuantity(item.getQuantity() - quantitySold);
            return itemRepository.save(item);
        }else if(quantitySold>item.getQuantity()){
            throw new RuntimeException("Not enough stock");
        }
        else {
            throw new RuntimeException("Item not found");
        }
    }
    public Item addItem(Item item) {
        //some validation
        try {
            Item savedItem = itemRepository.save(item);
            return savedItem;
        } catch (Exception e) {
            throw new RuntimeException("Invalid item");
        }
    }
}

