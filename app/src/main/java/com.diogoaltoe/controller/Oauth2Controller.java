package com.diogoaltoe.controller;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;
import org.apache.commons.codec.binary.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.diogoaltoe.R;


public class Oauth2Controller extends Application {

    /**
     * Instance already created
     */
    private static Oauth2Controller instance;

    /**
     * Client ID of your client credential.  Change this to match whatever credential you have created.
     */
    private static final String CLIENT_ID = "wsapp";

    /**
     * Client secret of your client credential.  Change this to match whatever credential you have created.
     */
    private static final String CLIENT_SECRET = "secret";

    /**
     * Account on which you want to request a resource. Change this to match the account you want to
     * retrieve resources on.
     */
    private String username;

    private String password;

    private static final String GRANT_TYPE = "password";

    /**
     * Project URL address.
     */
    private static final String URL_PROJECT = "http://10.0.2.2:8081/spring-boot-ws-oauth2/";

    /**
     * URL for generate OAuth access tokens.
     */
    private static final String URL_OAUTH2 = "oauth/token";

    private String accessToken;
    private String refreshToken;

    public static Oauth2Controller getInstance() {
        if (instance == null)
            instance = new Oauth2Controller();
        return instance;
    }

    public static void destroyInstance() {
        if (instance != null)
            instance = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenOauth2() {

        try {
            RestTemplate restTemplate = new RestTemplate();

            String headerPlain = CLIENT_ID + ":" + CLIENT_SECRET;
            byte[] headerBytes = headerPlain.getBytes();
            byte[] headerBase64 = Base64.encodeBase64(headerBytes);
            String header = new String(headerBase64);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + header);
            HttpEntity <String> httpEntity = new HttpEntity <String> (headers);

            UriComponentsBuilder url = UriComponentsBuilder
                    .fromUriString(URL_PROJECT + URL_OAUTH2)
                    // Add query parameter
                    .queryParam("username", username)
                    .queryParam("password", password)
                    .queryParam("grant_type", GRANT_TYPE);

            //executing the GET call
            String result = restTemplate.postForObject(url.toUriString(), httpEntity, String.class);

            //retrieving the response
            System.out.println("String - getTokenOauth2"+ result);

            //{
            //    "access_token":"38c118fd-c730-4df6-8150-b44a282b71f3",
            //    "token_type":"bearer",
            //    "refresh_token":"363989ff-e294-4b80-a36b-f21711a9f24b",
            //    "expires_in":1198,
            //    "scope":"read write"

            JSONObject jsonResponse = new JSONObject(result);

            String jsonAccessToken = jsonResponse.getString("access_token");
            String jsonRefreshToken = jsonResponse.getString("refresh_token");

            setAccessToken(jsonAccessToken);
            setRefreshToken(jsonRefreshToken);

            return "Ok";
        }
        catch (HttpClientErrorException e) {
            e.printStackTrace();
            System.out.println("HttpClientErrorException - callPostService: " + e.toString());

            return null;
        }
        catch (JSONException e) {
            e.printStackTrace();
            System.out.println("JSONException - callPostService: " + e.toString());

            return null;
        }
    }

    public Object callGetService(Context context, Boolean requireOAuth, String urlService) {

        NetworkController network = new NetworkController();
        // Check if Internet is working
        if (!network.isNetworkAvailable(context)) {
            // Show a message to the user to check his Internet
            Toast.makeText(context, R.string.app_network_offline, Toast.LENGTH_LONG).show();

            return null;
        } else {

            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                if (requireOAuth) {
                    headers.set("Authorization", "Bearer " + this.getAccessToken());
                }

                HttpEntity<Object> entity = new HttpEntity<>(headers);
                ResponseEntity<Object> result = restTemplate.exchange(
                        URL_PROJECT + urlService,
                        HttpMethod.GET,
                        entity,
                        Object.class
                );

                return result.getBody();

            } catch (HttpClientErrorException e) {
                e.printStackTrace();
                System.out.println("Exception - callGetService: " + e.toString());

                return null;
            }
        }
    }

    public String callPostService(Context context, Boolean requireOAuth, String urlService, Object params) {

        NetworkController network = new NetworkController();
        // Check if Internet is working
        if (!network.isNetworkAvailable(context)) {
            // Show a message to the user to check his Internet
            Toast.makeText(context, R.string.app_network_offline, Toast.LENGTH_LONG).show();

            return null;
        } else {

            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                if (requireOAuth) {
                    headers.set("Authorization", "Bearer " + this.getAccessToken());
                }

                HttpEntity<Object> entity = new HttpEntity<>(params, headers);
                ResponseEntity<Object> result = restTemplate.exchange(
                        URL_PROJECT + urlService,
                        HttpMethod.POST,
                        entity,
                        Object.class
                );

                return result.getStatusCode().toString();

            } catch (HttpClientErrorException e) {
                e.printStackTrace();
                System.out.println("Exception - callPostService: " + e.toString());

                return null;
            }
        }
    }


    public String callPutService(Context context, Boolean requireOAuth, String urlService, Object params) {

        NetworkController network = new NetworkController();
        // Check if Internet is working
        if (!network.isNetworkAvailable(context)) {
            // Show a message to the user to check his Internet
            Toast.makeText(context, R.string.app_network_offline, Toast.LENGTH_LONG).show();

            return null;
        } else {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                if (requireOAuth) {
                    headers.set("Authorization", "Bearer " + this.getAccessToken());
                }

                HttpEntity<Object> entity = new HttpEntity<>(params, headers);
                ResponseEntity<Object> result = restTemplate.exchange(
                        URL_PROJECT + urlService,
                        HttpMethod.PUT,
                        entity,
                        Object.class
                );

                return result.getStatusCode().toString();
            } catch (HttpClientErrorException e) {
                e.printStackTrace();
                System.out.println("Exception - callPutService: " + e.toString());

                return null;
            }
        }
    }


    public String callDeleteService(Context context, Boolean requireOAuth, String urlService, Object params) {

        NetworkController network = new NetworkController();
        // Check if Internet is working
        if (!network.isNetworkAvailable(context)) {
            // Show a message to the user to check his Internet
            Toast.makeText(context, R.string.app_network_offline, Toast.LENGTH_LONG).show();

            return null;
        } else {

            try {
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                if (requireOAuth) {
                    headers.set("Authorization", "Bearer " + this.getAccessToken());
                }
                HttpEntity<Object> entity = new HttpEntity<>(headers);

                ResponseEntity<Object> result = restTemplate.exchange(
                        URL_PROJECT + urlService + params,
                        HttpMethod.DELETE,
                        entity,
                        Object.class
                );

                //System.out.println("callDeleteService: "  + result.getStatusCode());

                return result.getStatusCode().toString();

            } catch (HttpClientErrorException e) {
                e.printStackTrace();
                System.out.println("Exception - callDeleteService: " + e.toString());

                return null;
            }
        }
    }

}
