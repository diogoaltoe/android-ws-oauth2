package com.diogoaltoe.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.diogoaltoe.R;
import com.diogoaltoe.controller.LoadingController;
import com.diogoaltoe.controller.Oauth2Controller;
import com.diogoaltoe.model.Product;

public class ProductListActivity extends AppCompatActivity {

    private ListView listViewProductList;
    private ArrayList<Product> arrayListProduct;
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

        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            //TODO: Show a progress spinner
            loading = new LoadingController();
            // Show a progress spinner
            loading.showProgress(ProductListActivity.this, viewLoading, true);
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                // Get instance from authenticate User
                Oauth2Controller oauth2 = Oauth2Controller.getInstance();
                // Call Web Service of Product List
                Object response = oauth2.callGetService(ProductListActivity.this, true, "product/");
                //System.out.println("String - Product: " + stringResponse);

                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String result = ow.writeValueAsString(response);

                return result;

            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Hidden a progress spinner
            loading.showProgress(ProductListActivity.this, viewLoading, false);

            // If returned string is NOT empty
            if(result != null) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    List<Product> listProducts = mapper.readValue(result, new TypeReference<List<Product>>() {});

                    //Loop the product list
                    for (Product product : listProducts) {
                        //System.out.println("Product - " + product.toString());
                        // adding product to users list
                        arrayListProduct.add(product);
                    }

                    //instantiate custom adapter
                    ProductAdapter adapter = new ProductAdapter(arrayListProduct, ProductListActivity.this);
                    listViewProductList.setAdapter(adapter);

                    listViewProductList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Object itemList = listViewProductList.getItemAtPosition(position);

                            //System.out.println("itemList: "+ itemList);

                            // Convert Object to Json (String)
                            Gson gson = new Gson();
                            String jsonStrItem = gson.toJson(itemList); //convert

                            try {
                                // Convert JsonString to JsonObject
                                JSONObject jsonObjItem = new JSONObject(jsonStrItem);

                                // Set items of list
                                String jsonId = jsonObjItem.getString("id");
                                String jsonName = jsonObjItem.getString("name");
                                String jsonDescription = jsonObjItem.getString("description");
                                String jsonCost = jsonObjItem.getString("cost");

                                // Redirect to the edit screen
                                // Passing the item's url as param
                                Intent intent = new Intent(ProductListActivity.this, ProductEditActivity.class);
                                intent.putExtra("id", jsonId);
                                intent.putExtra("name", jsonName);
                                intent.putExtra("description", jsonDescription);
                                intent.putExtra("cost", jsonCost);
                                startActivity(intent);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    //TODO: Add messagem if error
                } catch (IOException e) {
                    e.printStackTrace();
                    //TODO: Add messagem if error
                }
            }
            // If returned string is empty
            else {
                //TODO: Show message about empty return
            }
        }
    }
}
