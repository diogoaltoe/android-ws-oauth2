package com.diogoaltoe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.diogoaltoe.R;
import com.diogoaltoe.controller.Oauth2Controller;

public class UserHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        //Oauth2Controller oauth2 = Oauth2Controller.getInstance();
        //System.out.println("USERNAME: " + oauth2.getUsername());
    }

    /**
     * Runs when you click the User button
     * */
    public void buttonUserMain(View view) {
        Intent intent = new Intent(this, UserMainActivity.class);
        startActivity(intent);
    }

    /**
     * Runs when you click the Product button
     * */
    public void buttonProductMain(View view) {
        Intent intent = new Intent(this, ProductMainActivity.class);
        startActivity(intent);
    }

    /**
     * Runs when you click the Person button
     * */
    public void buttonPersonMain(View view) {
        Intent intent = new Intent(this, PersonMainActivity.class);
        startActivity(intent);
    }

    /**
     * Runs when you click the Logoff button
     * */
    public void buttonLogoff(View view) {
        // Clear the Session
        Oauth2Controller.destroyInstance();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
