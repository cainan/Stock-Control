package br.csoliveira.stockcontrol.model;

import java.io.Serializable;

public class Category implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int mIdCategory;
    private String mCategoryName;

    public void setIdCategory(int idCategory) {
        this.mIdCategory = idCategory;
    }

    public int getIdCategory() {
        return mIdCategory;
    }

    public void setCategoryName(String categoryName) {
        this.mCategoryName = categoryName;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Category)) {
            return false;
        }

        Category category = (Category) obj;

        if (category.getIdCategory() == this.getIdCategory()
                && category.getCategoryName().equals(this.getCategoryName())) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getIdCategory();
    }

}
