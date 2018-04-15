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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ws.droid.controller.AppController;
import ws.droid.R;
import ws.droid.controller.LoadingController;
import ws.droid.controller.NetworkController;

public class PersonNewActivity extends AppCompatActivity {

    // The URL to create new Person
    private String hrefWebService = "http://10.0.2.2:8080/people/";
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

        NetworkController network = new NetworkController();
        // Check if Internet is working
        if (!network.isNetworkAvailable(this)) {
            // Show a message to the user to check his Internet
            Toast.makeText(this, R.string.app_network_offline, Toast.LENGTH_LONG).show();
        } else {
            loading = new LoadingController();
            // Show a progress spinner
            loading.showProgress(PersonNewActivity.this, viewLoading, true);

            Map<String, String> params = new HashMap();
            params.put("firstName", editTextFirstName.getText().toString());
            params.put("lastName", editTextLastName.getText().toString());

            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    hrefWebService,
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
                        }
                    });

            AppController.getInstance(this).addToRequestQueue(jsonRequest);

            // Hidden a progress spinner
            loading.showProgress(PersonNewActivity.this, viewLoading, false);

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

    }

}
