package br.csoliveira.stockcontrol.model;

public class Product {

    private String mProductName;
    private float mPrice;
    private int mQuantity;
    private Category mCategory;

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        this.mProductName = productName;
    }

    public float getPrice() {
        return mPrice;
    }

    public void setPrice(float price) {
        this.mPrice = price;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        this.mQuantity = quantity;
    }

    public void setCategory(Category category) {
        this.mCategory = category;
    }

    public Category getCategory() {
        return mCategory;
    }

}
