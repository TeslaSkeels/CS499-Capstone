package com.teslaskeels.InventoryApp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddNewInventoryItem extends AppCompatActivity {

    //Create variables for our fields
    private EditText itemNameEdit, notesEdit, itemQuantityEdit;
    private Spinner categorySelect;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_inventory_item);

        //Init all of our elements
        itemNameEdit = findViewById(R.id.idEditItemName);
        notesEdit = findViewById(R.id.idEditNotes);
        itemQuantityEdit = findViewById(R.id.idEditQuantity);
        categorySelect = findViewById(R.id.idCategorySelect);

        Button addInventoryBtn = findViewById(R.id.idBtnInventory);

        //Create a DB Handler to be used
        dbHandler = new DBHandler(AddNewInventoryItem.this);

        //Set the inventory button click event
        addInventoryBtn.setOnClickListener(v -> {

            //Read in the fields
            String itemName = itemNameEdit.getText().toString();
            String itemNotes = notesEdit.getText().toString();
            String itemQuantity = itemQuantityEdit.getText().toString();

            //Check to make sure none of the fields are empty
            if (itemName.isEmpty() && itemNotes.isEmpty() &&  itemQuantity.isEmpty()) {
                Toast.makeText(AddNewInventoryItem.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                return;
            }

            //Parse the quantity to an int
            int quantity = Integer.parseInt(itemQuantity);

            //Parse out the Category Selection
            String categorySelection = categorySelect.getSelectedItem().toString();

            //Call the DB Handler to add the item to it
            dbHandler.addInventoryItem(itemName, quantity, itemNotes, categorySelection);

            //Send a toast message so that people know it got added.
            Toast.makeText(AddNewInventoryItem.this, String.format("%s has been added.",itemName), Toast.LENGTH_SHORT).show();

            //Clear out the fields for clean up
            itemNameEdit.setText("");
            notesEdit.setText("");
            itemQuantityEdit.setText("");
            categorySelect.setSelection(0);
            //Get back to the grid view page
            startActivity(new Intent(AddNewInventoryItem.this, GridView.class));
        });

        //Setup the Category Select Spinner
        ArrayList<String> categoryList = dbHandler.getCategoryNames();

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categoryList);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categorySelect.setAdapter(adapter);

    }
}