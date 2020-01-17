package com.mdff.app.fragment;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mdff.app.R;
import com.mdff.app.activity.Home;
import com.mdff.app.activity.VideoImageDisplay;
import com.mdff.app.adapter.CustomFeedListAdapter;
import com.mdff.app.app_interface.LikeDislike;
import com.mdff.app.app_interface.OnLoadMoreListener;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.utility.ApiController;
import com.mdff.app.model.Asset;
import com.mdff.app.utility.AlertMessage;
import com.mdff.app.utility.AppUtil;
import com.mdff.app.utility.ConnectivityReceiver;
import com.mdff.app.utility.Constant;
import com.mdff.app.utility.DividerRVDecoration;
import com.mdff.app.utility.NetworkUtils;
import com.mdff.app.app_interface.VolleyCallback;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Feed extends Fragment implements CustomFeedListAdapter.CallAssetFragment, AlertMessage.NoticeDialogListenerWithoutView, VolleyCallback, LikeDislike {
    private View view;
    private Home home;
    private RecyclerView rv_feed;
    private int connectStatus;
    private CustomFeedListAdapter customFeedListAdapter;
    private SwipeRefreshLayout swipeContainer;
    private AlertMessage alertMessage;
    public static boolean _hasLoadedOnce = false; //  boolean field
    Activity activity;
    public static String pageName = "feed";
    double page_count = 0;
    int count = 0;
    private Handler mHandler;

    public Feed() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_feed, container, false);
        home = ((Home) getActivity());
        connectStatus = ConnectivityReceiver.isConnected(getActivity());
        activity = getActivity();
        page_count = home.page_count;
        count = home.count;
        linkUIElements(this.view);
        // Setup refresh listener which triggers new data loading

        customFeedListAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i("OnLoadMoreListener", "1");
                home.feedList.add(null);
                mHandler = new Handler();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        customFeedListAdapter.notifyItemInserted(home.feedList.size() - 1);
                    }
                });
                if (page_count > count) {
                    Log.i("OnLoadMoreListener", "2");
                    count++;
                    getFeedList2();
                } else {
                    Log.i("OnLoadMoreListener", "3");
                    mHandler = new Handler();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            home.feedList.remove(home.feedList.size() - 1);
                            customFeedListAdapter.notifyItemRemoved(home.feedList.size());
                        }
                    });

                    Log.i("OnLoadMoreListener", "4");
                    customFeedListAdapter.setNotLoaded();
                }
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                    count = 0;
                    page_count = 0;
                    customFeedListAdapter.setLoaded();
                    getFeedList();
                } else {
                    swipeContainer.setRefreshing(false);
                    AlertMessage alertMessage = AlertMessage.newInstance(
                            getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                    alertMessage.show(getActivity().getFragmentManager(), "");
                }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.header_bg);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        _hasLoadedOnce = true;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (_hasLoadedOnce && pageName.equals("feed")) {
            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);
                    if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                        count = 0;
                        page_count = 0;
                        customFeedListAdapter.setLoaded();
                        getFeedList();
                    } else {
                        swipeContainer.setRefreshing(false);
                        AlertMessage alertMessage = AlertMessage.newInstance(
                                getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                        alertMessage.show(getActivity().getFragmentManager(), "");
                    }
                }
            });
        }
    }


    private void linkUIElements(View view) {
        rv_feed = (RecyclerView) view.findViewById(R.id.rv_feed);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_feed.setLayoutManager(linearLayoutManager);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        customFeedListAdapter = new CustomFeedListAdapter(home.feedList, getActivity(), Feed.this, getActivity().getSupportFragmentManager(), AlphaApplication.getInstance().getImageLoader(), Feed.this, rv_feed);
        rv_feed.setAdapter(customFeedListAdapter);

        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        rv_feed.addItemDecoration(new DividerRVDecoration(dividerDrawable));
//            Toast.makeText(getActivity(),"No feeds found",Toast.LENGTH_LONG).show();

        if (home.feedList.size() == 0) {
//            Toast.makeText(getActivity(),getString(R.string.noneed),Toast.LENGTH_LONG).show();

            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);
                    if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                        count = 0;
                        page_count = 0;
                        customFeedListAdapter.setLoaded();

                        getFeedList();
                    } else {
                        swipeContainer.setRefreshing(false);
                        AlertMessage alertMessage = AlertMessage.newInstance(
                                getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                        alertMessage.show(getActivity().getFragmentManager(), "");
                    }
                }
            });
        }
    }

    @Override
    public void displayFragment(String type, String url, String pf) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString("url", url);
        bundle.putString("pf", pf);
        Intent intent = new Intent(getActivity(), VideoImageDisplay.class);
        intent.putExtras(bundle);
