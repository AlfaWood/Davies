package com.mdff.app.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.mdff.app.R;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.utility.ApiController;
import com.mdff.app.model.Asset;
import com.mdff.app.model.Feed;
import com.mdff.app.utility.AlertMessage;
import com.mdff.app.utility.AppUtil;
import com.mdff.app.utility.ConnectivityReceiver;
import com.mdff.app.utility.Constant;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AlertMessage.NoticeDialogListenerWithoutView,VolleyCallback {
   private AlertMessage alertMessage;private AppUtil appUtil;
    boolean isFeed=false;public ArrayList<Feed> feedList;
    Activity activity;private String comeFrom;private boolean isFinish=false;private int connectStatus;int badge_count;
    double page_count = 0;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = MainActivity.this;
        appUtil=new AppUtil(activity);
        feedList=new ArrayList<Feed>();
        connectStatus = ConnectivityReceiver.isConnected(activity);
        getMessageCount();
        try {
            comeFrom = getIntent().getStringExtra("comeFrom");
        }
        catch(Exception e)
        {
            System.out.print(e);
        }

        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            getFeedList();
        }
        else{
            isFeed =false;
            alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
            alertMessage.show(getFragmentManager(), "");
        }


    }

    public void getMessageCount() {
        if(connectStatus !=NetworkUtils.TYPE_NOT_CONNECTED)

        {
            try {
                JSONObject jsonParam = new JSONObject();
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constant.WEB_URL + Constant.UNREADMESSAGE, null,
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
//                                    JSONObject respObj= jsonParam.getJSONObject("data");
                                    if (code.equals("10")) {
                                        JSONArray result = jsonParam.getJSONArray("result");
                                        if (result.length() > 0) {
                                            for(int i=0;i<result.length();i++) {
                                                JSONObject jsonObject = result.getJSONObject(i);
                                                badge_count = jsonObject.getInt("unreadMessages");
                                                appUtil.setPrefrence("unread_count",String.valueOf(badge_count));

                                            }
                                        } else {
                                            Toast.makeText(activity, message, Toast.LENGTH_LONG).show();

                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
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

                                Toast.makeText(MainActivity.this, "Time out error", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            if(error.networkResponse.statusCode==401)
                            {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                    ApiController apiController =new ApiController(MainActivity.this);
                                    apiController.userLogin(MainActivity.this);
                                }
                                else{
                                    alertMessage = AlertMessage.newInstance(
                                            getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                                    alertMessage.show(MainActivity.this.getFragmentManager(), "");
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
            alertMessage.show(MainActivity.this.getFragmentManager(), "");
            //  Toast.makeText(getActivity(), "No internet", Toast.LENGTH_LONG).show();
        }
    }




    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = activity.getAssets().open("feed.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void getFeedList() {

        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("page", 1);
                jsonParam.accumulate("app_key", Constant.APP_ID);
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.FEED, jsonParam,
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
                                        try {
                                            double pages = Double.parseDouble(jsonParam.getString("total_data"));
                                            page_count= (int) Math.ceil(pages / 10);
                                            count=0;
                                            System.out.println(page_count+count);
                                        }
                                        catch (Exception e)
                                        {

                                        }
                                        JSONArray result = jsonParam.getJSONArray("result");
                                        for (int i = 0; i < result.length(); i++) {
                                            Feed f=new Feed();
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
                                                f.setStoryData_with_html(appUtil.modifyString(result.getJSONObject(i).getString("storyData_with_html")));
                                            }
                                            catch (Exception e)
                                            {}
                                            f.setTitle(result.getJSONObject(i).getString("title"));
                                            f.setDateAndTime(result.getJSONObject(i).getString("dateAndTime"));
                                            f.setSocialPlatformName(result.getJSONObject(i).getString("socialPlatformName"));
                                            f.setStoryData(result.getJSONObject(i).getString("storyData"));
                                            f.setNumberOfLikes(appUtil.validateVal(result.getJSONObject(i).getString("numberOfLikes")));
                                            f.setNumberOfComments(appUtil.validateVal(result.getJSONObject(i).getString("numberOfComments")));
                                            JSONObject asset = result.getJSONObject(i).getJSONObject("asset");
                                            ArrayList<Asset> assets=new ArrayList<>();
                                            Asset a=new Asset();
                                            a.setType(asset.getString("type"));
                                            a.setUrl(asset.getString("url"));
                                            a.setThumbnail_url(asset.getString("thumbnail_url"));
                                            assets.add(a);
                                            f.setAsset(assets);
                                            feedList.add(f);
                                        }
                                        isFeed =true;
                                        Intent intent = new Intent(activity, Home.class);
                                        intent.putExtra("feedList",(Serializable)feedList);
                                        intent.putExtra("comeFrom",comeFrom);
                                        intent.putExtra("badgecount",badge_count);
                                        Bundle b =new Bundle();
                                        b.putDouble("page_count",page_count);
                                        b.putInt("count",count);
                                        intent.putExtras(b);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else  {
                                        isFeed =false;
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);

                                        alertMessage.show(getFragmentManager(), "");
                                    }

                                } catch (Throwable e) {
                                    Log.i("Excep", "error----" + e.getMessage());
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley error resp", "error----" + error.getMessage());

                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {

                                Toast.makeText(activity, "Time out error", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            if(error.networkResponse.statusCode==401)
                            {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                    ApiController apiController =new ApiController(activity);
                                    apiController.userLogin(MainActivity.this);
                                }
                                else{
                                    alertMessage = AlertMessage.newInstance(
                                            getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                                    alertMessage.show(getFragmentManager(), "");
                                }


                            }
                        }
                    }
                }){    //this is the part, that adds the header to the request
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json ;charset=utf-8");
                        params.put("Authorization", "Bearer "+appUtil.getPrefrence("accessToken"));
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
            }

        } else {
            isFeed =false;
            alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
            alertMessage.show(getFragmentManager(), "");
        }
    }

    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {
        if(isFinish)
        {
            isFinish=false;
            dialog.dismiss();
            finish();
        }
        else {
            dialog.dismiss();
        }
    }


    @Override
    public void onSuccessResponse(String code,String message,String status) {
        if(code.equals("10")) {
            isFeed = false;
            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                getFeedList();
            }
            else{
                alertMessage = AlertMessage.newInstance(
                        getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                alertMessage.show(getFragmentManager(), "");
            }
        }
        else{
            isFinish=true;
            alertMessage = AlertMessage.newInstance(
                    message, getString(R.string.ok),status);
            alertMessage.show(getFragmentManager(), "");

        }
    }

}
