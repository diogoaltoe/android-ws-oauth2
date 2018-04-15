package ws.droid.activity;

import android.content.Intent;
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
import ws.droid.R;
import ws.droid.controller.LoadingController;
import ws.droid.controller.NetworkController;

public class PersonListActivity extends AppCompatActivity {

    private String TAG = PersonListActivity.class.getSimpleName();

    private ListView listViewPersonList;

    // URL to get contacts JSON
    private static String hrefWebService = "http://10.0.2.2:8080/people/";

    private ArrayList<HashMap<String, String>> arrayListPerson;
    // Progress Bar
    private LoadingController loading;
    private View viewLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);

        arrayListPerson = new ArrayList<>();

        listViewPersonList = (ListView) findViewById(R.id.listViewPersonList);
        viewLoading = findViewById(R.id.progressBarLoading);

        listViewPersonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Object itemList = listViewPersonList.getItemAtPosition(position);

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
                String jsonHref = jsonObjItem.getString("href");

                // Redirect to the edit screen
                // Passing the item's url as param
                Intent intent = new Intent(PersonListActivity.this, PersonEditActivity.class);
                intent.putExtra("firstName", jsonFirstName);
                intent.putExtra("lastName", jsonLastName);
                intent.putExtra("href", jsonHref);
                startActivity(intent);

                //Toast.makeText(PersonListActivity.this,"You selected : " + jsonUrl,Toast.LENGTH_SHORT).show();

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
            loading = new LoadingController();
            // Show a progress spinner
            loading.showProgress(PersonListActivity.this, viewLoading, true);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    hrefWebService,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //TODO: handle success
                            Log.d("Response", response.toString());

                            try {
                                // Getting JSON Array node
                                JSONObject jsonEmbedded = response.getJSONObject("_embedded");
                                JSONArray jsonPeople = jsonEmbedded.getJSONArray("people");

                                // looping through All Users
                                for (int i = 0; i < jsonPeople.length(); i++) {
                                    JSONObject jsonPerson = jsonPeople.getJSONObject(i);

                                    String firstName = jsonPerson.getString("firstName");
                                    String lastName = jsonPerson.getString("lastName");

                                    JSONObject objLink = jsonPerson.getJSONObject("_links");
                                    JSONObject objHref = objLink.getJSONObject("self");

                                    String href = objHref.getString("href");

                                    // tmp hash map for single person
                                    HashMap<String, String> person = new HashMap<>();

                                    // adding each child node to HashMap key => value
                                    person.put("firstName", firstName);
                                    person.put("lastName", lastName);
                                    person.put("href", href);

                                    // adding person to users list
                                    arrayListPerson.add(person);

                                    /**
                                     * Updating parsed JSON data into ListView
                                     * */
                                    ListAdapter adapter = new SimpleAdapter(
                                            PersonListActivity.this,
                                            arrayListPerson,
                                            R.layout.person_list_item,
                                            new String[]{"firstName", "lastName", "href"},
                                            new int[]{R.id.textViewFirstName, R.id.textViewLastName, R.id.textViewHref});

                                    listViewPersonList.setAdapter(adapter);

                                    // Hidden a progress spinner
                                    loading.showProgress(PersonListActivity.this, viewLoading, false);
                                }
                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Hidden a progress spinner
                                        loading.showProgress(PersonListActivity.this, viewLoading, false);

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
                            // Hidden a progress spinner
                            loading.showProgress(PersonListActivity.this, viewLoading, false);
                        }
                    });

            AppController.getInstance(PersonListActivity.this).addToRequestQueue(jsonRequest);
        }
    }
}