//        intent.putExtra("bm",(Serializable)bm);
        startActivity(intent);
    }

    @Override
    public void displayFragment(com.mdff.app.model.Feed feed, Bitmap bm) {
        Bundle bundle = new Bundle();
        bundle.putString("type", feed.getAsset().get(0).getType());
        bundle.putString("url", feed.getAsset().get(0).getUrl());
        bundle.putString("pf", feed.getSocialPlatformName());
        Intent intent = new Intent(getActivity(), VideoImageDisplay.class);
//        intent.putExtras(bundle);
        intent.putExtra("bm", bm);
        startActivity(intent);
    }

    public void getFeedList() {
        if (pageName.equals("feed")) {
            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                try {
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } catch (Exception e) {
                }
                home.setProfile();
                try {

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.accumulate("page", 1);
                    jsonParam.accumulate("app_id", Constant.APP_ID);
                    Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.FEED, jsonParam,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
//                               ************ start  unread message  counter api   call  ****************
                                        try {
                                            ApiController apiController = new ApiController(getActivity());
                                            apiController.getMessageCount(Feed.this);
                                        } catch (Exception e) {
                                        }
//                                  *********** end unread message  counter api   call  ****************
                                        Log.i("Alpha response", response.toString());
                                        JSONObject jsonParam = new JSONObject(response.toString());
                                        String status = jsonParam.getString("status");
                                        String code = jsonParam.getString("code");
                                        String message = jsonParam.getString("message");
//                                    JSONObject respObj= jsonParam.getJSONObject("data");
                                        if (code.equals("10")) {
                                            try {

                                                double pages = Double.parseDouble(jsonParam.getString("total_data"));
                                                page_count = (int) Math.ceil(pages / 10);
                                                count = 0;
                                                customFeedListAdapter.setLoaded();
                                            } catch (Exception e) {

                                            }
                                            JSONArray result = jsonParam.getJSONArray("result");
                                            if (result.length() > 0) {
                                                home.feedList.clear();
                                                customFeedListAdapter.clear();
                                                for (int i = 0; i < result.length(); i++) {
                                                    com.mdff.app.model.Feed f = new com.mdff.app.model.Feed();
                                                    try {
                                                        f.setFeed_id(result.getJSONObject(i).getString("feed_id"));
                                                        f.setFeed_unique_id(result.getJSONObject(i).getString("feed_unique_id"));
                                                        f.setComment1_user(result.getJSONObject(i).getString("first_comment_name"));
                                                        f.setComment1(result.getJSONObject(i).getString("first_comment"));
                                                        f.setComment2_user(result.getJSONObject(i).getString("second_comment_name"));
                                                        f.setComment2(result.getJSONObject(i).getString("second_comment"));
                                                        f.setIsLike(result.getJSONObject(i).getString("current_follower_likes_this_post"));
                                                        f.setAlphaCommented(result.getJSONObject(i).getString("alpha_commented"));
                                                        f.setAlpha_comment(result.getJSONObject(i).getString("alpha_comment"));
                                                        f.setStoryData_with_html(home.appUtil.modifyString(result.getJSONObject(i).getString("storyData_with_html")));
                                                    } catch (Exception e) {
                                                    }
                                                    f.setTitle(result.getJSONObject(i).getString("title"));
//                                                f.setDateAndTime(home.appUtil.getFormattedDateTime(result.getJSONObject(i).getString("dateAndTime")));
//                                            f.setDateAndTime(getFormattedDateTime("17.04.2018 11:39:31"));
                                                    f.setDateAndTime(result.getJSONObject(i).getString("dateAndTime"));
                                                    f.setSocialPlatformName(result.getJSONObject(i).getString("socialPlatformName"));
                                                    f.setStoryData(result.getJSONObject(i).getString("storyData"));
                                                    f.setNumberOfLikes(home.appUtil.validateVal(result.getJSONObject(i).getString("numberOfLikes")));
                                                    f.setNumberOfComments(home.appUtil.validateVal(result.getJSONObject(i).getString("numberOfComments")));
//                                        f.setAsset(result.getJSONObject(i).getJSONArray("asset"));
                                                    JSONObject asset = result.getJSONObject(i).getJSONObject("asset");
                                                    ArrayList<Asset> assets = new ArrayList<>();
                                           /* ArrayList<Asset> assets=new ArrayList<>();
                                            for(int j=0; j<asset.length();j++) {*/
                                                    Asset a = new Asset();
                                                    a.setType(asset.getString("type"));
                                                    a.setUrl(asset.getString("url"));
                                                    a.setThumbnail_url(asset.getString("thumbnail_url"));
                                                    assets.add(a);
//                                            }
                                                    f.setAsset(assets);
                                                    home.feedList.add(f);
                                                }


                                                customFeedListAdapter.addAll(home.feedList);
//                                            customFeedListAdapter.notifyItemRangeInserted(home.feedList);
//                                            customFeedListAdapter.notifyItemRangeChanged(0, customFeedListAdapter.getItemCount());
                                                rv_feed.setAdapter(customFeedListAdapter);
                                                customFeedListAdapter.setLoaded();

                                                swipeContainer.setRefreshing(false);
                                                try {
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                } catch (Exception e) {
                                                }
                                                customFeedListAdapter.setLoaded();
                                            } else {
                                                try {
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                } catch (Exception e) {
                                                }
                                                swipeContainer.setRefreshing(false);
                                                Toast.makeText(getActivity(), getString(R.string.noneed), Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            try {
                                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                            } catch (Exception e) {
                                            }
                                            swipeContainer.setRefreshing(false);
                                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
//                                        progressDialog.dismiss();
                                        }


                                    } catch (Throwable e) {
//                                    progressDialog.dismiss();
                                        try {
                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        } catch (Exception e1) {
                                        }
                                        swipeContainer.setRefreshing(false);
                                        Log.i("Excep", "error----" + e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("Volley error resp", "error----" + error.getMessage());
//                        progressDialog.dismiss();

                            if (error.networkResponse == null) {
                                if (error.getClass().equals(TimeoutError.class)) {

                                    try {
                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    } catch (Exception e) {
                                    }
                                    swipeContainer.setRefreshing(false);
                                    Toast.makeText(getActivity(), "Time out error", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                if (error.networkResponse.statusCode == 401) {
                                    if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                        ApiController apiController = new ApiController(getActivity());
                                        apiController.userLogin(Feed.this);
                                    } else {
                                        try {
                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        } catch (Exception e) {
                                        }
                                        swipeContainer.setRefreshing(false);
                                        alertMessage = AlertMessage.newInstance(
                                                getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                                        alertMessage.show(getActivity().getFragmentManager(), "");
                                    }

                                }
                            }

                        }
                    }) {    //this is the part, that adds the header to the request
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json ;charset=utf-8");
                            params.put("Authorization", "Bearer " + home.appUtil.getPrefrence("accessToken"));
                            return params;
                        }
                    };

                    RetryPolicy policy = new DefaultRetryPolicy
                            (50000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    jsonObjectRequest.setRetryPolicy(policy);


                    AlphaApplication.getInstance().addToRequestQueue(jsonObjectRequest);

                } catch (Exception e) {
                    try {
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    } catch (Exception e1) {
                    }
                    swipeContainer.setRefreshing(false);
                    e.printStackTrace();
//                progressDialog.dismiss();
                }

            } else {
                try {
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                } catch (Exception e) {
                }
                swipeContainer.setRefreshing(false);
                alertMessage = AlertMessage.newInstance(
                        getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                alertMessage.show(getActivity().getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
            }

        }
    }

    public void getFeedList2() {

        if (pageName.equals("feed")) {
            if (count < page_count) {
                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
              /*  try {
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } catch (Exception e) {
                }*/
                    home.setProfile();
                    try {
                        JSONObject jsonParam = new JSONObject();
                        jsonParam.accumulate("page", count + 1);
                        jsonParam.accumulate("app_id", Constant.APP_ID);
                        Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.FEED, jsonParam,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
//                               ************ start  unread message  counter api   call  ****************
                                            try {
                                                ApiController apiController = new ApiController(getActivity());
                                                apiController.getMessageCount(Feed.this);
                                            } catch (Exception e) {
                                            }
//                                  *********** end unread message  counter api   call  ****************
                                            Log.i("Alpha response", response.toString());
                                            JSONObject jsonParam = new JSONObject(response.toString());
                                            String status = jsonParam.getString("status");
                                            String code = jsonParam.getString("code");
                                            String message = jsonParam.getString("message");
//                                    JSONObject respObj= jsonParam.getJSONObject("data");
                                            if (code.equals("10")) {
                                                JSONArray result = jsonParam.getJSONArray("result");
                                                if (result.length() > 0) {
                                                    if (count > 0) {
                                                        home.feedList.remove(home.feedList.size() - 1);
                                                        customFeedListAdapter.notifyItemRemoved(home.feedList.size());
                                                    }

                                                    for (int i = 0; i < result.length(); i++) {
                                                        com.mdff.app.model.Feed f = new com.mdff.app.model.Feed();
                                                        try {
                                                            f.setFeed_id(result.getJSONObject(i).getString("feed_id"));
                                                            f.setFeed_unique_id(result.getJSONObject(i).getString("feed_unique_id"));
                                                            f.setComment1_user(result.getJSONObject(i).getString("first_comment_name"));
                                                            f.setComment1(result.getJSONObject(i).getString("first_comment"));
                                                            f.setComment2_user(result.getJSONObject(i).getString("second_comment_name"));
                                                            f.setComment2(result.getJSONObject(i).getString("second_comment"));
                                                            f.setIsLike(result.getJSONObject(i).getString("current_follower_likes_this_post"));
                                                            f.setAlphaCommented(result.getJSONObject(i).getString("alpha_commented"));
                                                            f.setAlpha_comment(result.getJSONObject(i).getString("alpha_comment"));
                                                            f.setStoryData_with_html(home.appUtil.modifyString(result.getJSONObject(i).getString("storyData_with_html")));
                                                        } catch (Exception e) {
                                                        }
                                                        f.setTitle(result.getJSONObject(i).getString("title"));
//                                                f.setDateAndTime(home.appUtil.getFormattedDateTime(result.getJSONObject(i).getString("dateAndTime")));
//                                            f.setDateAndTime(getFormattedDateTime("17.04.2018 11:39:31"));
                                                        f.setDateAndTime(result.getJSONObject(i).getString("dateAndTime"));
                                                        f.setSocialPlatformName(result.getJSONObject(i).getString("socialPlatformName"));
                                                        f.setStoryData(result.getJSONObject(i).getString("storyData"));
                                                        f.setNumberOfLikes(home.appUtil.validateVal(result.getJSONObject(i).getString("numberOfLikes")));
                                                        f.setNumberOfComments(home.appUtil.validateVal(result.getJSONObject(i).getString("numberOfComments")));
//                                        f.setAsset(result.getJSONObject(i).getJSONArray("asset"));
                                                        JSONObject asset = result.getJSONObject(i).getJSONObject("asset");
                                                        ArrayList<Asset> assets = new ArrayList<>();
                                           /* ArrayList<Asset> assets=new ArrayList<>();
                                            for(int j=0; j<asset.length();j++) {*/
                                                        Asset a = new Asset();
                                                        a.setType(asset.getString("type"));
                                                        a.setUrl(asset.getString("url"));
                                                        a.setThumbnail_url(asset.getString("thumbnail_url"));
                                                        assets.add(a);
//                                            }
                                                        f.setAsset(assets);
                                                        home.feedList.add(f);
                                                        mHandler = new Handler();
                                                        mHandler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                customFeedListAdapter.notifyItemInserted(home.feedList.size());
                                                            }
                                                        });

                                                    }

                                                    customFeedListAdapter.setLoaded();
                                                } else {

                                                    Toast.makeText(getActivity(), getString(R.string.noneed), Toast.LENGTH_LONG).show();
                                                    if (count > 0) {
                                                        count--;
                                                        mHandler = new Handler();
                                                        mHandler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                home.feedList.remove(home.feedList.size() - 1);
                                                                customFeedListAdapter.notifyItemRemoved(home.feedList.size());
                                                                customFeedListAdapter.setLoaded();
                                                            }
                                                        });

                                                    }

                                                }
                                            } else {

                                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                                if (count > 0) {
                                                    count--;
                                                    mHandler = new Handler();
                                                    mHandler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            home.feedList.remove(home.feedList.size() - 1);
                                                            customFeedListAdapter.notifyItemRemoved(home.feedList.size());
                                                            customFeedListAdapter.setLoaded();
                                                        }
                                                    });

                                                }
                                            }
                                        } catch (Throwable e) {

                                            Log.i("Excep", "error----" + e.getMessage());
                                            e.printStackTrace();
                                            if (count > 0) {
                                                count--;
                                                mHandler = new Handler();
                                                mHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        home.feedList.remove(home.feedList.size() - 1);
                                                        customFeedListAdapter.notifyItemRemoved(home.feedList.size());
                                                        customFeedListAdapter.setLoaded();
                                                    }
                                                });

                                            }

                                        }


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("Volley error resp", "error----" + error.getMessage());
//                        progressDialog.dismiss();
                                if (count > 0) {
                                    count--;
                                    mHandler = new Handler();
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            home.feedList.remove(home.feedList.size() - 1);
                                            customFeedListAdapter.notifyItemRemoved(home.feedList.size());
                                            customFeedListAdapter.setLoaded();
                                        }
                                    });

                                }
                                if (error.networkResponse == null) {
                                    if (error.getClass().equals(TimeoutError.class)) {

                                        try {
                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        } catch (Exception e) {
                                        }
                                        swipeContainer.setRefreshing(false);
                                        Toast.makeText(getActivity(), "Time out error", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    if (error.networkResponse.statusCode == 401) {
                                        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                            ApiController apiController = new ApiController(getActivity());
                                            apiController.userLogin(Feed.this);
                                        } else {
                                            alertMessage = AlertMessage.newInstance(
                                                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                                            alertMessage.show(getActivity().getFragmentManager(), "");
                                        }

                                    }
                                }

                            }
                        }) {    //this is the part, that adds the header to the request
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("Content-Type", "application/json ;charset=utf-8");
                                params.put("Authorization", "Bearer " + home.appUtil.getPrefrence("accessToken"));
                                return params;
                            }
                        };

                        RetryPolicy policy = new DefaultRetryPolicy
                                (50000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        jsonObjectRequest.setRetryPolicy(policy);


                        AlphaApplication.getInstance().addToRequestQueue(jsonObjectRequest);

                    } catch (Exception e) {
                        e.printStackTrace();

                        if (count > 0) {
                            count--;
                            mHandler = new Handler();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    home.feedList.remove(home.feedList.size() - 1);
                                    customFeedListAdapter.notifyItemRemoved(home.feedList.size());
                                    customFeedListAdapter.setLoaded();
                                }
                            });

                        }
