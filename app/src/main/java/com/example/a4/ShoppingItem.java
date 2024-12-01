package com.example.a4;

class ShoppingItem {
    public String itemName;
    public String quantity;
    public String price;

    public ShoppingItem() {}

    public ShoppingItem(String itemName, String quantity, String price) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }
}