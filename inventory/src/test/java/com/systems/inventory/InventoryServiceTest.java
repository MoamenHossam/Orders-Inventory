package com.systems.inventory;

import com.systems.inventory.model.Item;
import com.systems.inventory.repository.ItemRepository;
import com.systems.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        item = new Item(1L, "Test Item", 100, 10.0);  // Sample item
    }

    // Test case for getItemById
    @Test
    void testGetItemById_ItemFound() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // Act
        Item result = inventoryService.getItemById(1L);

        // Assert
        assertEquals(item, result);
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testGetItemById_ItemNotFound() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.getItemById(1L);
        });
        assertEquals("Item not found", exception.getMessage());
    }

    // Test case for updateItemQuantity
    @Test
    void testUpdateItemQuantity_ItemFound() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // Act
        inventoryService.updateItemQuantity(1L, 10);

        // Assert
        assertEquals(90, item.getQuantity());  // Quantity should be reduced by 10
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testUpdateItemQuantity_ItemNotFound() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.updateItemQuantity(1L, 10);
        });
        assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void testUpdateItemQuantity_QuantityExceedsAvailable() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.updateItemQuantity(1L, 200);
        });
        assertEquals("Not enough stock", exception.getMessage());  // Modify the error message if needed
    }

    // Test case for addItem
    @Test
    void testAddItem_Success() {
        // Arrange
        when(itemRepository.save(item)).thenReturn(item);

        // Act
        Item result = inventoryService.addItem(item);

        // Assert
        assertNotNull(result);
        assertEquals(item, result);
        verify(itemRepository, times(1)).save(item);
    }
}
