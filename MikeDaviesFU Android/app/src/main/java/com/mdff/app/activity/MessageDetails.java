package com.mdff.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mdff.app.R;
import com.mdff.app.app_interface.VolleyCallback;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.model.MessageItems;
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deepika.Mishra on 5/29/2018.
 */

public class MessageDetails extends AppCompatActivity implements VolleyCallback {
    Activity activity;
    ImageView iv_message, iv_play;
    WebView wv_description;
    private TextView tv_From, tv_Date, tv_To, tv_subject, tv_content;
    private LinearLayout backLayout;
    private AppUtil appUtil;
    String toUserMobileNo;
    MessageItems data;
    private String comeFrom = "";
    int connectStatus;
//    iframe
    private MyWebChromeClient mWebChromeClient = null;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_messsage_detail);
        Intent i = getIntent();
        data = (MessageItems) i.getSerializableExtra("messagedetails");
        comeFrom = i.getStringExtra("comeFrom");
        appUtil = new AppUtil(this);
        activity = MessageDetails.this;
        connectStatus = ConnectivityReceiver.isConnected(activity);
        getMessageStatusUpdated(data);
        initializeIds();

    }

    private void initializeIds() {
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        iv_message = (ImageView) findViewById(R.id.iv_message);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        tv_From = (TextView) findViewById(R.id.tv_from);
        tv_To = (TextView) findViewById(R.id.tv_to);
        tv_Date = (TextView) findViewById(R.id.tv_date);
        tv_subject = (TextView) findViewById(R.id.tv_subject);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setVisibility(View.GONE);
        wv_description = (WebView) findViewById(R.id.wv_description);
//        wv_description.setScrollContainer(false);
        wv_description.setWebViewClient(new CustomWebViewClient());
        Typeface faceMedium = Typeface.createFromAsset(getAssets(),
                "fonts/helvetica-neue-medium.ttf");
        tv_From.setTypeface(faceMedium);
        tv_To.setTypeface(faceMedium);
//        tv_subject.setTypeface(faceMedium,Typeface.BOLD);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/HelveticaNeue-Light.ttf");
        tv_Date.setTypeface(face, Typeface.ITALIC);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, Home.class);
                intent.putExtra("comeFrom", "messageDetail");
                startActivity(intent);
                finish();
            }
        });

        try {
            tv_From.setText(data.getFrom());
            tv_content.setText(data.getDescription());
            tv_subject.setText(data.getTitle());
            tv_To.setText(data.getTo());
            tv_Date.setText(data.getCreated_at());


            if (data.getType().equalsIgnoreCase("document")) {
                Picasso.with(this).load(data.getThumbnail_url())//download URL
                        .into(iv_message);//imageview
                iv_message.setVisibility(View.VISIBLE);
            } else if (data.getType().equalsIgnoreCase("image")) {
                Picasso.with(this).load(data.getAttachment_url())//download URL
                        .into(iv_message);//imageview
                iv_message.setVisibility(View.VISIBLE);


            } else if (data.getType().equalsIgnoreCase("video")) {
                Picasso.with(this).load(data.getThumbnail_url())//download URL
                        .into(iv_message);//imageview
                iv_message.setVisibility(View.VISIBLE);
                iv_play.setVisibility(View.VISIBLE);

            } else {
                iv_message.setVisibility(View.GONE);

            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        try {

            wv_description.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
            wv_description.getSettings().setJavaScriptEnabled(true);
            wv_description.getSettings().setAllowFileAccess(true);
            mWebChromeClient = new MyWebChromeClient();
            wv_description.setWebChromeClient(mWebChromeClient);
//            wv_description.setWebChromeClient(new WebChromeClient());
            wv_description.loadDataWithBaseURL(null, data.getDescription(), "text/html", "utf-8", null);
        } catch (Exception e) {
        }


        iv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (data.getType().equalsIgnoreCase("document"))
                {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getAttachment_url()));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                    }
                } else {
                    Intent i = new Intent(activity, com.mdff.app.activity.MessageVideoImage.class);
                    i.putExtra("messageData", (Serializable) data);
                    startActivity(i);
                }


            }
        });
    }

    @Override
    public void onSuccessResponse(String code, String message, String status) {


        if (code.equals("10")) {
            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                getMessageStatusUpdated(data);
            } else {
                AlertMessage alertMessage = AlertMessage.newInstance(
                        getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                alertMessage.show(getFragmentManager(), "");
            }
        } else {

            AlertMessage alertMessage = AlertMessage.newInstance(
                    message, getString(R.string.ok), status);
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

            } catch (Exception e) {

            }

            return rt;
        }


    }


    //    phone call method
    public void phoneCall(String toUserMobileNo) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 11);

            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + toUserMobileNo));
                startActivity(callIntent);
            }
        } catch (Exception e) {
        }
    }

    // grant permission code
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
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

   /* @Override
    public void onBackPressed() {
        Intent intent = new Intent(activity, Home.class);
        intent.putExtra("comeFrom", "messageDetail");
        startActivity(intent);
        finish();
    }*/

    public void getMessageStatusUpdated(MessageItems itemlist) {
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("msg_id", itemlist.getId());
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.MESSAGEREAD, jsonParam,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
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
                                                JSONObject jsonObject = result.getJSONObject(i);
                                                int badge_count = jsonObject.getInt("unreadMessages");
                                                appUtil.setPrefrence("unread_count", String.valueOf(badge_count));
                                                Intent intent = new Intent();
                                                intent.setAction("message.came");
                                                sendBroadcast(intent);

                                            }
                                        } else {
                                            Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                        }
                                    } else {
//                                        pDialog.dismiss();
                                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                    }

                                } catch (Throwable e) {
//                                    pDialog.dismiss();
                                    Log.i("Excep", "error----" + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley error resp", "error----" + error.getMessage());
//                        pDialog.dismiss();
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {

                                Toast.makeText(activity, "Time out error", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (error.networkResponse.statusCode == 401) {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                    ApiController apiController = new ApiController(activity);
                                    apiController.userLogin(MessageDetails.this);
                                } else {
                                    AlertMessage alertMessage = AlertMessage.newInstance(
                                            getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                                    alertMessage.show(getFragmentManager(), "");
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
//                pDialog.dismiss();

            }

        } else

        {
            AlertMessage alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
            alertMessage.show(getFragmentManager(), "");
            //  Toast.makeText(getActivity(), "No internet", Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(activity, Home.class);
                intent.putExtra("comeFrom", "messageDetail");
                startActivity(intent);
                finish();
            }
            catch (Exception e){}
    }
}
