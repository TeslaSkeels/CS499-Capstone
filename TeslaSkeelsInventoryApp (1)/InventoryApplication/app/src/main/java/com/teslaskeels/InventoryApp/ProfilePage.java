package com.teslaskeels.InventoryApp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ProfilePage extends AppCompatActivity {

    private EditText newPassword, confirmNewPassword;
    private static final int MY_PERMISSIONS_MANAGE_DATABASE = 0 ;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        Button cancelButton = findViewById(R.id.idBtnCancel);
        Button saveButton = findViewById(R.id.idBtnSavePassword);
        newPassword = findViewById(R.id.idEditNewPassword);
        confirmNewPassword = findViewById(R.id.idEditConfirmNewPassword);

        cancelButton.setOnClickListener(v-> {
            startActivity(new Intent(ProfilePage.this, GridView.class));
        });

        saveButton.setOnClickListener(v -> {
            //Get text info
            String newPasswordText = newPassword.getText().toString();
            String confirmNewPasswordText = confirmNewPassword.getText().toString();

            //Validate if text fields are empty or not
            if (newPasswordText.isEmpty() || confirmNewPasswordText.isEmpty()) {
                Toast.makeText(ProfilePage.this, "Please enter both passwords", Toast.LENGTH_SHORT).show();
                return;
            }

            //Check to see if the two passwords match
            if (!newPasswordText.equals(confirmNewPasswordText)) {
                Toast.makeText(ProfilePage.this, "Make sure the passwords match.", Toast.LENGTH_SHORT).show();
                return;
            }

            //The passwords match, so we should update the Database
            //Create a DBHandler
            dbHandler = new DBHandler(ProfilePage.this);

            //Permission Check
            if (ContextCompat.checkSelfPermission(ProfilePage.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(ProfilePage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_MANAGE_DATABASE);
            }
            else
            {
                //Update the password in the database
                dbHandler.updatePassword(newPasswordText);

                //Clear the previous entries
                newPassword.setText("");
                confirmNewPassword.setText("");
                Toast.makeText(ProfilePage.this, "Password Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent( ProfilePage.this, GridView.class));
            }

        });
    }
}
