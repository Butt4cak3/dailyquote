package io.github.dasspike.dailyquote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A singleton class to handle network requests and cache responses.
 */
public class RequestController {
    @SuppressLint("StaticFieldLeak")
    private static RequestController instance;
    private final ImageLoader imageLoader;
    private final Context context;
    private RequestQueue requestQueue;

    private RequestController(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache = new LruCache<>(10);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    /**
     * Returns the singleton instance of the request controller.
     *
     * @param context If no instance exists yet, this context is used for creating one.
     * @return The only request controller instance.
     */
    public static synchronized RequestController getInstance(Context context) {
        if (instance == null) {
            instance = new RequestController(context);
        }
        return instance;
    }

    /**
     * Returns the request queue of the request controller.
     * @return The request queue.
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    private ImageLoader getImageLoader() {
        return imageLoader;
    }

    /**
     * Fetches a quote from the given URL endpoint (either using the cache or by making a network request).
     * @param url The URL of the quote endpoint.
     * @param forceRefresh If <code>true</code> invalidates the cache entry to force a refresh.
     * @param callback Gets called when the fetch was successful or when an error occurred.
     */
    public void fetchQuote(String url, final boolean forceRefresh, final ResponseListener callback) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                null, response -> {
            try {
                JSONArray quotes = response.getJSONObject("contents").getJSONArray("quotes");
                if (quotes.length() > 0) {
                    JSONObject quoteJson = quotes.getJSONObject(0);
                    String text = quoteJson.getString("quote");
                    String author = quoteJson.getString("author");
                    String date = quoteJson.getString("date");
                    String background = quoteJson.getString("background");
                    Date parsedDate = parseToDate(date);
                    Quote quote = new Quote(text, author, parsedDate, background);
                    // TODO: find a way to pass a valid "isImmediate" value
                    callback.onSuccess(quote, forceRefresh, false);
                } else {
                    callback.onError(new JSONException("No quotes in JSON response."));
                }
            } catch (JSONException | ParseException e) {
                callback.onError(e);
            }
        }, callback::onError);

        if (forceRefresh) {
            // TODO: find a way to force a refresh without invalidating cache
            // so that the force refresh won't happen after switching views or restarting the app
            getRequestQueue().getCache().invalidate(url, false);
        }
        request.setTag(url);
        getRequestQueue().add(request);
    }

    /**
     * Fetches an image from the given URL (either using the cache or by making a network request).
     * @param url The URL of the image to fetch.
     * @param forceRefresh If <code>true</code> invalidates the cache entry to force a refresh.
     * @param callback Gets called when the fetch was successful or when an error occurred.
     */
    public void fetchImage(String url, final boolean forceRefresh, final ResponseListener callback) {
        if (forceRefresh) {
            getRequestQueue().getCache().invalidate(url, false);
        }
        final ImageLoader imageLoader = getImageLoader();
        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                callback.onSuccess(response, forceRefresh, isImmediate);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
    }

    /**
     * Parses the given string of the format "yyyy-MM-dd" to a Date object. This uses US locale.
     *
     * @param date The string to parse.
     * @return A Date object representing the given date.
     * @throws ParseException If the string could not be parsed.
     */
    private Date parseToDate(String date) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return parser.parse(date);
    }

}
