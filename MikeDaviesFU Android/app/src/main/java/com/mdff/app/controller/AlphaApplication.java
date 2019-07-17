package com.mdff.app.controller;

import android.app.Application;

import com.mdff.app.activity.ResourceContentItem;
import com.mdff.app.utility.LruAppBitmapCache;
import com.mdff.app.utility.TypefaceUtil;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import static android.content.ContentValues.TAG;

/**
 * Created by Swati.Gupta on 4/3/2018.
 */

public class AlphaApplication extends Application {

    private static AlphaApplication mInstance;
    private ImageLoader mImageLoader;private RequestQueue mRequestQueue;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
//        OverrideFonts.setDefaultFont(this, "DEFAULT", "assets/HelveticaNeue-BoldCond_1.otf");
//        OverrideFonts.setDefaultFont(this, "MONOSPACE", "font/helvetica.TTF");
//        OverrideFonts.setDefaultFont(this, "SERIF", "fonts/HelveticaNeue-BoldCond_1.otf");
//        OverrideFonts.setDefaultFont(this, "SANS_SERIF", "font/helvetica.TTF");
        if (ResourceContentItem.isfont == true) {

            TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/HelveticaNeue-BoldCond_1.otf"); // font from assets: "assets/fonts/Roboto-Regular.ttf


        } else {
            TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/HelveticaNeue-Light.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf

        }
    }
    public AlphaApplication get()
    {
        return mInstance;
    }
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruAppBitmapCache());
        }
        return this.mImageLoader;
    }
    public static synchronized AlphaApplication getInstance() {
        return mInstance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
