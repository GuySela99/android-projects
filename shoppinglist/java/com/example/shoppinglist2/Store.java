package com.example.shoppinglist2;

import java.util.ArrayList;
import java.util.HashMap;

public class Store {
    HashMap<String,ArrayList<Product>> shop;

    public Store() {
        shop = new HashMap<>();
    }

    public Store(HashMap<String,ArrayList<Product>> shop) {
        shop = new HashMap<>();
    }

    public void newCategory(String category){
        ArrayList<Product> products = new ArrayList<>();
        shop.put(category,products);
    }

    public void newProduct(String category, Product product){
        ArrayList<Product> products = shop.get(category);
        products.add(product);
    }
}
