package com.teslaskeels.InventoryApp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teslaskeels.InventoryApp.BusinessObjects.InventoryItem;

import java.util.ArrayList;
import java.util.Hashtable;

public class GroupByCategory extends AppCompatActivity {

    //Create some of high level items that will be used
    private TableLayout categoryTable;
    private FloatingActionButton goBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_by_category);


        //Init our variables
        categoryTable = findViewById(R.id.idCategoryTable);
        goBackBtn = findViewById(R.id.idBtnGoBackToGridView);

        DBHandler dbHandler = new DBHandler(GroupByCategory.this);

        //Go get all of our inventory items from the DB
        ArrayList<InventoryItem> inventoryItems = dbHandler.getInventoryItems();

        Hashtable<String, Integer> quantityByCategory = new Hashtable<String, Integer>();

        //Set up the hashtable for each category with unique items.
        for (InventoryItem item: inventoryItems)
        {
            //If the item is in stock
            if (item.quantity > 1)
            {
                //If we don't already have it in the list, add it in.
                if (quantityByCategory.get(item.category) == null)
                {
                    quantityByCategory.putIfAbsent(item.category, 1);

                }
                else
                {
                    //If it is already in the list, increase the number of unique items by one
                    Integer value = quantityByCategory.get(item.category) + 1;
                    quantityByCategory.remove(item.category);
                    quantityByCategory.put(item.category, value);
                }
            }
        }

        ArrayList<String> categoryList = dbHandler.getCategoryNames();

        //Populate the category list with all those items
        for (String category: categoryList)
        {
            if (quantityByCategory.get(category) != null) {
                TableRow tr = new TableRow(this);
                tr.setLayoutParams((new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)));

                //Set up the category name column
                TextView categoryNameColumn = new TextView(this);
                categoryNameColumn.setText(category);
                categoryNameColumn.setMinHeight(48);
                categoryNameColumn.setTextSize(16);
                categoryNameColumn.setLayoutParams((new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)));
                tr.addView(categoryNameColumn);

                //Set up the Quantity Column
                TextView quantityColumn = new TextView(this);
                quantityColumn.setText(String.valueOf(quantityByCategory.get(category)));
                quantityColumn.setMinHeight(48);
                quantityColumn.setTextSize(16);
                quantityColumn.setInputType(InputType.TYPE_CLASS_NUMBER);
                quantityColumn.setLayoutParams((new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)));

                tr.addView(quantityColumn);

                categoryTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
        goBackBtn.setOnClickListener(v -> startActivity(new Intent(GroupByCategory.this, GridView.class)));
    }

}
