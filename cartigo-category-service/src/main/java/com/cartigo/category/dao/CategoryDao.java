package com.cartigo.category.dao;

public class CategoryDao {
    private String name;
    private String description;
    private Boolean isActive;

    public CategoryDao(String description, String name,Boolean isActive) {
        this.description = description;
        this.name = name;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
