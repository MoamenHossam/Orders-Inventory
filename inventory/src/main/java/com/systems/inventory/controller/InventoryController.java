package com.systems.inventory.controller;

import com.systems.inventory.model.Item;
import com.systems.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // Get item by id
    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItem(@PathVariable Long itemId) {
        try {
            System.out.println(itemId);
            Item item = inventoryService.getItemById(itemId);

            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
    }

    // Update item quantity after an order
    @PutMapping("/{itemId}")
    public ResponseEntity<Void> updateItem(@PathVariable Long itemId, @RequestBody Item item) {
        try {
            inventoryService.updateItemQuantity(itemId, item.getQuantity());
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping
    public ResponseEntity<Item> addItem(@RequestBody Item item) {
        try {
            Item createdItem = inventoryService.addItem(item);
            return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
