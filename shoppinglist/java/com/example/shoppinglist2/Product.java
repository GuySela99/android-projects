package com.example.shoppinglist2;

public class Product {
    private boolean purchased = false;//if the item been purchased
    private String nameProduct;//the name of the product

    /**
     * empty constructor- to the firestore
     */
    public Product() {
    }

    /**
     *
     * @param category the category that the product will be in. but this is not on use
     * @param nameProduct-The name of the product
     */
    public Product(Category category, String nameProduct) {
        //this.category = category;
        this.nameProduct = nameProduct;
        //category.addToIndexNUMber();// change the number of the product in the category.
    }

    /**
     *
     * @return the status of the product- purchased or not
     */
    public boolean getPurchased() {
        return purchased;
    }



    public String getNameProduct() {
        return nameProduct;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }
}
