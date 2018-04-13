package ws.droid.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ws.droid.controller.AppController;
import ws.droid.controller.HttpController;
import ws.droid.R;
import ws.droid.controller.NetworkController;

public class UserListActivity extends AppCompatActivity {

    private String TAG = UserListActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView listViewUserList;

    // URL to get contacts JSON
    private static String urlWebService = "http://10.0.2.2:8080/people/";

    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        contactList = new ArrayList<>();

        listViewUserList = (ListView) findViewById(R.id.listViewUserList);

        listViewUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Object itemList = listViewUserList.getItemAtPosition(position);

            // Convert Object to Json (String)
            Gson gson = new Gson();
            String jsonStrItem = gson.toJson(itemList); //convert

            try {
                // Convert JsonString to JsonObject
                JSONObject jsonObjItem = new JSONObject(jsonStrItem);

                // Set first name
                String jsonFirstName = jsonObjItem.getString("firstName");
                // Set last name
                String jsonLastName = jsonObjItem.getString("lastName");
                // Set link url of item
                String jsonUrl = jsonObjItem.getString("href");

                // Redirect to the edit screen
                // Passing the item's url as param
                Intent intent = new Intent(UserListActivity.this, UserEditActivity.class);
                intent.putExtra("firstName", jsonFirstName);
                intent.putExtra("lastName", jsonLastName);
                intent.putExtra("url", jsonUrl);
                startActivity(intent);

                //Toast.makeText(UserListActivity.this,"You selected : " + jsonUrl,Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            }
        });

        NetworkController network = new NetworkController();
        // Check if Internet is working
        if (!network.isNetworkAvailable(this)) {
            // Show a message to the user to check his Internet
            Toast.makeText(this, R.string.app_network_offline, Toast.LENGTH_LONG).show();
        } else {
            //new GetUsers().execute();

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, urlWebService, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //TODO: handle success
                    Log.d("Response", response.toString());

                    try {
                        // Getting JSON Array node
                        JSONObject embedded = response.getJSONObject("_embedded");
                        JSONArray contacts = embedded.getJSONArray("people");

                        // looping through All Contacts
                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);

                            String firstName = c.getString("firstName");
                            String lastName = c.getString("lastName");

                            JSONObject objLink = c.getJSONObject("_links");
                            JSONObject objHref = objLink.getJSONObject("self");

                            String link_href = objHref.getString("href");

                            // tmp hash map for single contact
                            HashMap<String, String> contact = new HashMap<>();

                            // adding each child node to HashMap key => value
                            contact.put("firstName", firstName);
                            contact.put("lastName", lastName);
                            contact.put("href", link_href);

                            // adding contact to contact list
                            contactList.add(contact);

                            /**
                             * Updating parsed JSON data into ListView
                             * */
                            ListAdapter adapter = new SimpleAdapter(
                                    UserListActivity.this,
                                    contactList,
                                    R.layout.user_list_item,
                                    new String[]{"firstName", "lastName", "href"},
                                    new int[]{R.id.textViewFirstName, R.id.textViewLastName, R.id.textViewHref});

                            listViewUserList.setAdapter(adapter);
                        }
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
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    //TODO: handle failure
                    //Log.d("Error.Response", error.printStackTrace());
                }
            });

            AppController.getInstance(UserListActivity.this).addToRequestQueue(jsonRequest);
        }
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetUsers extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(UserListActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, urlWebService, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //TODO: handle success
                    Log.d("Response", response.toString());

                    try {
                        // Getting JSON Array node
                        JSONObject embedded = response.getJSONObject("_embedded");
                        JSONArray contacts = embedded.getJSONArray("people");

                        // looping through All Contacts
                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);

                            String firstName = c.getString("firstName");
                            String lastName = c.getString("lastName");

                            JSONObject objLink = c.getJSONObject("_links");
                            JSONObject objHref = objLink.getJSONObject("self");

                            String link_href = objHref.getString("href");

                            // tmp hash map for single contact
                            HashMap<String, String> contact = new HashMap<>();

                            // adding each child node to HashMap key => value
                            contact.put("firstName", firstName);
                            contact.put("lastName", lastName);
                            contact.put("href", link_href);

                            // adding contact to contact list
                            contactList.add(contact);
                        }
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
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    //TODO: handle failure
                    //Log.d("Error.Response", error.printStackTrace());
                }
            });

            AppController.getInstance(UserListActivity.this).addToRequestQueue(jsonRequest);

/*
            HttpController sh = new HttpController();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(urlWebService);

            //Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONObject embedded = jsonObj.getJSONObject("_embedded");
                    JSONArray contacts = embedded.getJSONArray("people");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String firstName = c.getString("firstName");
                        String lastName = c.getString("lastName");

                        JSONObject objLink = c.getJSONObject("_links");
                        JSONObject objHref = objLink.getJSONObject("self");

                        String link_href = objHref.getString("href");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("firstName", firstName);
                        contact.put("lastName", lastName);
                        contact.put("href", link_href);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
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
*/

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    UserListActivity.this,
                    contactList,
                    R.layout.user_list_item,
                    new String[]{"firstName", "lastName", "href"},
                    new int[]{R.id.textViewFirstName, R.id.textViewLastName, R.id.textViewHref});

            listViewUserList.setAdapter(adapter);
        }

    }
}
