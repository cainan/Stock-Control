package br.csoliveira.stockcontrol.model;

import java.io.Serializable;

public class Product implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int mIdProduct;
    private String mProductName;
    private float mPrice;
    private int mQuantity;
    private Category mCategory;

    public int getIdProduct() {
        return mIdProduct;
    }

    public void setIdProduct(int idProduct) {
        this.mIdProduct = idProduct;
    }

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

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Product)) {
            return false;
        }

        Product product = (Product) obj;

        if (product.getCategory().equals(this.getCategory())
                && product.getIdProduct() == this.getIdProduct()
                && product.getProductName().equals(this.getProductName())) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getIdProduct();
    }

}
