package com.diogoaltoe.controller;

import android.app.Application;
import android.content.Context;
import org.apache.commons.codec.binary.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;


public class Oauth2Controller extends Application {

    // Instance already created
    private static Oauth2Controller instance;
    // Client ID of your client credential
    private static final String CLIENT_ID = "wsapp";
    // Client secret of your client credential
    private static final String CLIENT_SECRET = "secret";

    // Username account
    private String username;
    // Password account
    private String password;
    // Type of grant for an access token
    private static final String GRANT_TYPE = "password";

    // Project URL address.
    //private static final String URL_PROJECT = "http://10.0.2.2:8081/spring-boot-ws-oauth2/";
    private static final String URL_PROJECT = "http://diogoaltoe.com/spring-boot-ws-oauth2/";

    // URL for generate OAuth access tokens.
    private static final String URL_OAUTH2 = "oauth/token";

    private String accessToken;
    private String refreshToken;

    private JSONObject embedded;

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

    public JSONObject getEmbedded() {
        return embedded;
    }

    public void setEmbedded(JSONObject embedded) {
        this.embedded = embedded;
    }

    public String getTokenOauth2(Context context) {

        NetworkController network = new NetworkController();
        // Check if Internet is working
        if (!network.isNetworkAvailable(context)) {

            return "NetworkException";

        } else {
            try {
                String headerPlain = CLIENT_ID + ":" + CLIENT_SECRET;
                byte[] headerBytes = headerPlain.getBytes();
                byte[] headerBase64 = Base64.encodeBase64(headerBytes);
                String header = new String(headerBase64);

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Basic " + header);
                HttpEntity<String> httpEntity = new HttpEntity<>(headers);

                UriComponentsBuilder url = UriComponentsBuilder
                        .fromUriString(URL_PROJECT + URL_OAUTH2)
                        // Add query parameter
                        .queryParam("username", username)
                        .queryParam("password", password)
                        .queryParam("grant_type", GRANT_TYPE);

                RestTemplate restTemplate = new RestTemplate();
                // Executing the call to Authentication
                String result = restTemplate.postForObject(
                        url.toUriString(),
                        httpEntity,
                        String.class
                );

                // Retrieving the response
                //System.out.println("String - getTokenOauth2"+ result);

                //{
                //    "access_token":"38c118fd-c730-4df6-8150-b44a282b71f3",
                //    "token_type":"bearer",
                //    "refresh_token":"363989ff-e294-4b80-a36b-f21711a9f24b",
                //    "expires_in":1198,
                //    "scope":"read write"
                //}

                JSONObject jsonResponse = new JSONObject(result);

                String jsonAccessToken = jsonResponse.getString("access_token");
                String jsonRefreshToken = jsonResponse.getString("refresh_token");

                setAccessToken(jsonAccessToken);
                setRefreshToken(jsonRefreshToken);

                return "Authorized";

            } catch (HttpClientErrorException e) {
                // e.printStackTrace();
                // System.out.println("HttpClientErrorException - callPostService: " + e.toString().trim());

                // Error "401", when Bad Request
                // Or Error "400", when Fail in authentication
                return e.getMessage().trim();

            } catch (JSONException e) {
                // e.printStackTrace();
                // System.out.println("JSONException - getTokenOauth2: " + e.toString());

                return "JSONException";
            }
        }
    }

    public Object callGetService(Context context, Boolean requireOAuth, String urlService) {

        NetworkController network = new NetworkController();
        // Check if Internet is working
        if (!network.isNetworkAvailable(context)) {

            return "NetworkException";

        } else {

            try {

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.registerModule(new Jackson2HalModule());

                MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
                converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
                converter.setObjectMapper(mapper);

                RestTemplate restTemplate = new RestTemplate(Collections.<HttpMessageConverter<?>> singletonList(converter));

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

                // Return Json
                return result.getBody();

            } catch (HttpClientErrorException e) {
                //e.printStackTrace();
                //System.out.println("Exception - callGetService: " + e.toString().trim());

                // Error "401"
                return e.getMessage().trim();
            }
        }
    }

    public String callPostService(Context context, Boolean requireOAuth, String urlService, Object params) {

        NetworkController network = new NetworkController();
        // Check if Internet is working
        if (!network.isNetworkAvailable(context)) {

            return "NetworkException";

        } else {

            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));

                if (requireOAuth) {
                    headers.set("Authorization", "Bearer " + this.getAccessToken());
                }

                HttpEntity<Object> entity = new HttpEntity<>(params, headers);
                URI result = restTemplate.postForLocation(
                        URL_PROJECT + urlService,
                        entity,
                        Object.class
                );

                // Return URL
                return result.toString();

            } catch (HttpClientErrorException e) {
                //e.printStackTrace();
                //System.out.println("Exception - callPostService: " + e.toString().trim());

                // Error "401"
                return e.getMessage().trim();
            }
        }
    }


    public String callPutService(Context context, Boolean requireOAuth, String urlService, Object params) {

        NetworkController network = new NetworkController();
        // Check if Internet is working
        if (!network.isNetworkAvailable(context)) {

            return "NetworkException";

        } else {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                if (requireOAuth) {
                    headers.set("Authorization", "Bearer " + this.getAccessToken());
                }

                HttpEntity<Object> entity = new HttpEntity<>(params, headers);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<Object> result = restTemplate.exchange(
                        urlService,
                        HttpMethod.PUT,
                        entity,
                        Object.class
                );

                // Return "204"
                return result.getStatusCode().toString().trim();

            } catch (HttpClientErrorException e) {
                //e.printStackTrace();
                //System.out.println("Exception - callPutService: " + e.toString());

                // Error "401"
                return e.getMessage().trim();
            }
        }
    }


    public String callPatchService(Context context, Boolean requireOAuth, String urlService, String params) {

        NetworkController network = new NetworkController();
        // Check if Internet is working
        if (!network.isNetworkAvailable(context)) {

            return "NetworkException";

        } else {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                if (requireOAuth) {
                    headers.set("Authorization", "Bearer " + this.getAccessToken());
                }

                HttpEntity<String> entity = new HttpEntity<>(params, headers);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate();

                ResponseEntity<String> result = restTemplate.exchange(
                        urlService,
                        HttpMethod.PATCH,
                        entity,
                        String.class
                );

                // Return "204"
                return result.getStatusCode().toString().trim();

            } catch (HttpClientErrorException e) {
                //e.printStackTrace();
                //System.out.println("Exception - callPatchService: " + e.getMessage().trim());

                // Error "401"
                return e.getMessage().trim();
            }
        }
    }


    public String callDeleteService(Context context, Boolean requireOAuth, String urlService) {

        NetworkController network = new NetworkController();
        // Check if Internet is working
        if (!network.isNetworkAvailable(context)) {

            return "NetworkException";

        } else {

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                if (requireOAuth) {
                    headers.set("Authorization", "Bearer " + this.getAccessToken());
                }
                HttpEntity<Object> entity = new HttpEntity<>(headers);

                RestTemplate restTemplate = new RestTemplate();

                ResponseEntity<Object> result = restTemplate.exchange(
                        urlService,
                        HttpMethod.DELETE,
                        entity,
                        Object.class
                );

                //System.out.println("callDeleteService: "  + result.getStatusCode());

                // Return "204"
                return result.getStatusCode().toString().trim();

            } catch (HttpClientErrorException e) {
                //System.out.println("Exception - callDeleteService: " + e.getMessage().trim());

                // Error "401"
                return e.getMessage().trim();
            }
        }
    }

}
