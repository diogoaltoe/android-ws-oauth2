package com.diogoaltoe.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.diogoaltoe.R;
import com.diogoaltoe.controller.LoadingController;
import com.diogoaltoe.controller.Oauth2Controller;
import com.diogoaltoe.model.User;


public class UserEditActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;

    // URL to get contacts JSON
    private String paramName;
    private String paramEmail;
    // Progress Bar
    private LoadingController loading;
    private View viewLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        viewLoading = findViewById(R.id.progressBarLoading);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            paramName = extras.getString("name");
            paramEmail = extras.getString("email");

            // Update the fields on screen
            editTextName.setText(paramName, TextView.BufferType.EDITABLE);
            editTextEmail.setText(paramEmail, TextView.BufferType.EDITABLE);
        }
    }


    /**
     * Runs when you click the Save button
     * */
    public void buttonSave(View view) {

        User user = new User(
                editTextName.getText().toString(),
                editTextEmail.getText().toString()
        );

        new BackgroundEditTask(user).execute();
    }

    /**
     * Runs when you click the Delete button
     * */
    public void buttonDelete(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(UserEditActivity.this);
        builder.setMessage(R.string.text_delete_confirmation)
                .setTitle(R.string.text_attention_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Delete the record confirmed
                        deleteRecord();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * Delete the confirmed record
     * */
    public void deleteRecord() {

        new BackgroundDeleteTask(paramEmail).execute();
    }


    class BackgroundEditTask extends AsyncTask<Void, Void, String> {

        private final User params;

        public BackgroundEditTask(User params) {
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            //TODO: Show a progress spinner
            loading = new LoadingController();
            // Show a progress spinner
            loading.showProgress(UserEditActivity.this, viewLoading, true);
        }

        @Override
        protected String doInBackground(Void... voids) {

            // Get instance from authenticate User
            Oauth2Controller oauth2 = Oauth2Controller.getInstance();
            // Call Web Service of User List
            String result = oauth2.callPutService(UserEditActivity.this, true, "user/", this.params);
            //System.out.println("String - User: " + result);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // Hidden a progress spinner
            loading.showProgress(UserEditActivity.this, viewLoading, false);

            // If returned string is NOT empty
            if(result != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserEditActivity.this);
                builder.setMessage(R.string.text_edit_message)
                        .setTitle(R.string.text_success_title)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(((Dialog)dialog).getContext(), UserListActivity.class));
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            // If returned string is empty
            else {
                //TODO: Show message about empty return
            }
        }
    }


    class BackgroundDeleteTask extends AsyncTask<Void, Void, String> {

        private final String params;

        public BackgroundDeleteTask(String params) {
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            //TODO: Show a progress spinner
            loading = new LoadingController();
            // Show a progress spinner
            loading.showProgress(UserEditActivity.this, viewLoading, true);
        }

        @Override
        protected String doInBackground(Void... voids) {

            // Get instance from authenticate User
            Oauth2Controller oauth2 = Oauth2Controller.getInstance();
            // Call Web Service of User List
            String result = oauth2.callDeleteService(UserEditActivity.this, true, "user/", this.params);
            //System.out.println("String - User: " + result);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // Hidden a progress spinner
            loading.showProgress(UserEditActivity.this, viewLoading, false);

            // If returned string is NOT empty
            if(result != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserEditActivity.this);
                builder.setMessage(R.string.text_delete_message)
                        .setTitle(R.string.text_success_title)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(((Dialog) dialog).getContext(), UserListActivity.class));
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            // If returned string is empty
            else {
                //TODO: Show message about empty return
            }
        }
    }
}
