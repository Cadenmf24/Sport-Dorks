package com.estore.api.estoreapi.persistence.shoppingCart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.estore.api.estoreapi.model.Inventory.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.estore.api.estoreapi.persistence.Inventory.InventoryDAO;

@Component
public class ShoppingCartFileDAO implements ShoppingCartDAO{
    private Map<String,Item> shoppingCart;
    //private Map<String, Item> inventory;
    private InventoryDAO InventoryDAO;

    private String filename;

    private ObjectMapper objectMapper;

    //private String inventoryFilename;
    
    public ShoppingCartFileDAO(@Value("${shoppingCart.filename}") String filename, ObjectMapper objectMapper, InventoryDAO InventoryDAO) throws IOException {
        this.filename = filename;
        this.objectMapper = objectMapper;
        this.InventoryDAO = InventoryDAO;
        loadShoppingCart();
        //loadInventory();
    }

    private void loadShoppingCart() throws IOException {
        shoppingCart = new TreeMap<>();
        Item[] shoppingCartArray = objectMapper.readValue(new File(filename), Item[].class);
        for (Item item : shoppingCartArray) {
            shoppingCart.put(item.getName(), item);
        }
    }

    // private void loadInventory() throws IOException {
    //     inventory = new TreeMap<>();
    //     Item[] inventoryArray = objectMapper.readValue(new File(inventoryFilename), Item[].class);
    //     for (Item item : inventoryArray) {
    //         inventory.put(item.getName(), item);
    //     }
    // }

    private ArrayList<Item> getShoppingCartArray() {
        return new ArrayList<>(shoppingCart.values());
    }
    
    // private ArrayList<Item> getInventoryArray() {
    //     return new ArrayList<>(inventory.values());
    // }

    private void saveCart() throws IOException {
        objectMapper.writeValue(new File(filename), getShoppingCartArray());
    }

    // private void saveInventory() throws IOException {
    //     objectMapper.writeValue(new File(inventoryFilename), getInventoryArray());
    // }



    @Override
    public Item[] getCart() throws IOException {
        return getShoppingCartArray().toArray(new Item[0]);
    }

    @Override
    public Item addItem(String name) throws IOException {
        Item newItem = new Item(name, 1, InventoryDAO.getItem(name).getCost());
        shoppingCart.put(name, newItem);
        saveCart();
        return newItem;
    }

    @Override
    public boolean deleteItem(String name) throws IOException {
        shoppingCart.remove(name);
        saveCart();
        return true;
    }

    @Override
    public boolean purchaseItem(String name) throws IOException {
        int quantity = InventoryDAO.getItem(name).getQuantity() - shoppingCart.get(name).getQuantity();
        Item updatedItem = new Item(name, quantity, InventoryDAO.getItem(name).getCost());
        InventoryDAO.deleteItem(name);
        InventoryDAO.createItem(updatedItem);
        deleteItem(name);
        return true;
    }
}