//                progressDialog.dismiss();
                    }

                } else {

                    if (count > 0) {
                        count--;
                        mHandler = new Handler();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                home.feedList.remove(home.feedList.size() - 1);
                                customFeedListAdapter.notifyItemRemoved(home.feedList.size());
                                customFeedListAdapter.setLoaded();
                            }
                        });

                    }
                    alertMessage = AlertMessage.newInstance(
                            getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                    alertMessage.show(getActivity().getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(home, getString(R.string.no_feeds), Toast.LENGTH_SHORT).show();
                if (count > 0) {
                    count--;
                    mHandler = new Handler();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            home.feedList.remove(home.feedList.size() - 1);
                            customFeedListAdapter.notifyItemRemoved(home.feedList.size());
                            customFeedListAdapter.setLoaded();
                        }
                    });

                }

            }
        }
    }

    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {
        dialog.dismiss();
    }


    @Override
    public void onSuccessResponse(String code, String message, String status) {
        if (code.equals("10")) {
            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                count = 0;
                page_count = 0;
                customFeedListAdapter.setLoaded();
                getFeedList();
            } else {
                swipeContainer.setRefreshing(false);
                AlertMessage alertMessage = AlertMessage.newInstance(
                        getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                alertMessage.show(getActivity().getFragmentManager(), "");
            }
        } else {
            swipeContainer.setRefreshing(false);
            home.isFinish = true;
            alertMessage = AlertMessage.newInstance(
                    message, getString(R.string.ok), status);
            alertMessage.show(getActivity().getFragmentManager(), "");

        }
    }

    @Override
    public void postLikeDislike(String fid, String id, final String st, final VolleyCallback volleyCallback, final int pos, final String comeFrom, final ImageButton iv_like, final com.mdff.app.model.Feed f, final Activity activity, final TextView tv_like, final LinearLayout iv_likeLayout) {
        if (ConnectivityReceiver.isConnected(activity) != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                JSONObject jsonParam = new JSONObject();
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JSONObject object = new JSONObject();
                object.accumulate("feed_id", id);
                object.accumulate("feed_unique_id", fid);
                object.accumulate("like", st);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.LIKE_POST, object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("Alpha response", response.toString());
                                    JSONObject jsonParam = new JSONObject(response.toString());
                                    String status = jsonParam.getString("status");
                                    String code = jsonParam.getString("code");
                                    String message = jsonParam.getString("message");
                                    if (code.equals("10")) {
                                        if (comeFrom.equals("feedList")) {
                                            iv_like.clearAnimation();
                                            home.feedList.get(pos).setIsLike(st);
                                            home.feedList.get(pos).setNumberOfLikes(jsonParam.getString("total_likes"));
                                            customFeedListAdapter.addAll(home.feedList);
                                        } else if (comeFrom.equals("feedDetail")) {

                                            f.setIsLike(st);
                                            f.setNumberOfLikes(jsonParam.getString("total_likes"));
                                            tv_like.setText(f.getNumberOfLikes());
                                            if (st.equals("0")) {
                                                iv_like.setBackground(activity.getResources().getDrawable(R.drawable.dislike));
                                            } else {
                                                iv_like.setBackground(activity.getResources().getDrawable(R.drawable.likes));
                                            }
                                            iv_like.clearAnimation();
                                        }
                                        iv_likeLayout.setEnabled(true);
                                    }

                                } catch (Throwable e) {

                                    Log.i("Excep", "error----" + e.getMessage());
                                    e.printStackTrace();
                                    iv_like.clearAnimation();
                                    iv_likeLayout.setEnabled(true);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley error resp", "error----" + error.getMessage());
                        iv_like.clearAnimation();
                        iv_likeLayout.setEnabled(true);
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
//                                pDialog.dismiss();
//                                Toast.makeText(, "Time out error", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (error.networkResponse.statusCode == 401) {
                                if (ConnectivityReceiver.isConnected(activity) != NetworkUtils.TYPE_NOT_CONNECTED) {

                                    ApiController apiController = new ApiController(activity);
                                    apiController.userLogin(volleyCallback);
                                } else {
                                    AlertMessage alertMessage = AlertMessage.newInstance(
                                            activity.getString(R.string.noInternet), activity.getString(R.string.ok), activity.getString(R.string.alert));
                                    alertMessage.show(activity.getFragmentManager(), "");
                                }
                            }
                        }


                    }
                }) {    //this is the part, that adds the header to the request
                    @Override
                    public Map<String, String> getHeaders() {
                        AppUtil appUtil = new AppUtil(activity);
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json ;charset=utf-8");
                        params.put("Authorization", "Bearer " + appUtil.getPrefrence("accessToken"));
                        return params;
                    }
                };

                RetryPolicy policy = new DefaultRetryPolicy
                        (50000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);


                AlphaApplication.getInstance().addToRequestQueue(jsonObjectRequest);

            } catch (Exception e) {
                iv_like.clearAnimation();
                iv_likeLayout.setEnabled(true);
                e.printStackTrace();
//                progressDialog.dismiss();
            }

        } else {
            iv_like.clearAnimation();
            iv_likeLayout.setEnabled(true);
            AlertMessage alertMessage = AlertMessage.newInstance(
                    activity.getString(R.string.noInternet), activity.getString(R.string.ok), activity.getString(R.string.alert));
            alertMessage.show(activity.getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }

    }
}
