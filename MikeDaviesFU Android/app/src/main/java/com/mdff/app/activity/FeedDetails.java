package com.mdff.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mdff.app.R;
import com.mdff.app.adapter.CustomCommentListAdapter;
import com.mdff.app.app_interface.VolleyCallback;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.fragment.Feed;
import com.mdff.app.model.Comment;
import com.mdff.app.utility.AlertMessage;
import com.mdff.app.utility.ApiController;
import com.mdff.app.utility.AppUtil;
import com.mdff.app.utility.ConnectivityReceiver;
import com.mdff.app.utility.Constant;
import com.mdff.app.utility.NetworkUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FeedDetails extends AppCompatActivity implements VolleyCallback{
    com.mdff.app.model.Feed data;
    RelativeLayout imv_layout;
    CircularProgressBar circularProgressBar;
    private TextView tv_title, tv_date, tv_socialsite, tv_story, tv_likescount, tv_comment_count,tv_pipe,tv_alpha_comment;
    ImageView imv_story,iv_play;private boolean _hasLoadedOnce= false; //  boolean field
    private LinearLayout backLayout, like_comment_layout;private EditText commentEdit; private  LinearLayout sendLayout;
    private RecyclerView rv_comment;private CustomCommentListAdapter customCommentListAdapter;
    ArrayList<Comment> comments;    private int connectStatus; AlertMessage alertMessage;
    private ImageButton iv_like;String apiName;String likeSt;LinearLayout alpha_comment_layout;
    private WebView wv_description;String toUserMobileNo;Activity activity;AppUtil appUtil;boolean isFinish=false;
    LinearLayout iv_likeLayout;ScrollView scrollView;
//   for  iframe
    private MyWebChromeClient mWebChromeClient = null;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_feed_details);
        activity=FeedDetails.this;
        appUtil=new AppUtil(activity);
        data = (com.mdff.app.model.Feed) getIntent().getSerializableExtra("feeddetails");
        connectStatus = ConnectivityReceiver.isConnected(activity);
        comments = new ArrayList<>();
        initializeIds();
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            getCommentList();
        } else {

            AlertMessage alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
            alertMessage.show(getFragmentManager(), "");
        }
    }
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView wv, String url) {
            boolean rt = false;
            try {

                if (url.startsWith("tel:")) {
                    toUserMobileNo = url.substring(4);
                    phoneCall(toUserMobileNo);
                    rt = true;
                } else if (url.startsWith("email:")) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{url.substring(6)});
                    startActivity(intent);
                    rt = true;
                } else if (url.startsWith("url:")) {
                    if (url.substring(4).contains("http://") || url.substring(4).contains("https://")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url.substring(4)));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://" + url.substring(4)));
                        startActivity(intent);

                    }
                    return true;
                } else if (url.startsWith("http://") || url.startsWith("https://")) {

                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://" + url));
                    startActivity(intent);
                    return true;
                }

            }
            catch (Exception e)
            {

            }

            return rt;
        }


    }
    private void initializeIds() {
        circularProgressBar = (CircularProgressBar) findViewById(R.id.homeloader);
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        scrollView=(ScrollView) findViewById(R.id.scrollView);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_socialsite = (TextView) findViewById(R.id.tv_socialsite);
        tv_story = (TextView) findViewById(R.id.tv_story);
        tv_story.setVisibility(View.GONE);
        wv_description = (WebView) findViewById(R.id.wv_description);
        wv_description.setWebViewClient(new CustomWebViewClient());
        tv_pipe = (TextView) findViewById(R.id.tv_pipe);
        tv_alpha_comment = (TextView) findViewById(R.id.tv_alpha_comment);
        commentEdit=(EditText) findViewById(R.id.commentEdit);
        commentEdit.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(commentEdit, InputMethodManager.HI);
        sendLayout = (LinearLayout) findViewById(R.id.sendLayout);
        iv_likeLayout=(LinearLayout) findViewById(R.id.iv_likeLayout);
        iv_like=(ImageButton) findViewById(R.id.iv_like);
        alpha_comment_layout= (LinearLayout) findViewById(R.id.alpha_comment_layout);
        sendLayout.setEnabled(false);
        Typeface faceLightItalic = Typeface.createFromAsset(activity.getAssets(),
                "fonts/HelveticaNeue-LightItalic.ttf");
        Typeface faceMedium = Typeface.createFromAsset(activity.getAssets(),
                "fonts/helvetica-neue-medium.ttf");
        Typeface faceRegular = Typeface.createFromAsset(activity.getAssets(),
                "fonts/helvetica-neue-regular.ttf");
        Typeface faceLight = Typeface.createFromAsset(activity.getAssets(),
                "fonts/HelveticaNeue-Light.ttf");
        tv_story.setTypeface(faceMedium);
        tv_alpha_comment.setTypeface(faceLightItalic);
        tv_likescount = (TextView) findViewById(R.id.tv_likescount);
        tv_comment_count = (TextView) findViewById(R.id.tv_comment_count);
        imv_story = (ImageView) findViewById(R.id.imv_story);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        //  imv_layout = (FrameLayout) findViewById(R.id.imv_layout);
        imv_layout = (RelativeLayout) findViewById(R.id.imv_layout);
        like_comment_layout = (LinearLayout) findViewById(R.id.like_comment_layout);
        tv_date.setText(data.getDateAndTime());
        tv_story.setText(data.getStoryData());
        tv_likescount.setText(data.getNumberOfLikes());
        tv_comment_count.setText(data.getNumberOfComments());

        try {
            wv_description.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
            wv_description.getSettings().setJavaScriptEnabled(true);
            wv_description.getSettings().setAllowFileAccess(true);
            mWebChromeClient = new MyWebChromeClient();
            wv_description.setWebChromeClient(mWebChromeClient);
//            wv_description.setWebChromeClient(new WebChromeClient());
          /*  wv_description.getSettings().setTextZoom(100);
            wv_description.setInitialScale(100);*/


            wv_description.loadDataWithBaseURL(null, data.getStoryData_with_html(), "text/html", "utf-8", null);
//            wv_description.loadDataWithBaseURL(null, h, "text/html", "utf-8", null);

        }
        catch(Exception e)
        {}
        if (data.getSocialPlatformName().equals("Alpha Post")) {
            like_comment_layout.setVisibility(View.VISIBLE);
            tv_socialsite.setText(data.getTitle());

        } else {
            tv_socialsite.setText(data.getSocialPlatformName());
            like_comment_layout.setVisibility(View.VISIBLE);
        }

        if (data.getAsset().get(0).getType().equals("text")) {
            imv_layout.setVisibility(View.GONE);
            circularProgressBar.setVisibility(View.GONE);

        } else {
            imv_layout.setVisibility(View.VISIBLE);

            try {
                String imageUri;
                if (data.getAsset().get(0).getType().equalsIgnoreCase("video")) {
                    iv_play.setVisibility(View.VISIBLE);

                    imageUri = data.getAsset().get(0).getThumbnail_url();
                } else {
                    iv_play.setVisibility(View.GONE);
                    imageUri = data.getAsset().get(0).getUrl();
                }
                Picasso.with(activity).load(imageUri)
                        .into(imv_story);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(data.getIsLike().equals("0"))
        {
            iv_like.setBackground(getResources().getDrawable(R.drawable.dislike));
        }
        else{
            iv_like.setBackground(getResources().getDrawable(R.drawable.likes));
        }

        if(data.getAlphaCommented().equals("1"))
        {
            alpha_comment_layout.setVisibility(View.VISIBLE);
            tv_alpha_comment.setText(data.getAlpha_comment());
        }
        else{
            alpha_comment_layout.setVisibility(View.GONE);
        }

//        comment section start
        rv_comment = (RecyclerView) findViewById(R.id.rv_comment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        rv_comment.setLayoutManager(linearLayoutManager);
        customCommentListAdapter = new CustomCommentListAdapter(comments, activity, getSupportFragmentManager());
        rv_comment.setAdapter(customCommentListAdapter);
//        comment section end

        imv_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("type", data.getAsset().get(0).getType());
                bundle.putString("url", data.getAsset().get(0).getUrl());
                bundle.putString("pf", data.getSocialPlatformName());
                Intent intent = new Intent(activity, VideoImageDisplay.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onBackPressed();
            }
        });

        sendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                    @SuppressLint("ResourceType") Animation blink = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.animator.blink);
                    sendLayout.startAnimation(blink);
                    postCommentList();
                } else {

                    alertMessage = AlertMessage.newInstance(
                            getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                    alertMessage.show(activity.getFragmentManager(), "");
                }
            }
        });
        commentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                commentEdit.setCursorVisible(true);

            }
        });

        commentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                try {
                    if(cs.length()>0)
                    {
                        sendLayout.setEnabled(true);
                    }
                    else{
//                        commentEdit.setCursorVisible(false);
                        sendLayout.setEnabled(false);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });


        iv_likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_likeLayout.setEnabled(false);
                likeSt=data.getIsLike().equals("0")?"1":"0";
                @SuppressLint("ResourceType") Animation blink = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.animator.blink);
                iv_like.startAnimation(blink);
                Feed f=new Feed();
                System.out.println(f);
                apiName="postLikeDislike";
                f.postLikeDislike(data.getFeed_unique_id(),data.getFeed_id(),likeSt ,(VolleyCallback) f,0,"feedDetail",iv_like,data,activity,tv_likescount,iv_likeLayout);


            }


        });

    }



    public void getCommentList() {
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                JSONObject jsonParam = new JSONObject();
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JSONObject object = new JSONObject();
                object.accumulate("feed_id", data.getFeed_id());
                object.accumulate("feed_unique_id", data.getFeed_unique_id());
                object.accumulate("page", 1);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.GET_COMMENTS, object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    //                               ************ start  unread message  counter api   call  ****************
                                    try {
                                        ApiController apiController = new ApiController(activity);
                                        apiController.getMessageCount(FeedDetails.this);
                                    } catch (Exception e) {
                                    }
//                                  *********** end unread message  counter api   call  ****************
//                                    pDialog.dismiss();
                                    Log.i("Alpha response", response.toString());
                                    JSONObject jsonParam = new JSONObject(response.toString());
                                    String status = jsonParam.getString("status");
                                    String code = jsonParam.getString("code");
                                    String message = jsonParam.getString("message");

                                    if (code.equals("10")) {
                                        JSONArray result = jsonParam.getJSONArray("result");
                                        if (result.length() > 0) {
                                            for (int i = 0; i < result.length(); i++) {
                                                Comment comment = new Comment();
                                                comment.setId(result.getJSONObject(i).getString("id"));
                                                comment.setFeed_id(result.getJSONObject(i).getString("feed_id"));
                                                comment.setFeed_unique_id(result.getJSONObject(i).getString("feed_unique_id"));
                                                comment.setContent(result.getJSONObject(i).getString("comment"));
                                                comment.setUserName(result.getJSONObject(i).getString("name"));
                                                comment.setAlpha_commented(result.getJSONObject(i).getString("alpha_commented"));
                                                comments.add(comment);
                                            }
                                            customCommentListAdapter.addAll(comments);
                                            rv_comment.setAdapter(customCommentListAdapter);
                                        }
                                    }
                                }catch (Throwable e) {
                                    Log.i("Excep", "error----" + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley error resp", "error----" + error.getMessage());
//

                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
//                                pDialog.dismiss();


                                Toast.makeText(activity, "Time out error", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            if(error.networkResponse.statusCode==401)
                            {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                    apiName="getCommentList";
                                    ApiController apiController =new ApiController(activity);
                                    apiController.userLogin(FeedDetails.this);
                                }
                                else{
                                    AlertMessage    alertMessage = AlertMessage.newInstance(
                                            getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                                    alertMessage.show(activity.getFragmentManager(), "");
                                }
                            }
                        }


                    }
                }) {    //this is the part, that adds the header to the request
                    @Override
                    public Map<String, String> getHeaders() {
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

                e.printStackTrace();
//                progressDialog.dismiss();
            }

        } else {

            AlertMessage alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
            alertMessage.show(activity.getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }

    public void postCommentList() {
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                JSONObject jsonParam = new JSONObject();
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JSONObject object = new JSONObject();
                object.accumulate("feed_id", data.getFeed_id());
                object.accumulate("feed_unique_id", data.getFeed_unique_id());
                object.accumulate("comment", commentEdit.getText().toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.POST_COMMENTS, object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    //                               ************ start  unread message  counter api   call  ****************
                                    try {
                                        ApiController apiController = new ApiController(activity);
                                        apiController.getMessageCount(FeedDetails.this);
                                    } catch (Exception e) {
                                    }
//                                  *********** end unread message  counter api   call  ****************
//                                    pDialog.dismiss();
                                    Log.i("Alpha response", response.toString());
                                    JSONObject jsonParam = new JSONObject(response.toString());
                                    String status = jsonParam.getString("status");
                                    String code = jsonParam.getString("code");
                                    String message = jsonParam.getString("message");

                                    if (code.equals("10")) {
                                        Comment comment = new Comment();
                                        comment.setFeed_id(jsonParam.getString("feed_id"));
                                        comment.setFeed_unique_id(jsonParam.getString("feed_unique_id"));
                                        comment.setContent(jsonParam.getString("comment"));
                                        comment.setUserName(jsonParam.getString("name"));
                                        data.setNumberOfComments(jsonParam.getString("comment_count"));
                                        tv_comment_count.setText(data.getNumberOfComments());
//                                        comments.add(comment);
                                        comments.add(0,comment);
                                        customCommentListAdapter.addAll(comments);
                                        rv_comment.setAdapter(customCommentListAdapter);
                                        new Handler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                scrollView.scrollTo(0, like_comment_layout.getTop());
                                            }
                                        });

                                        commentEdit.setText("");
                                        sendLayout.clearAnimation();
                                        appUtil.hideSoftKeyboard(activity);

                                    }
                                    else{
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        alertMessage.show(activity.getFragmentManager(), "");
                                    }

                                }catch (Throwable e) {
                                    Log.i("Excep", "error----" + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley error resp", "error----" + error.getMessage());
//

                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
//                                pDialog.dismiss();


                                Toast.makeText(activity, "Time out error", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            if(error.networkResponse.statusCode==401)
                            {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                                    apiName="postCommentList";
                                    ApiController apiController =new ApiController(activity);
                                    apiController.userLogin(FeedDetails.this);
                                }
                                else{
                                    AlertMessage    alertMessage = AlertMessage.newInstance(
                                            getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                                    alertMessage.show(activity.getFragmentManager(), "");
                                }
                            }
                        }


                    }
                }) {    //this is the part, that adds the header to the request
                    @Override
                    public Map<String, String> getHeaders() {
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

                e.printStackTrace();
//                progressDialog.dismiss();
            }

        } else {

            AlertMessage alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
            alertMessage.show(activity.getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }




    @Override
    public void onSuccessResponse(String code, String message, String status) {

        if(code.equals("10")) {
            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                if(apiName.equals("getCommentList"))
                {
                    apiName="";
                    getCommentList();
                }
                else if(apiName.equals("postCommentList"))
                {
                    apiName="";
                    postCommentList();
                }
                else if(apiName.equals("getCommentList"))
                {
                    apiName="";
                    Feed f=new Feed();
                    System.out.println(f);
                    apiName="postLikeDislike";
                    f.postLikeDislike(data.getFeed_unique_id(),data.getFeed_id(),likeSt ,(VolleyCallback) f,0,"feedDetail",iv_like,data,activity,tv_likescount,iv_likeLayout);
                }


            }
            else{

                AlertMessage alertMessage = AlertMessage.newInstance(
                        getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                alertMessage.show(activity.getFragmentManager(), "");
            }
        }
        else{

            isFinish=true;
            alertMessage = AlertMessage.newInstance(
                    message, getString(R.string.ok),status);
            alertMessage.show(activity.getFragmentManager(), "");

        }

    }

    //    phone call method
    public void phoneCall(String toUserMobileNo)
    {
        try {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 11);

            }
            else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
//            callIntent.setData(Uri.parse("tel:" + "9336515501"));//change the number
                callIntent.setData(Uri.parse("tel:" + toUserMobileNo));
                startActivity(callIntent);
            }
        }
        catch(Exception e){}
    }

    // grant permission code
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch(requestCode) {
            case 11:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    phoneCall(toUserMobileNo);

                } else {
//            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;

        }
    }

    public class MyWebChromeClient extends WebChromeClient {

        FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            try{
            mContentView = (RelativeLayout) findViewById(R.id.detail);
//            mContentView.setBackgroundColor(getResources().getColor(R.color.black));
            mContentView.setVisibility(View.GONE);
            mCustomViewContainer = new FrameLayout(activity);
            mCustomViewContainer.setLayoutParams(LayoutParameters);
//            mCustomViewContainer.setBackgroundResource(android.R.color.black);
//                setActivityBackgroundColor(getResources().getColor(R.color.black));
//                mCustomViewContainer.setBackgroundColor(getResources().getColor(R.color.black));
            view.setLayoutParams(LayoutParameters);
            mCustomViewContainer.addView(view);
            mCustomView = view;
//                mCustomView.setBackgroundColor(getResources().getColor(R.color.black));
            mCustomViewCallback = callback;
            mCustomViewContainer.setVisibility(View.VISIBLE);
            setContentView(mCustomViewContainer);

            }
            catch (Exception e){}
        }
        public void setActivityBackgroundColor(int color) {
            View view = activity.getWindow().getDecorView();
            view.setBackgroundColor(color);
        }
        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            } else {
                try{
                // Hide the custom view.

                mCustomView.setVisibility(View.GONE);
                // Remove the custom view from its container.
                mCustomViewContainer.removeView(mCustomView);
                mCustomView = null;

                mCustomViewContainer.setVisibility(View.GONE);
                mCustomViewContainer=null;
                mCustomViewCallback.onCustomViewHidden();
                // Show the content view.
//                mContentView.setBackgroundColor(getResources().getColor(R.color.white));
                mContentView.setVisibility(View.VISIBLE);
                setContentView(mContentView);
//                    setActivityBackgroundColor(getResources().getColor(R.color.white));
                }
                catch (Exception e){}
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mCustomViewContainer != null)
            try {
                mWebChromeClient.onHideCustomView();
            }
            catch (Exception e){}
        else if (wv_description.canGoBack())
            try{
            wv_description.goBack();
            }
            catch (Exception e){}
        else
            try{
            super.onBackPressed();
            }
            catch (Exception e){}
    }

}
