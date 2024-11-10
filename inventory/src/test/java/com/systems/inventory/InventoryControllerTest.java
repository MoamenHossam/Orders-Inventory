package com.systems.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systems.inventory.controller.InventoryController;
import com.systems.inventory.model.Item;
import com.systems.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InventoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build();
        item = new Item(1L, "Test Item", 100, 10.0);
    }

    @Test
    void testGetItem_ItemFound() throws Exception {
        // Arrange
        when(inventoryService.getItemById(1L)).thenReturn(item);

        // Act & Assert
        mockMvc.perform(get("/inventory/{itemId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.quantity").value(100))
                .andExpect(jsonPath("$.price").value(10.0));

        verify(inventoryService, times(1)).getItemById(1L);
    }

    @Test
    void testGetItem_ItemNotFound() throws Exception {
        // Arrange
        when(inventoryService.getItemById(1L)).thenThrow(new RuntimeException("Item not found"));

        // Act & Assert
        mockMvc.perform(get("/inventory/{itemId}", 1L))
                .andExpect(status().isNotFound());

        verify(inventoryService, times(1)).getItemById(1L);
    }

    @Test
    void testUpdateItem_Success() throws Exception {
        // Arrange
        Item updatedItem = new Item(1L, "Test Item", 90, 10.0);
        when(inventoryService.updateItemQuantity(1L, 10)).thenReturn(updatedItem);

        // Act & Assert
        mockMvc.perform(put("/inventory/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedItem)))
                .andExpect(status().isOk());

    }

    @Test
    void testUpdateItem_ItemNotFound() throws Exception {
        // Arrange
        when(inventoryService.updateItemQuantity(1L, 10)).thenThrow(new RuntimeException("Item not found"));
        Item mockitem = new Item(1L, "Test Item", 10, 10.0);
        // Act & Assert
        mockMvc.perform(put("/inventory/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mockitem)))
                .andExpect(status().isBadRequest());

    }

//    @Test
//    void testAddItem_Success() throws Exception {
//        // Arrange
////        when(inventoryService.addItem(item)).thenReturn(item);
//
//        // Act & Assert
//        mockMvc.perform(post("/inventory")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(item)))
//                .andExpect(status().isCreated())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.name").value("Test Item"))
//                .andExpect(jsonPath("$.quantity").value(100))
//                .andExpect(jsonPath("$.price").value(10.0));
//
////        verify(inventoryService, times(1)).addItem(item);
//    }

//    @Test
//    void testAddItem_Failure() throws Exception {
//        // Arrange
//        when(inventoryService.addItem(item)).thenThrow(new RuntimeException("Invalid item"));
//
//        // Act & Assert
//        mockMvc.perform(post("/inventory")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(item)))
//                .andExpect(status().isBadRequest());
//
//        verify(inventoryService, times(1)).addItem(item);
//    }

    // Utility method to convert object to JSON string
    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }
}
