package ws.droid.activity;

import android.content.Intent;
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

public class ProductListActivity extends AppCompatActivity {

    private String TAG = ProductListActivity.class.getSimpleName();

    private ListView listViewProductList;

    // URL to get contacts JSON
    private static String hrefWebService = "http://10.0.2.2:8080/product/";

    private ArrayList<HashMap<String, String>> arrayListProduct;
    // Progress Bar
    private LoadingController loading;
    private View viewLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        arrayListProduct = new ArrayList<>();

        listViewProductList = (ListView) findViewById(R.id.listViewProductList);
        viewLoading = findViewById(R.id.progressBarLoading);

        listViewProductList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Object itemList = listViewProductList.getItemAtPosition(position);

            // Convert Object to Json (String)
            Gson gson = new Gson();
            String jsonStrItem = gson.toJson(itemList); //convert

            try {
                // Convert JsonString to JsonObject
                JSONObject jsonObjItem = new JSONObject(jsonStrItem);

                // Set name
                String jsonName = jsonObjItem.getString("name");
                // Set description
                String jsonDescription = jsonObjItem.getString("description");
                // Set cost
                String jsonCost = jsonObjItem.getString("cost");
                // Set link url of item
                String jsonHref = jsonObjItem.getString("href");

                // Redirect to the edit screen
                // Passing the item's url as param
                Intent intent = new Intent(ProductListActivity.this, ProductEditActivity.class);
                intent.putExtra("name", jsonName);
                intent.putExtra("description", jsonDescription);
                intent.putExtra("cost", jsonCost);
                intent.putExtra("href", jsonHref);
                startActivity(intent);

                //Toast.makeText(ProductListActivity.this,"You selected : " + jsonUrl,Toast.LENGTH_SHORT).show();

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
            loading.showProgress(ProductListActivity.this, viewLoading, true);

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
                                JSONArray jsonProducts = jsonEmbedded.getJSONArray("product");

                                // looping through All Users
                                for (int i = 0; i < jsonProducts.length(); i++) {
                                    JSONObject jsonProduct = jsonProducts.getJSONObject(i);

                                    String name = jsonProduct.getString("name");
                                    String description = jsonProduct.getString("description");
                                    String cost = jsonProduct.getString("cost");

                                    JSONObject objLink = jsonProduct.getJSONObject("_links");
                                    JSONObject objHref = objLink.getJSONObject("self");

                                    String href = objHref.getString("href");

                                    // tmp hash map for single product
                                    HashMap<String, String> product = new HashMap<>();

                                    // adding each child node to HashMap key => value
                                    product.put("name", name);
                                    product.put("description", description);
                                    product.put("cost", cost);
                                    product.put("href", href);

                                    // adding product to users list
                                    arrayListProduct.add(product);

                                    /**
                                     * Updating parsed JSON data into ListView
                                     * */
                                    ListAdapter adapter = new SimpleAdapter(
                                            ProductListActivity.this,
                                            arrayListProduct,
                                            R.layout.product_list_item,
                                            new String[]{"name", "description", "cost", "href"},
                                            new int[]{R.id.textViewName, R.id.textViewDescription, R.id.textViewCost, R.id.textViewHref});

                                    listViewProductList.setAdapter(adapter);

                                    // Hidden a progress spinner
                                    loading.showProgress(ProductListActivity.this, viewLoading, false);
                                }
                            } catch (final JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Hidden a progress spinner
                                        loading.showProgress(ProductListActivity.this, viewLoading, false);

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
                            loading.showProgress(ProductListActivity.this, viewLoading, false);
                        }
                    });

            AppController.getInstance(ProductListActivity.this).addToRequestQueue(jsonRequest);
        }
    }
}
