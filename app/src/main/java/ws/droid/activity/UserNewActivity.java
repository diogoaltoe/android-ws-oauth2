package ws.droid.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import ws.droid.controller.NetworkController;

public class UserNewActivity extends AppCompatActivity {

    // we"ll make HTTP request to this URL to retrieve weather conditions
    String hrefWebService = "http://10.0.2.2:8080/people/";
    // Textview to show temperature and description
    TextView textViewFirstName;
    TextView textViewLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_new);

        //link graphical items to variables
        textViewFirstName = (TextView) findViewById(R.id.editTextFirstName);
        textViewLastName = (TextView) findViewById(R.id.editTextLastName);
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
            params.put("firstName", textViewFirstName.getText().toString());
            params.put("lastName", textViewLastName.getText().toString());

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

            AlertDialog.Builder builder = new AlertDialog.Builder(UserNewActivity.this);
            builder.setMessage(R.string.text_save_message)
                    .setTitle(R.string.text_success_title)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(((Dialog)dialog).getContext(), MainActivity.class));
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();

        }

    }

}
