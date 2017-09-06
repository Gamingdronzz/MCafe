package com.mcafeweb.utils;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.ResponseDelivery;

/**
 * Created by Balpreet on 29-May-17.
 */

public class MyRequestQueue extends RequestQueue {
    public MyRequestQueue(Cache cache, Network network, int threadPoolSize, ResponseDelivery delivery) {
        super(cache, network, threadPoolSize, delivery);
    }

    public MyRequestQueue(Cache cache, Network network, int threadPoolSize) {
        super(cache, network, threadPoolSize);
    }

    public MyRequestQueue(Cache cache, Network network) {
        super(cache, network);
    }
}
