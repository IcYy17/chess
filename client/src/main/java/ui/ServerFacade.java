package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthInfo;
import model.GameInfo;
import model.UserInfo;
//import server.Server;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import requests.JoinGameRequest;
import response.JoinGameResponse;
import response.ListGamesResponse;

public class ServerFacade {
    private final String serverConn;

    public ServerFacade(String url) {
        serverConn = url;
    }
    //


    private <T> T newRequest(String method, String path, Object request, Class<T> responseClass, String token) throws ResponseException {
        try {
            HttpURLConnection http = setupConnection(method, path, token);
            if (request != null) {
                writeHttp(request, http);
            }
            http.connect();
            // exception line?
            return responseClass != null ? readHttp(http, responseClass) : null;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private HttpURLConnection setupConnection(String method, String path, String token) throws IOException, URISyntaxException {
        URL url = new URL(new URI(serverConn + path).toString());
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        if (token != null) {
            http.setRequestProperty("Authorization", token);
        }
        return http;
    }
    private static <T> T readHttp(HttpURLConnection http, Class<T> responseClass) throws IOException {
        if (http.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return null;
        }
        try (InputStream response = http.getInputStream();
             Reader read = new InputStreamReader(response, StandardCharsets.UTF_8)) {
            return new Gson().fromJson(read, responseClass);
        }
    }

    private static void writeHttp(Object req, HttpURLConnection connect) throws IOException {
        if (req == null) return;
        connect.setRequestProperty("Content", "application/json");
        byte[] requestDataBytes = new Gson().toJson(req).getBytes(StandardCharsets.UTF_8);
        connect.setDoOutput(true);
        try (OutputStream outputStream = connect.getOutputStream()) {
            outputStream.write(requestDataBytes);
        }
    }

    //


    public AuthInfo register(String username, String password, String email) throws ResponseException {
        return newRequest("POST", "/user", new UserInfo(username, password, email), AuthInfo.class, null);
    }

    public AuthInfo login(String username, String password) throws ResponseException {
        return newRequest("POST", "/session", new UserInfo(username, password, null), AuthInfo.class, null);
    }
    public void logout(String token) throws ResponseException {
        this.newRequest("DELETE", "/session", null, null, token);
    }
    public GameInfo createGame(String token, String gameName) throws ResponseException {
        return newRequest("POST", "/game", new GameInfo(0, null, null, gameName, null), GameInfo.class, token);
    }

    public ListGamesResponse listGames(String token) throws ResponseException {
        return this.newRequest("GET", "/game", null, ListGamesResponse.class, token);
    }

    //issues with this method, 2 args vs 3
    public GameInfo joinGame(String token, int gameId, String color) throws ResponseException {
        return this.newRequest("PUT", "/game", new JoinGameRequest(color, gameId), GameInfo.class, token);
    }

    public void clearData() {
        HttpURLConnection connection = null;
        int statusCode;
        try {
            URL endpoint = new URL(serverConn + "/db");
            connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod("DELETE");
            connection.connect();
            statusCode = connection.getResponseCode();
        } catch (IOException e) {
            System.out.println("Data clearance failed: " + e.getMessage());
            return;
        }

        if (statusCode == HttpURLConnection.HTTP_OK) {
            System.out.println("All data successfully cleared.");
        } else {
            System.out.println("Data clearance failed: Server responded with code " + statusCode);
        }
        if (connection != null) {
            connection.disconnect();
        }
    }

}
