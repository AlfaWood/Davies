package com.mdff.app.app_interface;

import android.app.Activity;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Swati.Gupta on 7/3/2018.
 */

public interface LikeDislike {

public void postLikeDislike(String fid, String id, String status, final VolleyCallback volleyCallback, int pos, String comeFrom, ImageButton iv_like, com.mdff.app.model.Feed f, Activity activity, TextView tv_like, LinearLayout iv_likeLayout);


}
