package com.mcafeweb.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.mcafeweb.R;
import com.mcafeweb.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by Balpreet on 26-May-17.
 */

public class VolleyHelper {
    private static final int MY_TIMEOUT = 15000;
    String TAG = "Volley";
    Context context;

    public interface VolleyResponse {
        void onResponse(String result);

        void onResponse(JSONObject result);

        void onResponse(JSONArray result);

        void onResponse(NetworkResponse result);

        void onError(VolleyError error);

    }

    public VolleyResponse delegate = null;

    public VolleyHelper(VolleyResponse delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    Response.Listener stringResponseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d(TAG, "String Response : " + response);
            delegate.onResponse(response);
        }
    };

    Response.Listener jsonObjectResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d(TAG, "JSON Response : " + response);
            delegate.onResponse(response);
        }
    };

    Response.Listener jsonArrayResponseListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            Log.d(TAG, "Array Response : " + response);
            delegate.onResponse(response);
        }
    };

    Response.Listener<NetworkResponse> networkResponseListener = new Response.Listener<NetworkResponse>() {
        @Override
        public void onResponse(NetworkResponse response) {
            try {
                final String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers));
                JSONObject json = Helper.Instance.getJson(jsonString);
                Log.d(TAG, "Network Response : ");
                Helper.Instance.printJSON(json);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            delegate.onResponse(response);
        }
    };


    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
            Log.d(TAG, "Error : " + error.getClass());

            /*
            if (error.getClass() == TimeoutError.class) {
                Toast.makeText(context.getApplicationContext(), "Connection is slow", Toast.LENGTH_SHORT);
            }
            if (error.getClass() == NoConnectionError.class) {
                Toast.makeText(context.getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT);
            }
            if (error.getClass() == NetworkError.class) {
                Toast.makeText(context.getApplicationContext(), "This connection may not have internet connectivity", Toast.LENGTH_SHORT);
            }
            */


            delegate.onError(error);
        }
    };

    public void makeStringRequest(String url, String TAG) {
        if (countRequestsInFlight(TAG) == 0) {
            StringRequest strReq = new StringRequest(Request.Method.POST, url, stringResponseListener, errorListener);
            setShouldCache(strReq, true);
            strReq.setRetryPolicy(new DefaultRetryPolicy(MY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Log.d(TAG,"Request = " + strReq);
            AppController.getInstance().addToRequestQueue(strReq, TAG);
        }
    }

    public void makeStringRequest(String url, String TAG, final Map<String, String> params) {
        if (countRequestsInFlight(TAG) == 0) {
            StringRequest strReq = new StringRequest(Request.Method.POST, url, stringResponseListener, errorListener) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(MY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Log.d(TAG,"Request = " + strReq);
            AppController.getInstance().addToRequestQueue(strReq, TAG);
        }
    }

    public void makeCachedRequest(String url, String TAG) {
        CacheRequest cacheRequest = new CacheRequest(Request.Method.POST, url, networkResponseListener, errorListener);
        cacheRequest.setRetryPolicy(new DefaultRetryPolicy(MY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(cacheRequest, TAG);
        Log.d(this.TAG, "Making Cache Request of : " + url);
    }

    public void makeCachedRequest(String url, String TAG, final Map<String, String> params) {
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
        CacheRequest cacheRequest = new CacheRequest(Request.Method.POST, url, networkResponseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public String getCacheKey() {
                return generateCacheKeyWithParam(super.getCacheKey(), params);
            }
        };
        cacheRequest.setRetryPolicy(new DefaultRetryPolicy(MY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (requestQueue.getCache().get(cacheRequest.getCacheKey()) != null) {
            AppController.getInstance().getRequestQueue().getCache().invalidate(cacheRequest.getCacheKey(), true);
        }
        if (countRequestsInFlight(cacheRequest.getCacheKey()) == 0) {
            AppController.getInstance().addToRequestQueue(cacheRequest, TAG);
        }

        Log.d(TAG,"Request = " + cacheRequest);
    }


    public void makeJsonRequest(String url, String TAG) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, null, jsonObjectResponseListener, errorListener);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(MY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }

    public void makeJsonRequest(String url, String TAG, final Map<String, String> params) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, null, jsonObjectResponseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(MY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }

    public void makeJsonArrayRequest(String url, String TAG) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, null, jsonArrayResponseListener, errorListener);
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(MY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayRequest, TAG);
    }

    public void makeJsonArrayRequest(String url, String TAG, final Map<String, String> params) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, null, jsonArrayResponseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(MY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayRequest, TAG);
    }

    public NetworkImageView loadImageInNetworkImageView(NetworkImageView networkImageView, String URL) {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

// If you are using NetworkImageView
        networkImageView.setImageUrl(URL, imageLoader);
        return networkImageView;
    }

    public ImageView loadImageInImageView(final ImageView imageView, String URL_IMAGE) {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        imageLoader.get(URL_IMAGE, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    // load image into imageview
                    imageView.setImageBitmap(response.getBitmap());
                }
            }
        });
        return imageView;
    }


    public ImageView loadImageInImageViewWithLoaders(final ImageView imageView, String URL_IMAGE) {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        //imageLoader.get(URL_IMAGE, ImageLoader.getImageListener(
//                imageView, R.drawable.ico_loading, R.drawable.ico_error));
        return imageView;

    }

    public boolean checkCachedData(String url) {
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(url);
        if (entry != null) {
            Log.d(TAG, "Cached data found in = " + url);
            return true;
            // handle data, like converting it to xml, json, bitmap etc.,
        }
        return false;
    }

    public void invalidateCachedData(String url) {
        AppController.getInstance().getRequestQueue().getCache().invalidate(url, true);
    }

    private void setShouldCache(StringRequest stringRequest, boolean should) {
        stringRequest.setShouldCache(should);
    }

    private void setShouldCache(JsonObjectRequest jsonObjectRequest, boolean should) {
        jsonObjectRequest.setShouldCache(should);
    }

    private void setShouldCache(JsonArrayRequest jsonArrayRequest, boolean should) {
        jsonArrayRequest.setShouldCache(should);
    }

    private void setShouldCache(CacheRequest cacheRequest, boolean should) {
        cacheRequest.setShouldCache(should);
    }

    public void removeCachedURL(String url) {
        AppController.getInstance().getRequestQueue().getCache().remove(url);
    }

    public void removeCache() {
        AppController.getInstance().getRequestQueue().getCache().clear();
    }

    public void cancelRequest(String TAG) {
        AppController.getInstance().getRequestQueue().cancelAll(TAG);
    }


    public int countRequestsInFlight(String tag) {
        RequestQueue queue = AppController.getInstance().getRequestQueue();
        CountRequestsInFlight inFlight = new CountRequestsInFlight(tag);
        queue.cancelAll(inFlight);
        return inFlight.getCount();
    }


    public class CountRequestsInFlight implements RequestQueue.RequestFilter {
        Object tag;
        int count = 0;

        public CountRequestsInFlight(Object tag) {
            this.tag = tag;
        }

        @Override
        public boolean apply(Request<?> request) {
            if (request.getTag().equals(tag)) {
                count++;
            }
            return false;  // always return false.
        }

        public int getCount() {
            return count;
        }
    }

    public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;
        long serverDate = 0;
        String serverEtag = null;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }

        serverEtag = headers.get("ETag");

        final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
        final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
        final long softExpire = now + cacheHitButRefreshed;
        final long ttl = now + cacheExpired;

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = ttl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;

        return entry;
    }

    public static String generateCacheKeyWithParam(String url, Map<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            url += entry.getKey() + "=" + entry.getValue();
        }
        return url;
    }

    public long getMinutesDifference(long timeStart, long timeStop) {
        long diff = timeStop - timeStart;
        long diffMinutes = diff / (60 * 1000);

        return diffMinutes;
    }
}