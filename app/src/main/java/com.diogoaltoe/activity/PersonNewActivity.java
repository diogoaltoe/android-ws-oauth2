package com.diogoaltoe.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Toast;

import com.diogoaltoe.R;
import com.diogoaltoe.controller.LoadingController;
import com.diogoaltoe.controller.Oauth2Controller;
import com.diogoaltoe.model.Person;

public class PersonNewActivity extends AppCompatActivity {

    // EditTexts of screen
    private EditText editTextFirstName;
    private EditText editTextLastName;
    // Progress Bar
    private LoadingController loading;
    private View viewLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_new);

        //link graphical items to variables
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        viewLoading = findViewById(R.id.progressBarLoading);
    }

    /**
     * Runs when you click the Save button
     * */
    public void buttonSave(View view) {

        Person person = new Person(
                editTextFirstName.getText().toString(),
                editTextLastName.getText().toString()
        );

        new BackgroundTask(person).execute();
    }


    class BackgroundTask extends AsyncTask<Void, Void, String> {

        private final Person params;

        public BackgroundTask(Person params) {
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            //TODO: Show a progress spinner
            loading = new LoadingController();
            // Show a progress spinner
            loading.showProgress(PersonNewActivity.this, viewLoading, true);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Get instance from authenticate Person
                Oauth2Controller oauth2 = Oauth2Controller.getInstance();
                // Call Web Service of Person List
                String result = oauth2.callPostService(PersonNewActivity.this, true,"person/", this.params);
                //System.out.println("String - Person: " + stringResponse);

                return result;

            } catch (Exception e) {
                //System.out.println("Exception: " + e.getMessage());

                return "Exception";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Hidden a progress spinner
            loading.showProgress(PersonNewActivity.this, viewLoading, false);

            // If returned string is success (URL)
            if( URLUtil.isHttpUrl(result) || URLUtil.isHttpsUrl(result) ) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PersonNewActivity.this);
                builder.setMessage(R.string.text_save_message)
                        .setTitle(R.string.text_success_title)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(((Dialog)dialog).getContext(), PersonMainActivity.class));
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            // If returned string is NetworkException
            else if(result == "NetworkException") {
                //TODO: Show message about exception return
                Toast.makeText(
                    getApplicationContext(),
                    R.string.exception_network,
                    Toast.LENGTH_LONG)
                        .show();
            }
            // If returned string is Exception
            // Or return "401"
            else {
                //TODO: Show message about exception return
                Toast.makeText(
                    getApplicationContext(),
                    R.string.exception_service,
                    Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
