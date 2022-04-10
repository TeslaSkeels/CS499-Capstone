package com.teslaskeels.InventoryApp.BusinessObjects;

public class User {

    public int userId;
    public String username;
    public String userPassword;

    public User(String userId, String username, String userPassword)
    {
        this.userId = Integer.parseInt(userId);
        this.username = username;
        this.userPassword = userPassword;
    }
}
