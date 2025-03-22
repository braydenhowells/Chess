package ui;

import com.google.gson.Gson;

import java.io.*;


import exception.ResponseException;
import requests.*;
import results.CreateResult;
import results.ListResult;
import results.LoginResult;
import results.SimpleResult;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {

    private final String serverUrl;
    private String authToken;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public LoginResult register(RegisterRequest request) {
        var path = "/user";
        try {
            var result = this.makeRequest("POST", path, request, LoginResult.class);
            // set this so we can use it later in other calls
            authToken = result.authToken();
            return result;

        } catch (ResponseException e) {
            return new LoginResult(e.getMessage(), null, null);
        }
    }


    public SimpleResult logout(LogoutRequest request) {
        var path = "/session";
        try {
            var result = this.makeRequest("DELETE", path, request, SimpleResult.class);
            // reset auth token since we are logged out
            authToken = "";
            return result;

        } catch (ResponseException e) {
            return new SimpleResult(e.getMessage());
        }
    }


    public LoginResult login(LoginRequest request) {
        var path = "/session";
        try {
            var result = this.makeRequest("POST", path, request, LoginResult.class);
            // snag auth token for the future
            authToken = result.authToken();
            return result;
        }
        catch(ResponseException e) {
            return new LoginResult(e.getMessage(), null, null);
        }
    }


    public SimpleResult clear() {
        var path = "/db";
        try {
            return this.makeRequest("DELETE", path, null, SimpleResult.class);
        } catch (ResponseException e) {
            return new SimpleResult("Attempting to clear the database resulted in an error.");
        }
    }

    public CreateResult create(CreateRequest request) {
        var path = "/game";
        try {
            return this.makeRequest("POST", path, request, CreateResult.class);
        } catch (ResponseException e) {
            return new CreateResult(null, e.getMessage());
        }
    }

    public ListResult list() {
        var path = "/game";
        try {
            return this.makeRequest("GET", path, null, ListResult.class);
        } catch (ResponseException e) {
            return new ListResult(e.getMessage(), null);
        }
    }

    public SimpleResult join(JoinRequest request) {
        var path = "/game";
        try {
            return this.makeRequest("PUT", path, request, SimpleResult.class);
        } catch (ResponseException e) {
            return new SimpleResult(e.getMessage());
        }
    }









    // methods for actually talking to server w the request objects
    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            // debug
            System.out.println("sending " + method + " request to: " + serverUrl + path);

            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            // in header, use the auth token stored if we have one
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken); // 💥 Add this line
            }


            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        }
        // pet shop has this double catch here. might as well include it eh?
        catch (ResponseException e) {
            throw e;
        }
        catch (Exception e) {
            System.out.println("uh oh, an error occurred in the makeRequest method");
            throw new ResponseException(500, e.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
