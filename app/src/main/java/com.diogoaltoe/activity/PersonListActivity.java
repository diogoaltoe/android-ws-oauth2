package com.diogoaltoe.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import com.diogoaltoe.R;
import com.diogoaltoe.controller.LoadingController;
import com.diogoaltoe.controller.Oauth2Controller;
import com.diogoaltoe.model.Person;

public class PersonListActivity extends AppCompatActivity {

    private ListView listViewPersonList;

    private ArrayList<Person> arrayListPerson;

    // Progress Bar
    private LoadingController loading;
    private View viewLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);

        // Instance the user array list
        arrayListPerson = new ArrayList<>();
        // Set the user list with element on the screen
        listViewPersonList = (ListView) findViewById(R.id.listViewPersonList);
        // Set the progress spinner with element on the screen
        viewLoading = findViewById(R.id.progressBarLoading);

        // Call the background task to set the list
        new BackgroundTask().execute();
    }

    /**
     * Class that execute in background the task of set the list
     */
    class BackgroundTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            // Instance a progress spinner
            loading = new LoadingController();
            // Show a progress spinner
            loading.showProgress(PersonListActivity.this, viewLoading, true);
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {

                // Get instance from authenticate User
                Oauth2Controller oauth2 = Oauth2Controller.getInstance();
                // Call Web Service of Person List
                Object response = oauth2.callGetService(PersonListActivity.this, true, "person/?sort=firstName&firstName.dir=asc");
                //System.out.println("String - Person: " + stringResponse);

                // Map the response into a String
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String result = ow.writeValueAsString(response);

                return result;

            } catch (Exception e) {
                //System.out.println("Exception: " + e.getMessage());

                return "Exception";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Hidden a progress spinner
            loading.showProgress(PersonListActivity.this, viewLoading, false);

            // If returned string is NOT NetworkException
            if(result != "NetworkException") {
                // If returned string is NOT Exception
                // And NOT return "401"
                if((result != "Exception") && (!result.equals("401"))) {
                    try {
                        // Convert the result String into a Json
                        JSONObject jsonResult = new JSONObject(result);

                        // Verify if the list has items
                        if (jsonResult.has("_embedded")) {
                            // Get the "_embedded" node
                            JSONObject jsonEmbedded = jsonResult.getJSONObject("_embedded");
                            // Get the "person" node
                            JSONArray jsonPeople = jsonEmbedded.getJSONArray("person");

                            // Looping through All People
                            for (int i = 0; i < jsonPeople.length(); i++) {
                                // Get the Person from the current position
                                JSONObject jsonPerson = jsonPeople.getJSONObject(i);

                                // Get the value from "firstName" field
                                String firstName = jsonPerson.getString("firstName");
                                // Get the value from "lastName" field
                                String lastName = jsonPerson.getString("lastName");

                                // Get the "_links" node
                                JSONObject objLink = jsonPerson.getJSONObject("_links");
                                // Get the "self" node
                                JSONObject objHref = objLink.getJSONObject("self");

                                // Get the value from "href" field
                                String href = objHref.getString("href");

                                // Set the User with the values of fields
                                Person person = new Person();
                                person.setFirstName(firstName);
                                person.setLastName(lastName);
                                person.setHref(href);

                                // Adding Person to People List
                                arrayListPerson.add(person);
                            }

                            // Instance custom adapter
                            // And set with the People List
                            PersonAdapter adapter = new PersonAdapter(arrayListPerson, PersonListActivity.this);
                            listViewPersonList.setAdapter(adapter);

                            // Create a click listener from the item of the list
                            listViewPersonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    // Get the position of the item clicked
                                    Object itemList = listViewPersonList.getItemAtPosition(position);

                                    //System.out.println("itemList: "+ itemList);

                                    // Instance Gson variable
                                    Gson gson = new Gson();
                                    // Convert Object to Json (String)
                                    String jsonStrItem = gson.toJson(itemList);

                                    try {
                                        // Convert JsonString to JsonObject
                                        JSONObject jsonObjItem = new JSONObject(jsonStrItem);

                                        // Set items of List
                                        String jsonFirstName = jsonObjItem.getString("firstName");
                                        String jsonLastName = jsonObjItem.getString("lastName");
                                        String jsonHref = jsonObjItem.getString("href");

                                        // Redirect to the edit screen
                                        // Passing the item's url as param
                                        Intent intent = new Intent(PersonListActivity.this, PersonEditActivity.class);
                                        intent.putExtra("firstName", jsonFirstName);
                                        intent.putExtra("lastName", jsonLastName);
                                        intent.putExtra("href", jsonHref);
                                        startActivity(intent);

                                    } catch (final JSONException e) {
                                        //System.out.println("JSONException: " + e.getMessage());

                                        //TODO: Add messagem if error
                                        Toast.makeText(
                                            getApplicationContext(),
                                            R.string.exception_json,
                                            Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
                        }
                        // If returned string is Empty
                        else {
                            //TODO: Show message about exception return
                            Toast.makeText(
                                getApplicationContext(),
                                R.string.exception_empty,
                                Toast.LENGTH_LONG)
                                    .show();
                        }
                    } catch (final JSONException e) {
                        //System.out.println("JSONException: " + e.getMessage());

                        //TODO: Add messagem if error
                        Toast.makeText(
                            getApplicationContext(),
                            R.string.exception_json,
                            Toast.LENGTH_LONG)
                                .show();
                    }
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
            // If returned string is NetworkException
            else {
                //TODO: Show message about exception return
                Toast.makeText(
                        getApplicationContext(),
                        R.string.exception_network,
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

}
