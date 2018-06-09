package com.herokuapp.jordan_chau.popularmovies.utils;

import android.net.Uri;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * <h1>Network Utility</h1>
 * This class handles all necessary HTTP Connections in order to retrieve user information from the API
 * Or send new user information to it
 * It also provides helper methods to build the URL
 *
 * @author Jordan Chau
 * @since 2018-03-07
 */
public class NetworkUtility {

    private static final String TAG = NetworkUtility.class.getSimpleName();
    private static final String URL = "http://api.themoviedb.org/3/movie/popular";

    final static String API_PARAM = "api_key";

    /**
     * This method builds a URL with the specified candidate parameter
     * and returns the URL
     * @param apiQuery - String parameter that specifies candidate parameter
     * @return URL - Returns a URL with the specified candidate parameter
     */
    public static URL buildURL(String apiQuery) {
        Uri uri = Uri.parse(URL).buildUpon().appendQueryParameter(API_PARAM, apiQuery).build();

        URL builtURL = null;
        try {
            builtURL = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + builtURL);

        return builtURL;
    }

    /**
     * This method creates a GET request with the specified url, sends it through a HTTP URL Connection
     * and returns a String that represents a response containing information about all users
     * @param url - Url parameter to create the request with
     * @return String - Returns a String that represents all user info
     */
    public static String getHttpUrlResponse(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * This method creates a curl POST request with the specified data, sends it through a HTTP URL Connection
     * and returns a String that represents the curl request
     * @param data - Data parameter that includes a user's name, email, and candidate ID
     * @return String - Returns a String that represents the curl request
     */
    public static String curl(String data) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(URL).openConnection();
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type",  "application/x-www-form-urlencoded");
        con.getOutputStream().write(data.getBytes());
        con.getOutputStream().close();

        ByteArrayOutputStream rspBuff = new ByteArrayOutputStream();
        InputStream rspStream = con.getInputStream();

        int c;
        while ((c = rspStream.read()) > 0) {
            rspBuff.write(c);
        }
        rspStream.close();

        return new String(rspBuff.toByteArray());
    }
}
