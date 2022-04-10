package com.teslaskeels.InventoryApp.BusinessObjects;

public class InventoryItem {

    public int itemId;
    public String itemName;
    public int quantity;
    public String notes;
    public String category;

    public InventoryItem(String itemId, String itemName, String quantity, String notes, String category)
    {
        this.itemId = Integer.parseInt(itemId);
        this.itemName = itemName;
        this.quantity = Integer.parseInt(quantity);
        this.notes = notes;
        this.category = category;
    }

}
