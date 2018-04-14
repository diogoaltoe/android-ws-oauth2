package ws.droid.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ws.droid.controller.AppController;
import ws.droid.controller.NetworkController;
import ws.droid.R;


public class UserEditActivity extends AppCompatActivity {

    private EditText editTextFirstName;
    private EditText editTextLastName;

    // URL to get contacts JSON
    private static String paramHref;
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
            paramHref = extras.getString("href");

            // Update the fields on screen
            editTextFirstName.setText(paramFirstName, TextView.BufferType.EDITABLE);
            editTextLastName.setText(paramLastName, TextView.BufferType.EDITABLE);
        }
    }


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
            //params.put("Content-Type", "application/json");
            params.put("firstName", editTextFirstName.getText().toString());
            params.put("lastName", editTextLastName.getText().toString());

            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    paramHref,
                    parameters,
                    new Response.Listener<JSONObject>() {
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
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.DELETE, paramHref, null, new Response.Listener<JSONObject>() {
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
