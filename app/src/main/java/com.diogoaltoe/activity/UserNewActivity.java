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
import com.diogoaltoe.model.User;

public class UserNewActivity extends AppCompatActivity {

    // EditTexts of screen
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordRepeat;
    // Progress Bar
    private LoadingController loading;
    private View viewLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_new);

        //link graphical items to variables
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPasswordRepeat = (EditText) findViewById(R.id.editTextPasswordRepeat);
        viewLoading = findViewById(R.id.progressBarLoading);
    }

    /**
     * Runs when you click the Save button
     * */
    public void buttonSave(View view) {

        User user = new User(
                editTextName.getText().toString(),
                editTextEmail.getText().toString(),
                editTextPassword.getText().toString()
        );

        new BackgroundTask(user).execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        private final User params;

        public BackgroundTask(User params) {
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            //TODO: Show a progress spinner
            loading = new LoadingController();
            // Show a progress spinner
            loading.showProgress(UserNewActivity.this, viewLoading, true);
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                // Get instance from authenticate User
                Oauth2Controller oauth2 = Oauth2Controller.getInstance();
                // Call Web Service of User List
                String result = oauth2.callPostService(UserNewActivity.this, true,"user/", this.params);
                //System.out.println("String - User: " + stringResponse);

                return result;

            } catch (Exception e) {
                //System.out.println("Exception: " + e.getMessage());

                return "Exception";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Hidden a progress spinner
            loading.showProgress(UserNewActivity.this, viewLoading, false);

            // If returned string is success (URL)
            if( URLUtil.isHttpUrl(result) || URLUtil.isHttpsUrl(result) ) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(UserNewActivity.this);
                    builder.setMessage(R.string.text_save_message)
                            .setTitle(R.string.text_success_title)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(((Dialog)dialog).getContext(), UserMainActivity.class));
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
