package com.teslaskeels.InventoryApp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teslaskeels.InventoryApp.BusinessObjects.InventoryItem;

import java.util.ArrayList;
import java.util.Locale;

public class GridView extends AppCompatActivity {

    //Create some of high level items that will be used
    private TableLayout inventoryTable;
    private FloatingActionButton saveInventoryBtn, goToCategoryViewBtn;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);

        //Init our variables
        inventoryTable = findViewById(R.id.idInventoryTable);
        FloatingActionButton createInventoryBtn = findViewById(R.id.idBtnAddInventory);
        FloatingActionButton editProfileBtn = findViewById(R.id.idBtnEditProfile);
        saveInventoryBtn = findViewById(R.id.idBtnSaveInvChanges);
        goToCategoryViewBtn = findViewById(R.id.idBtnGoToViewByCategory);

        //Go get all of our inventory items from the DB
        ArrayList<InventoryItem> inventoryItems = new DBHandler(GridView.this).getInventoryItems();

        //Populate the inventory list with all those items
        for (InventoryItem item: inventoryItems)
        {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams((new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)));

            //Set up the item name column
            EditText itemNameColumn = new EditText(this);
            itemNameColumn.setText(item.itemName);
            itemNameColumn.setId(item.itemId);
            itemNameColumn.setMinHeight(48);
            itemNameColumn.setTextSize(14);
            itemNameColumn.setLayoutParams((new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)));
            itemNameColumn.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0)
                    {
                        saveInventoryBtn.setEnabled(true);
                    }
                }
            });
            tr.addView(itemNameColumn);

            //Set up the Quantity Column
            EditText quantityColumn = new EditText(this);
            quantityColumn.setText(String.valueOf(item.quantity));
            quantityColumn.setId(item.itemId);
            quantityColumn.setMinHeight(48);
            quantityColumn.setTextSize(14);
            quantityColumn.setInputType(InputType.TYPE_CLASS_NUMBER);
            quantityColumn.setLayoutParams((new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)));

            quantityColumn.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0)
                    {
                        saveInventoryBtn.setEnabled(true);
                    }
                }
            });
            tr.addView(quantityColumn);

            //Set up the notes column
            EditText notesColumn = new EditText(this);
            notesColumn.setText(item.notes);
            notesColumn.setId(item.itemId);
            notesColumn.setTextSize(14);
            notesColumn.setLayoutParams((new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)));
            notesColumn.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0)
                    {
                        saveInventoryBtn.setEnabled(true);
                    }
                }
            });
            tr.addView(notesColumn);

            //Create the button to delete out the row
            Button deleteRowBtn = new Button(this);
            deleteRowBtn.setText("Delete");
            deleteRowBtn.setId(item.itemId);
            deleteRowBtn.setEnabled(true);
            deleteRowBtn.setClickable(true);
            deleteRowBtn.setBackgroundColor(Color.rgb(126, 0, 126));
            deleteRowBtn.setLayoutParams((new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)));
            deleteRowBtn.setOnClickListener(v -> {
                new DBHandler(GridView.this).deleteInventoryItem(deleteRowBtn.getId());
                finish();
                startActivity(getIntent());
                Toast.makeText(GridView.this, "Item Deleted", Toast.LENGTH_SHORT).show();
            });

            //Make sure to set the row ID to the id of the item, so we know what to get, updated, or delete later
            tr.setId(item.itemId);
            tr.addView(deleteRowBtn);

            inventoryTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        //Setup the create button action
        createInventoryBtn.setOnClickListener(v -> startActivity(new Intent(GridView.this, AddNewInventoryItem.class)));
        createInventoryBtn.setBackgroundColor(Color.rgb(126, 0, 126));
        //Setup the Save Update button action
        saveInventoryBtn.setOnClickListener(v -> {

            //Disable the button, it can't be clicked multiple times
            saveInventoryBtn.setEnabled(false);

            //It starts at 2, because the first two rows are the header rows of the table
            for (int i = 2, j = inventoryTable.getChildCount(); i < j; i++) {
                View view = inventoryTable.getChildAt(i);
                if (view instanceof TableRow) {
                    //For each item in the row, go through and update the DB with them
                    TableRow row = (TableRow) view;
                    EditText firstColumn = (EditText) row.getChildAt(0);
                    new DBHandler(GridView.this).updateInventoryItem(row.getId(), DBHandler.ITEM_COL, firstColumn.getText().toString());

                    EditText secondColumn = (EditText) row.getChildAt(1);
                    new DBHandler(GridView.this).updateInventoryItem(row.getId(), DBHandler.QUANTITY_COL, secondColumn.getText().toString());

                    int quantity = Integer.parseInt(secondColumn.getText().toString());
                    //If the quantity is less than 2, we should send an SMS message
                    if (quantity < 2)
                    {
                        SendSMSMessage(firstColumn.getText().toString(), quantity);
                    }

                    EditText thirdColumn = (EditText) row.getChildAt(2);
                    new DBHandler(GridView.this).updateInventoryItem(row.getId(), DBHandler.NOTES_COL, thirdColumn.getText().toString());
                }
            }
            //Make a message so people know something happened
            Toast.makeText(GridView.this, "Changes Saved!", Toast.LENGTH_SHORT).show();
        });
        saveInventoryBtn.setBackgroundColor(Color.rgb(126, 0, 126));
        editProfileBtn.setOnClickListener(v -> startActivity(new Intent(GridView.this, ProfilePage.class)));

        goToCategoryViewBtn.setOnClickListener(v -> startActivity(new Intent(GridView.this, GroupByCategory.class)));
    }

    //Send a message
    public void SendSMSMessage(String itemName, int quantity)
    {
        //This is hardcoded as the phone number of the app
        String phoneNo = "+1-555-521-5554";
        String message = String.format(Locale.US, "Your item: %s is at %d", itemName, quantity);

        //Check the permissions, and if not allowed see if we can get permission
        if (ContextCompat.checkSelfPermission(GridView.this,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(GridView.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
        else
        {
            //Send the message
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
        }
    }

}
