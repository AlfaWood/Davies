package com.mdff.app.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mdff.app.R;
import com.mdff.app.adapter.CustomResourceSubListItemsAdapter;
import com.mdff.app.app_interface.ResoucrceInterface;
import com.mdff.app.app_interface.VolleyCallback;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.model.ResourceItemContent;
import com.mdff.app.model.ResourceItems;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResourceSubList extends AppCompatActivity implements VolleyCallback,ResoucrceInterface,AlertMessage.NoticeDialogListenerWithoutView {

    private EditText et_search;
    ListView lv_resourcesubList;
    ResourceItems resourcedata;
    private TextView tv_header_title;
    private int connectStatus;
    private AppUtil appUtil;
    private CustomResourceSubListItemsAdapter customResourceSubListAdapter;
    private ArrayList<ResourceItems> resourcessubList;
    private AlertMessage alertMessage;
    View view_bottom;
    LinearLayout backLayout; private ArrayList<ResourceItemContent> resourceItemContents;
    public static ArrayList<ResourceItemContent> resourceItemContentsStatic;
    private SwipeRefreshLayout swipeContainer;Activity activity;FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_resource_sub_list);
        activity=ResourceSubList.this;
        appUtil = new AppUtil(activity);
        connectStatus = ConnectivityReceiver.isConnected(activity);
        resourcessubList = new ArrayList<>();
        linkUIElements();

        swipeContainer.post(new Runnable() {
            @Override public void run() {
                swipeContainer.setRefreshing(true);
                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                    getResourceSubList();
                } else {
                    swipeContainer.setRefreshing(false);
                    AlertMessage alertMessage = AlertMessage.newInstance(
                            getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                    alertMessage.show(getFragmentManager(), "");
                }
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                    getResourceSubList();
                } else {
                    swipeContainer.setRefreshing(false);
                    AlertMessage alertMessage = AlertMessage.newInstance(
                            getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                    alertMessage.show(getFragmentManager(), "");
                }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.header_bg);
        
        
    }

    private void linkUIElements() {
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        view_bottom = (View) findViewById(R.id.view_bottom);
        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setTextColor(getResources().getColor(R.color.black));
        et_search.setCursorVisible(false);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/HelveticaNeue-Light.ttf");
        et_search.setTypeface(face);
//        resourcedata = (ResourceItems) getArguments().getSerializable("resource");
        resourcedata = (ResourceItems) getIntent().getSerializableExtra("resource");
        tv_header_title = (TextView) findViewById(R.id.tv_header_title);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        lv_resourcesubList = (ListView) findViewById(R.id.lv_resourcesubList);
        tv_header_title.setText(resourcedata.getTopic());
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    appUtil.hideSoftKeyboard(ResourceSubList.this);
                    handled = true;
                }
                return handled;
            }
        });
        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                et_search.setCursorVisible(true);


            }
        });
        //search topic on edittext
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                try {
                    if (et_search.hasFocus() && !cs.toString().isEmpty()) {
                        //    customResourceListAdapter.getFilter().filter(cs.toString());
                        customResourceSubListAdapter.getFilter().filter(cs.toString());
                        // Do whatever
                    }
                    else if(et_search.hasFocus() && cs.toString().isEmpty())
                    {
                        customResourceSubListAdapter = new CustomResourceSubListItemsAdapter(resourcessubList, activity, getSupportFragmentManager(), ResourceSubList.this);
                        lv_resourcesubList.setAdapter(customResourceSubListAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // When user changed the Text
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
    }



    public void getResourceSubList() {
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                JSONObject jsonParam = new JSONObject();
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JSONObject object = new JSONObject();
                object.accumulate("sub_topic_id", resourcedata.getId());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.RESOURCE_SUB_TOPICS, object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    //                               ************ start  unread message  counter api   call  ****************
                                    try {
                                        ApiController apiController = new ApiController(activity);
                                        apiController.getMessageCount(ResourceSubList.this);
                                    }
                                    catch(Exception e)
                                    {}
//                                  *********** end unread message  counter api   call  ****************
//                                    pDialog.dismiss();
                                    Log.i("Alpha response", response.toString());
                                    JSONObject jsonParam = new JSONObject(response.toString());
                                    String status = jsonParam.getString("status");
                                    String code = jsonParam.getString("code");
                                    String message = jsonParam.getString("message");

                                    if (code.equals("10")) {
                                        JSONArray result = jsonParam.getJSONArray("result");
                                        resourcessubList.clear();
                                        if (result.length() > 0) {
                                            view_bottom.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < result.length(); i++) {
                                                ResourceItems resourceItems = new ResourceItems();
                                                JSONObject jsonObject = result.getJSONObject(i);
                                                for (int j = 0; j < jsonObject.length(); j++) {
                                                    JSONObject jsonObject1 = jsonObject.getJSONObject("asset");
                                                    if (jsonObject1.length() > 0) {
                                                        resourceItems.setTopic(jsonObject1.getString("topic"));
                                                        resourceItems.setId(jsonObject1.getString("id"));
                                                        resourceItems.setDescription(jsonObject1.getString("description"));
                                                        resourceItems.setSub_topic_id(jsonObject1.getString("sub_topic"));
                                                        resourcessubList.add(resourceItems);
                                                        setListAdapter();
                                                    }

                                                    else {
//                                                        pDialog.dismiss();
                                                        swipeContainer.setRefreshing(false);

                                                        //     Toast.makeText(activity, getString(R.string.noresource), Toast.LENGTH_LONG).show();
                                                        alertMessage = AlertMessage.newInstance(
                                                                message, getString(R.string.ok), getString(R.string.alert));
                                                        alertMessage.show(activity.getFragmentManager(), "");
                                                    }

                                                    swipeContainer.setRefreshing(false);
                                                }
                                                swipeContainer.setRefreshing(false);
                                            }
                                        } else {
//                                            pDialog.dismiss();
                                            swipeContainer.setRefreshing(false);

                                            alertMessage = AlertMessage.newInstance(
                                                    message, getString(R.string.ok), getString(R.string.alert));
                                            alertMessage.show(activity.getFragmentManager(), "");
                                        }
                                    } else {
                                        swipeContainer.setRefreshing(false);
                                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
//                                        progressDialog.dismiss();
                                    }
                                } catch (Throwable e) {
//                                    pDialog.dismiss();
//                                    progressDialog.dismiss();
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
                        swipeContainer.setRefreshing(false);
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
//                                pDialog.dismiss();

                                swipeContainer.setRefreshing(false);
                                Toast.makeText(activity, "Time out error", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            if(error.networkResponse.statusCode==401)
                            {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                    ApiController apiController =new ApiController(activity);
                                    apiController.userLogin(ResourceSubList.this);
                                }
                                else{
                                    alertMessage = AlertMessage.newInstance(
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
                swipeContainer.setRefreshing(false);
                e.printStackTrace();
//                progressDialog.dismiss();
            }

        } else {
            swipeContainer.setRefreshing(false);
            AlertMessage alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
            alertMessage.show(activity.getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }


    private void setListAdapter() {
        customResourceSubListAdapter = new CustomResourceSubListItemsAdapter(resourcessubList, activity, getSupportFragmentManager(), ResourceSubList.this);
        lv_resourcesubList.setAdapter(customResourceSubListAdapter);
    }

    @Override
    public void onSuccessResponse(String code, String message, String status) {
        if(code.equals("10")) {
            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                getResourceSubList();
            }
            else{
                alertMessage = AlertMessage.newInstance(
                        getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                alertMessage.show(activity.getFragmentManager(), "");
            }
        }
        else{

            alertMessage = AlertMessage.newInstance(
                    message, getString(R.string.ok),status);
            alertMessage.show(activity.getFragmentManager(), "");

        }
    }


    @Override
    public void displayContent(String id, final FragmentTransaction fragmentTransaction1) {

        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                try {
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
                catch (Exception e)
                {}
                JSONObject jsonParam = new JSONObject();
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JSONObject object = new JSONObject();
                object.accumulate("id", id);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.RESOURCE_CONTENT, object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    //                               ************ start  unread message  counter api   call  ****************
                                    try {
                                        ApiController apiController = new ApiController(activity);
                                        apiController.getMessageCount(ResourceSubList.this);
                                    }
                                    catch(Exception e)
                                    {}
//                                  *********** end unread message  counter api   call  ****************
//                                    pDialog.dismiss();
                                    Log.i("Alpha response", response.toString());
                                    JSONObject jsonParam = new JSONObject(response.toString());
                                    String status = jsonParam.getString("status");
                                    String code = jsonParam.getString("code");
                                    String message = jsonParam.getString("message");
                                    if (code.equals("10")) {
//                                        String topic = jsonParam.getString("topic");
                                        JSONArray result = jsonParam.getJSONArray("result");
                                        if (result.length() > 0) {
                                            String topic="";
                                            try {
                                                topic = jsonParam.getString("topic");
                                            }
                                            catch (Exception e)
                                            {}
                                            resourceItemContents=new ArrayList<>();

                                            for (int i = 0; i < result.length(); i++) {
                                                JSONObject jsonObject = result.getJSONObject(i);
                                                for (int j = 0; j < jsonObject.length(); j++) {
                                                    JSONObject jsonObject1 = jsonObject.getJSONObject("asset");
                                                    if (jsonObject1.length() > 0) {
                                                        ResourceItemContent resource = new ResourceItemContent();
                                                        resource.setTopic(topic);
                                                        resource.setTopic_id(jsonObject1.getString("topic_id"));
                                                        resource.setImage(jsonObject1.getString("image"));
                                                        resource.setVideo_url(jsonObject1.getString("video_url"));
                                                        resource.setThumbnail_url(jsonObject1.getString("thumbnail_url"));
                                                        resource.setType(jsonObject1.getString("type"));
                                                        resource.setTitle(jsonObject1.getString("title"));
                                                        resource.setId(jsonObject1.getString("id"));
                                                        resource.setLocation(jsonObject1.getString("location"));
//                                                        resource.setDescription(jsonObject1.getString("description"));
                                                        resource.setDescription(appUtil.modifyString(jsonObject1.getString("description")));
                                                        resource.setCreated_at(appUtil.getFormattedDateTime(jsonObject1.getString("created_at")));
                                                        resourceItemContents.add(resource);
                                                    }

                                                    else {
//                                                        pDialog.dismiss();


                                                        //     Toast.makeText(activity, getString(R.string.noresource), Toast.LENGTH_LONG).show();
                                                        alertMessage = AlertMessage.newInstance(
                                                                message, getString(R.string.ok), getString(R.string.alert));
                                                        alertMessage.show(activity.getFragmentManager(), "");
                                                    }


                                                }

                                            }
                                            if(resourceItemContents.size()>0)
                                            {
                                                Intent intent=new Intent(activity, com.mdff.app.activity.ContentSubList.class);
//                                                intent.putExtra("contentsubList", (Serializable) resourceItemContents);
                                                resourceItemContentsStatic=resourceItemContents;
                                                intent.putExtra("comeFrom", "resourceSublist");
                                                startActivity(intent);
                                            }


                                        } else {
//                                            pDialog.dismiss();


                                            alertMessage = AlertMessage.newInstance(
                                                    message, getString(R.string.ok), getString(R.string.alert));
                                            alertMessage.show(activity.getFragmentManager(), "");
                                        }
                                        try {
                                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        }
                                        catch (Exception e){}
                                    } else {

                                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                        try {
                                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        }
                                        catch (Exception e){}
//                                        progressDialog.dismiss();
                                    }
                                } catch (Throwable e) {
//                                    pDialog.dismiss();
//                                    progressDialog.dismiss();
                                    try {
                                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    }
                                    catch (Exception e1){}
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
//                                pDialog.dismiss();

                                swipeContainer.setRefreshing(false);
                                Toast.makeText(activity, "Time out error", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            if(error.networkResponse.statusCode==401)
                            {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                    ApiController apiController =new ApiController(activity);
                                    apiController.userLogin(ResourceSubList.this);
                                }
                                else{
                                    alertMessage = AlertMessage.newInstance(
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
                try {
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                }
                catch (Exception ew){}
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
    public void displaySublist(ResourceItems resource, FragmentTransaction fragmentTransaction) {
        Intent intent=new Intent(activity, com.mdff.app.activity.ResourceSubList.class);
        intent.putExtra("resource", (Serializable) resource);
        startActivity(intent);

    }


    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}
