package ws.droid.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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

import ws.droid.R;
import ws.droid.controller.AppController;
import ws.droid.controller.LoadingController;
import ws.droid.controller.NetworkController;

public class UserListActivity extends AppCompatActivity {

    private String TAG = UserListActivity.class.getSimpleName();

    private ListView listViewUserList;

    // URL to get contacts JSON
    private static String hrefWebService = "http://10.0.2.2:8080/user/";

    private ArrayList<HashMap<String, String>> arrayListUsers;

    private LoadingController loading;
    private View viewLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        arrayListUsers = new ArrayList<>();

        listViewUserList = (ListView) findViewById(R.id.listViewUserList);
        viewLoading = findViewById(R.id.progressBarLoading);

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
                String jsonFirstName = jsonObjItem.getString("name");
                // Set last name
                String jsonLastName = jsonObjItem.getString("email");
                // Set link url of item
                String jsonHref = jsonObjItem.getString("href");

                // Redirect to the edit screen
                // Passing the item's url as param
                Intent intent = new Intent(UserListActivity.this, UserEditActivity.class);
                intent.putExtra("name", jsonFirstName);
                intent.putExtra("email", jsonLastName);
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
            loading.showProgress(UserListActivity.this, viewLoading, true);

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
                                JSONArray jsonUsers = jsonEmbedded.getJSONArray("user");

                                // looping through All Users
                                for (int i = 0; i < jsonUsers.length(); i++) {
                                    JSONObject jsonUser = jsonUsers.getJSONObject(i);

                                    String name = jsonUser.getString("name");
                                    String email = jsonUser.getString("email");

                                    JSONObject objLink = jsonUser.getJSONObject("_links");
                                    JSONObject objHref = objLink.getJSONObject("self");

                                    String href = objHref.getString("href");

                                    // tmp hash map for single user
                                    HashMap<String, String> user = new HashMap<>();

                                    // adding each child node to HashMap key => value
                                    user.put("name", name);
                                    user.put("email", email);
                                    user.put("href", href);

                                    // adding user to users list
                                    arrayListUsers.add(user);

                                    /**
                                     * Updating parsed JSON data into ListView
                                     * */
                                    ListAdapter adapter = new SimpleAdapter(
                                            UserListActivity.this,
                                            arrayListUsers,
                                            R.layout.user_list_item,
                                            new String[]{"name", "email", "href"},
                                            new int[]{R.id.textViewName, R.id.textViewEmail, R.id.textViewHref});

                                    listViewUserList.setAdapter(adapter);

                                    // Hidden a progress spinner
                                    loading.showProgress(UserListActivity.this, viewLoading, false);
                                }
                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Hidden a progress spinner
                                        loading.showProgress(UserListActivity.this, viewLoading, false);

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
                            loading.showProgress(UserListActivity.this, viewLoading, false);
                        }
                    });

            AppController.getInstance(UserListActivity.this).addToRequestQueue(jsonRequest);
        }
    }

}
