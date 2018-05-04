package com.diogoaltoe.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.support.v7.app.AppCompatActivity;

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

        arrayListPerson = new ArrayList<>();

        listViewPersonList = (ListView) findViewById(R.id.listViewPersonList);
        viewLoading = findViewById(R.id.progressBarLoading);

        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            //TODO: Show a progress spinner
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
                Object response = oauth2.callGetService(PersonListActivity.this, false, "person/");
                //System.out.println("String - Person: " + stringResponse);

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
            loading.showProgress(PersonListActivity.this, viewLoading, false);

            // If returned string is NOT empty
            if(result != null) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    List<Person> listPeople = mapper.readValue(result, new TypeReference<List<Person>>() {});

                    //Loop the person list
                    for (Person person : listPeople) {
                        //System.out.println("Person - " + person.toString());
                        // adding person to users list
                        arrayListPerson.add(person);
                    }

                    //instantiate custom adapter
                    PersonAdapter adapter = new PersonAdapter(arrayListPerson, PersonListActivity.this);
                    listViewPersonList.setAdapter(adapter);

                    listViewPersonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Object itemList = listViewPersonList.getItemAtPosition(position);

                            //System.out.println("itemList: "+ itemList);

                            // Convert Object to Json (String)
                            Gson gson = new Gson();
                            String jsonStrItem = gson.toJson(itemList); //convert

                            try {
                                // Convert JsonString to JsonObject
                                JSONObject jsonObjItem = new JSONObject(jsonStrItem);

                                // Set items of List
                                String jsonId = jsonObjItem.getString("id");
                                String jsonFirstName = jsonObjItem.getString("firstName");
                                String jsonLastName = jsonObjItem.getString("lastName");

                                // Redirect to the edit screen
                                // Passing the item's url as param
                                Intent intent = new Intent(PersonListActivity.this, PersonEditActivity.class);
                                intent.putExtra("id", jsonId);
                                intent.putExtra("firstName", jsonFirstName);
                                intent.putExtra("lastName", jsonLastName);
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
