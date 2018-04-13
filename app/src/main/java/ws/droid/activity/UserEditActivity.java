package ws.droid.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ws.droid.controller.AppController;
import ws.droid.controller.NetworkController;
import ws.droid.R;

public class UserEditActivity extends AppCompatActivity {

    private String TAG = UserEditActivity.class.getSimpleName();
    private ProgressDialog pDialog;

    private EditText editTextFirstName;
    private EditText editTextLastName;

    // URL to get contacts JSON
    private static String paramUrl;
    private String paramFirstName;
    private String paramLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            paramFirstName = extras.getString("firstName");
            paramLastName = extras.getString("lastName");
            paramUrl = extras.getString("url");

            // Update the fields on screen
            editTextFirstName.setText(paramFirstName, TextView.BufferType.EDITABLE);
            editTextLastName.setText(paramLastName, TextView.BufferType.EDITABLE);
        }

        //new GetContacts().execute();
    }


    /**
     * Async task class to get json by making HTTP call
     */
    /*
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(UserEditActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpController sh = new HttpController();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(paramUrl);

            //Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject c = new JSONObject(jsonStr);

                    paramFirstName = c.getString("firstName");
                    paramLastName = c.getString("lastName");

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            // Update the fields on screen
            editTextFirstName.setText(paramFirstName, TextView.BufferType.EDITABLE);
            editTextLastName.setText(paramLastName, TextView.BufferType.EDITABLE);
        }

    }
    */

    /**
     * Runs when you click the Save button
     * */
    public void buttonSave(View view) {

        NetworkController network = new NetworkController();
        // Check if Internet is working
        if (!network.isNetworkAvailable(this)) {
            // Show a message to the user to check his Internet
            Toast.makeText(this, R.string.app_network_offline, Toast.LENGTH_LONG).show();
        } else {
            Map<String, String> params = new HashMap();
            params.put("firstName", editTextFirstName.getText().toString());
            params.put("lastName", editTextLastName.getText().toString());

            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, paramUrl, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //TODO: handle success
                    Log.d("Response", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    //TODO: handle failure
                    //Log.d("Error.Response", error.printStackTrace());
                }
            });

            AppController.getInstance(this).addToRequestQueue(jsonRequest);

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

        NetworkController network = new NetworkController();
        // Check if Internet is working
        if (!network.isNetworkAvailable(this)) {
            // Show a message to the user to check his Internet
            Toast.makeText(this, R.string.app_network_offline, Toast.LENGTH_LONG).show();
        } else {
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.DELETE, paramUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //TODO: handle success
                    Log.d("Response", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    //TODO: handle failure
                    //Log.d("Error.Response", error.printStackTrace());
                }
            });

            AppController.getInstance(this).addToRequestQueue(jsonRequest);

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
    }

}
