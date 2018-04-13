package ws.droid.controller;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;

public class JsonController {

    public JsonController() {
    }

    public String makeServiceCall(Context context, String method, String url, Map<String, String> params) {

        int methodInt;

        switch (method) {
            case "GET":
                methodInt = 0;
                break;
            case "POST":
                methodInt = 1;
                break;
            case "PUT":
                methodInt = 2;
                break;
            case "DELETE":
                methodInt = 3;
                break;
            default:
                methodInt = 0;
                break;
        }

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(methodInt, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: handle success

                //Intent intent = new Intent(this, MainActivity.class);
                //startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //TODO: handle failure
            }
        });

        AppController.getInstance(context).addToRequestQueue(jsonRequest);

        return "";
    }
}
