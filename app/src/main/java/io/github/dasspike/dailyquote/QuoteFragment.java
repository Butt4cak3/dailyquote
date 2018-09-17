package io.github.dasspike.dailyquote;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.ClientError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * This fragment is used to display a single quote.
 */
public class QuoteFragment extends Fragment {
    private ImageView imageView;
    private TextView textView_quote;
    private TextView textView_author;
    private int shortAnimationDuration;
    private RequestController requestController;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String url;
    private String title;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quote, container, false);

        // get URL endpoint
        Bundle args = getArguments();
        if (args != null) {
            url = args.getString("url");
        }

        swipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        imageView = rootView.findViewById(R.id.imageView);
        textView_quote = rootView.findViewById(R.id.textView_quote);
        textView_author = rootView.findViewById(R.id.textView_author);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        requestController = RequestController.getInstance(getContext());

        // set up listener for image responses
        final ResponseListener imageResponseListener = new ResponseListener() {
            @Override
            public <T> void onSuccess(T object, boolean forceRefresh, boolean isImmediate) {
                Bitmap bitmap = ((ImageLoader.ImageContainer) object).getBitmap();
                if (bitmap == null) {
                    if (!isImmediate) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    if (isImmediate) {
                        showImage(bitmap, false);
                        if (!forceRefresh) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    } else {
                        showImage(bitmap, true);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                if (e instanceof NoConnectionError) {
                    showToast(getString(R.string.no_connection));
                } else if (e instanceof TimeoutError) {
                    showToast(getString(R.string.timeout));
                } else {
                    showError(e.getLocalizedMessage());
                }
                swipeRefreshLayout.setRefreshing(false);
            }

        };

        // set up listener for quote responses
        final ResponseListener quoteResponseListener = new ResponseListener() {
            @Override
            public <T> void onSuccess(T object, boolean forceRefresh, boolean isImmediate) {
                Quote quote = (Quote) object;
                if (isImmediate) {
                    showQuote(quote, false);
                } else {
                    showQuote(quote, true);
                }
                requestController.fetchImage(quote.backgroundUrl, forceRefresh, imageResponseListener);
            }

            @Override
            public void onError(Exception e) {
                if (e instanceof NoConnectionError) {
                    showToast(getString(R.string.no_connection));
                } else if (e instanceof TimeoutError) {
                    showToast(getString(R.string.timeout));
                } else if (e instanceof ParseError) {
                    showToast(getString(R.string.invalid_response));
                } else if (e instanceof JSONException | e instanceof ParseException) {
                    showToast(getString(R.string.quote_invalid_format));
                } else if (e instanceof ClientError) {
                    switch (((ClientError) e).networkResponse.statusCode) {
                        case 429:
                            showToast(getString(R.string._429_too_many_requests));
                            break;
                        case 404:
                            showToast(getString(R.string._404_not_found));
                            break;
                        default:
                            showError(e.toString());
                    }
                } else {
                    showError(e.getLocalizedMessage());
                }
                swipeRefreshLayout.setRefreshing(false);
            }

        };

        // listen for the refresh gesture
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            requestController.fetchQuote(url, true, quoteResponseListener);
        });

        // show refresh indicator if an update is needed
        Cache.Entry entry = requestController.getRequestQueue().getCache().get(url);
        if (entry == null || entry.refreshNeeded() || entry.isExpired()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        requestController.fetchQuote(url, false, quoteResponseListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // cancel requests when the fragment stops
        requestController.getRequestQueue().cancelAll(url);
    }

    /**
     * Sets the title of the action bar to the given text.
     *
     * @param title The text the action bar should display.
     */
    private void setActionBarTitle(String title) {
        this.title = title;
        setActionBarTitle();
    }

    /**
     * Sets the title of the action bar to the title of the current quote.
     */
    public void setActionBarTitle() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
    }

    /**
     * Shows the given quote by setting the text and author on the TextViews.
     * @param quote The quote to show.
     * @param fadeIn If <code>true</code>, the text and author TextViews fade in.
     */
    private void showQuote(@NonNull Quote quote, boolean fadeIn) {
        DateFormat formatter = SimpleDateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        setActionBarTitle(formatter.format(quote.date));

        if (fadeIn) {
            textView_quote.setVisibility(View.INVISIBLE);
            textView_author.setVisibility(View.INVISIBLE);
        }

        textView_quote.setText(quote.text);
        textView_author.setText(quote.author);

        if (fadeIn) {
            textView_quote.setAlpha(0f);
            textView_author.setAlpha(0f);
            textView_quote.setVisibility(View.VISIBLE);
            textView_author.setVisibility(View.VISIBLE);

            textView_quote.animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration)
                    .setListener(null);

            textView_author.animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration)
                    .setListener(null);
        }
    }

    /**
     * Shows a toast with the given text.
     * @param info The text to display.
     */
    private void showToast(String info) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getContext(), info, duration);
        toast.show();
    }

    /**
     * Set the text of the TextView for the quote text to the given string.
     * @param error The text to show.
     */
    private void showError(String error) {
        textView_quote.setText(error);
        textView_author.setText("");
        imageView.setImageBitmap(null);
    }

    /**
     * Displays the given image.
     * @param bitmap The image to show.
     * @param fadeIn If <code>true</code>, the image fades in.
     */
    private void showImage(Bitmap bitmap, boolean fadeIn) {
        if (fadeIn) {
            imageView.setVisibility(View.INVISIBLE);
        }

        imageView.setImageBitmap(bitmap);
        imageView.setImageAlpha(100);

        if (fadeIn) {
            imageView.setAlpha(0f);
            imageView.setVisibility(View.VISIBLE);
            imageView.animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration)
                    .setListener(null);
        }
    }

}