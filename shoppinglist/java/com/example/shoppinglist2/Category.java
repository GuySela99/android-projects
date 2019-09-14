package com.example.shoppinglist2;

import java.util.ArrayList;

public class Category {
    public String nameCategory;
    public int indexNUMber=0;// an instance of how much product have in the category
    public ArrayList<Product> products;


    /**
     * empty constructor for the firestore
     */
    public Category() {
    }

    /**
     *
     * @param nameCategory- the name of the category will be called
     */
    public Category(String nameCategory) {
        this.nameCategory = nameCategory;
        products = new ArrayList<>();

    }

    /**
     *
     * @return the name of the category
     */
    public String getNameCategory() {
        return nameCategory;
    }

    /**
     *
     * @return the counting number
     */
    public int getIndexNUMber() {
        return indexNUMber;
    }

    /**
     * add one product from the counting
     */

    public void addToIndexNUMber(){
        indexNUMber++;
    }

    /**
     * submits one product from the counting
     */
    public  void loseToIndexNUMber(){
        indexNUMber--;
    }

    /**
     * add a new product to the category
     * @param p- the product i want to add
     */
    public void add(Product p){
        products.add(p);
        addToIndexNUMber();
    }

    /**
     * set to category new listArray of product
     * @param products
     */
    public void setProducts(ArrayList<Product> products) {
        this.products = products;
        indexNUMber = products.size();
    }

    /**
     * remove product from the list
     * @param p- the product that i want to remove
     */
    public void remove(Product p){
        products.remove(p);
        loseToIndexNUMber();
    }

    /**
     *
     * @return the listArray of the products
     */
    public ArrayList<Product> getProducts() {
        return products;
    }

    /**
     * check if two categories are the same
     * @param obj- another category object
     * @return true if the name is the same. false if not.
     */
    @Override
    public boolean equals( Object obj) {
        Category category = (Category)obj;
        if(this.nameCategory.equals(category.nameCategory))
            return true;
        else
            return false;
    }
}
