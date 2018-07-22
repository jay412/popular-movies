package com.herokuapp.jordan_chau.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * <h1>Network Utility</h1>
 * This class handles all necessary HTTP Connections in order to retrieve movie information from the API
 * It also provides helper methods to build the URL
 *
 * @author Jordan Chau
 * @since 2018-06-15
 */
public class NetworkUtility {

    private static final String TAG = NetworkUtility.class.getSimpleName();
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    private final static String API_PARAM = "api_key";
    private final static String TRAILER_KW = "/videos";
    private final static String REVIEWS_KW = "/reviews";

    /**
     * This method builds a URL with the specified api parameter
     * and returns the URL
     * @param apiQuery - String parameter that specifies API parameter
     * @param url - String parameter that represents the base url to use
     * @return URL - Returns a URL with the specified api parameter
     */
    public static URL buildURL(String apiQuery, String url) {
        String baseUrl;
        switch (url) {
            case "popular":
                baseUrl = BASE_URL.concat("popular");
                break;
            case "top_rated":
                baseUrl = BASE_URL.concat("top_rated");
                break;
            default:
                baseUrl = "ERROR";
                break;
        }

        Uri uri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(API_PARAM, apiQuery).build();

        URL builtURL = null;
        try {
            builtURL = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return builtURL;
    }

    public static URL buildMovieURL(int movieID, String apiQuery, String type){
        String baseUrl = BASE_URL.concat(Integer.toString(movieID));

        switch (type) {
            case "trailers":
                baseUrl = baseUrl.concat(TRAILER_KW);
                break;
            case "reviews":
                baseUrl = baseUrl.concat(REVIEWS_KW);
                break;
            default:
                baseUrl = "ERROR";
                break;
        }

        Uri uri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(API_PARAM, apiQuery).build();

        URL builtURL = null;
        try {
            builtURL = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //Log.v(TAG, "Built URI = " + builtURL);

        return builtURL;
    }

    /**
     * This method creates a GET request with the specified url, sends it through a HTTP URL Connection
     * and returns a String that represents a response containing information about all movies
     * @param url - Url parameter to create the request with
     * @return String - Returns a String that represents all movie info
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

    public static Boolean checkInternetConnection(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
