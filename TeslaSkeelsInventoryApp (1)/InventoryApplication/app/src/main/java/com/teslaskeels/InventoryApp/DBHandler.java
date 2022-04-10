package com.teslaskeels.InventoryApp;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import com.teslaskeels.InventoryApp.BusinessObjects.InventoryItem;
import com.teslaskeels.InventoryApp.BusinessObjects.User;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    //Name of our Database
    private static final String DB_NAME = "inventoryDB";

    //DB Version
    private static final int DB_VERSION = 1;

    //Below is the name for our Inventory Table
    private static final String INVENTORY_TABLE_NAME = "inventoryItem";

    //Below are the Column names for the Inventory Table
    private static final String ID_COL = "itemId";
    public static final String ITEM_COL = "item";
    public static final String QUANTITY_COL = "quantity";
    public static final String NOTES_COL = "notes";
    public static final String CATEGORY_COL = "category";

    //Below variable is for the user table name
    private static final String USER_TABLE_NAME = "userTable";

    //Below are the Column Names for the user table
    private static final String USERID_COL = "userId";
    private static final String USERNAME_COL = "username";
    private static final String PASSWORD_COL = "password";

    //Below variable is for the category table name
    private static final String CATEGORY_TABLE_NAME = "categoryTable";

    //Below are the Column Names for the category table
    private static final String CATEGORYID_COL = "categoryId";
    private static final String CATEGORYNAME_COL = "categoryName";

    // Create a constructor for our DB
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Below we are creating the Username table with the correct table columns with types
        String query = "CREATE TABLE " + USER_TABLE_NAME + " ("
                + USERID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME_COL + " TEXT,"
                + PASSWORD_COL + " TEXT)";

        //Call the query to create it
        db.execSQL(query);

        //Below we are creating the inventory table with the correct table columns with types
        query = "CREATE TABLE " + INVENTORY_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ITEM_COL + " TEXT,"
                + QUANTITY_COL + " INTERGER,"
                + NOTES_COL + " TEXT, "
                + CATEGORY_COL + " TEXT )";

        //Call the query to create it
        db.execSQL(query);

        //Below we are creating the category table with the correct table columns with types
        query = "CREATE TABLE " + CATEGORY_TABLE_NAME + " ("
                + CATEGORYID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CATEGORYNAME_COL + " TEXT)";

        //Call the query to create it
        db.execSQL(query);

        //Fill out the base values in the category table
        populateCategoryTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Clear out the tables and remake them on upgrade
        db.execSQL("DROP TABLE IF EXISTS " + INVENTORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
        onCreate(db);
    }

    //Method to add in a new inventory item to the db
    public void addInventoryItem(String itemName, int quantity, String notesField, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //Get our key value pairs ready for the DB
        values.put(ITEM_COL, itemName);
        values.put(QUANTITY_COL, quantity);
        values.put(NOTES_COL, notesField);
        values.put(CATEGORY_COL, category);

        //Insert them into the DB
        db.insert(INVENTORY_TABLE_NAME, null, values);

        //Close the DB connection
        db.close();
    }

    //Method to update the item in the db
    public void updateInventoryItem(int keyId, String column, String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(column, value);
        db.update(INVENTORY_TABLE_NAME, cv, ID_COL + " = " + keyId, null);
        db.close();
    }

    //Method to delete an item from the DB
    public void deleteInventoryItem(int keyId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(INVENTORY_TABLE_NAME, ID_COL + "=" + keyId, null);
        db.close();
    }

    //Get all the inventory items from the DB
    public ArrayList<InventoryItem> getInventoryItems()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dbCursor = db.rawQuery("Select * From inventoryItem", null);
        ArrayList<InventoryItem> retInventoryList = new ArrayList<>();

        //Go through each item, create an inventory item for it and add it to the return list
        if (dbCursor.moveToFirst())
        {
            do {
                retInventoryList.add(new InventoryItem(dbCursor.getString(0),
                        dbCursor.getString(1),
                        dbCursor.getString(2),
                        dbCursor.getString(3),
                        dbCursor.getString(4)
                        ));
            } while (dbCursor.moveToNext());
        }

        dbCursor.close();
        return  retInventoryList;
    }

    //Method to add a new user to the DB
    public void addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //Get our key value pairs ready for the DB
        values.put(USERNAME_COL, username.toLowerCase());
        values.put(PASSWORD_COL, password);

        //Insert them into the DB
        db.insert(USER_TABLE_NAME, null, values);

        //Close the DB connection
        db.close();
    }

    //Method to add a update the users password in the database
    public void updatePassword(String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PASSWORD_COL, password);

        //Update the password in the database
        db.update(USER_TABLE_NAME, cv, USERID_COL + " = " + Login.LoggedInUser.userId, null);

        //Close the DB connection
        db.close();
    }

    //Method to get a user from the DB
    public User getUser(String userName)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dbCursor = db.rawQuery(String.format("Select * From userTable Where username = '%s' ", userName.toLowerCase()), null);
        User foundUser = null;

        //If any user was found, get there information
        if (dbCursor.getCount() > 0)
        {
            if (dbCursor.moveToFirst())
            {
                foundUser = new User(dbCursor.getString(0),
                        dbCursor.getString(1),
                        dbCursor.getString(2));
            }
        }

        dbCursor.close();
        return  foundUser;
    }

    //Method to add in a new category item to the db
    public void addCategoryItem(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //Get our key value pairs ready for the DB
        values.put(CATEGORYNAME_COL, categoryName);

        //Insert them into the DB
        db.insert(CATEGORY_TABLE_NAME, null, values);

        //Close the DB connection
        db.close();
    }

    //Get all the category items from the DB
    public ArrayList<String> getCategoryNames()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dbCursor = db.rawQuery("Select * From " +CATEGORY_TABLE_NAME, null);
        ArrayList<String> retCategoryList = new ArrayList<>();

        //Go through each item, grab each category and add it to the return list
        if (dbCursor.moveToFirst())
        {
            do {
                retCategoryList.add(dbCursor.getString(1));
            } while (dbCursor.moveToNext());
        }

        dbCursor.close();
        return  retCategoryList;
    }

    //Sets up some default information to populate the Category Table
    public void populateCategoryTable()
    {
        addCategoryItem("Food");
        addCategoryItem("Tool");
        addCategoryItem("Toy");
        addCategoryItem("Electronics");
    }

}
