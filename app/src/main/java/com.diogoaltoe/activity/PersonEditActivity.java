package com.diogoaltoe.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.diogoaltoe.controller.LoadingController;
import com.diogoaltoe.R;
import com.diogoaltoe.controller.Oauth2Controller;
import com.diogoaltoe.model.Person;


public class PersonEditActivity extends AppCompatActivity {

    private EditText editTextFirstName;
    private EditText editTextLastName;

    // URL to get contacts JSON
    private String paramId;
    private String paramFirstName;
    private String paramLastName;
    // Progress Bar
    private LoadingController loading;
    private View viewLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_edit);

        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        viewLoading = findViewById(R.id.progressBarLoading);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            paramId = extras.getString("id");
            paramFirstName = extras.getString("firstName");
            paramLastName = extras.getString("lastName");

            // Update the fields on screen
            editTextFirstName.setText(paramFirstName, TextView.BufferType.EDITABLE);
            editTextLastName.setText(paramLastName, TextView.BufferType.EDITABLE);
        }
    }


    /**
     * Runs when you click the Save button
     * */
    public void buttonSave(View view) {

        Person person = new Person(
                Integer.parseInt(paramId),
                editTextFirstName.getText().toString(),
                editTextLastName.getText().toString()
        );

        new BackgroundEditTask(person).execute();
    }

    /**
     * Runs when you click the Delete button
     * */
    public void buttonDelete(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(PersonEditActivity.this);
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

        new BackgroundDeleteTask(Integer.parseInt(paramId)).execute();
    }


    class BackgroundEditTask extends AsyncTask<Void, Void, String> {

        private final Person params;

        public BackgroundEditTask(Person params) {
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            //TODO: Show a progress spinner
            loading = new LoadingController();
            // Show a progress spinner
            loading.showProgress(PersonEditActivity.this, viewLoading, true);
        }

        @Override
        protected String doInBackground(Void... voids) {

            // Get instance from authenticate User
            Oauth2Controller oauth2 = Oauth2Controller.getInstance();
            // Call Web Service of Person List
            String result = oauth2.callPutService(PersonEditActivity.this, true, "person/", this.params);
            //System.out.println("String - Person: " + result);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // Hidden a progress spinner
            loading.showProgress(PersonEditActivity.this, viewLoading, false);

            // If returned string is NOT empty
            if(result != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonEditActivity.this);
                builder.setMessage(R.string.text_edit_message)
                        .setTitle(R.string.text_success_title)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(((Dialog)dialog).getContext(), PersonListActivity.class));
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

        private final int params;

        public BackgroundDeleteTask(int params) {
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            //TODO: Show a progress spinner
            loading = new LoadingController();
            // Show a progress spinner
            loading.showProgress(PersonEditActivity.this, viewLoading, true);
        }

        @Override
        protected String doInBackground(Void... voids) {

            // Get instance from authenticate User
            Oauth2Controller oauth2 = Oauth2Controller.getInstance();
            // Call Web Service of Person List
            String result = oauth2.callDeleteService(PersonEditActivity.this, false, "person/", Integer.toString(this.params));
            //System.out.println("String - Person: " + result);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // Hidden a progress spinner
            loading.showProgress(PersonEditActivity.this, viewLoading, false);

            // If returned string is NOT empty
            if(result != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonEditActivity.this);
                builder.setMessage(R.string.text_delete_message)
                        .setTitle(R.string.text_success_title)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(((Dialog) dialog).getContext(), PersonListActivity.class));
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
