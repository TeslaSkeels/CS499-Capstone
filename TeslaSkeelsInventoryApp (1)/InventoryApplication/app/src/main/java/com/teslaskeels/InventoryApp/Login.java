package com.teslaskeels.InventoryApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.teslaskeels.InventoryApp.BusinessObjects.User;

public class Login extends AppCompatActivity {

    public static User LoggedInUser;

    private EditText usernameEdit, usernamePassword;
    private DBHandler dbHandler;
    private static final int MY_PERMISSIONS_MANAGE_DATABASE = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //Init variables
        usernameEdit = findViewById(R.id.idEditUsername);
        usernamePassword = findViewById(R.id.idEditPassword);
        Button loginBtn = findViewById((R.id.idBtnLogin));

        //Create a DBHandler
        dbHandler = new DBHandler(Login.this);
        loginBtn.setOnClickListener(v -> {
            //Get text info
            String username = usernameEdit.getText().toString();
            String userPassword = usernamePassword.getText().toString();

            //Validate if text fields are empty or not
            if (username.isEmpty() && userPassword.isEmpty()) {
                Toast.makeText(Login.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                return;
            }

            //Check to see if the user is in the DB or not
            User foundUser = dbHandler.getUser(username);

            //If user is not there, add them
            if (foundUser == null)
            {
                //Permission Check
                if (ContextCompat.checkSelfPermission(Login.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_MANAGE_DATABASE);
                }
                else
                {
                    //If user not found add a new user
                    dbHandler.addUser(username, userPassword);
                    //Push a toast message so people know something happened
                    Toast.makeText(Login.this, String.format("User %s created.", username), Toast.LENGTH_SHORT).show();
                    usernameEdit.setText("");
                    usernamePassword.setText("");
                    startActivity(new Intent(Login.this, GridView.class));
                }
            }
            else
            {
                //User was found, check the password
                if (foundUser.userPassword.equals(userPassword))
                {
                    LoggedInUser = foundUser;
                    usernameEdit.setText("");
                    usernamePassword.setText("");
                    startActivity(new Intent(Login.this, GridView.class));
                }
                else
                {
                    Toast.makeText(Login.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}