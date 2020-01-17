package com.mdff.app.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mdff.app.R;
import com.mdff.app.activity.Home;
import com.mdff.app.adapter.CustomResourceAdapter;
import com.mdff.app.app_interface.ResoucrceInterface;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.model.ResourceItemContent;
import com.mdff.app.utility.ApiController;
import com.mdff.app.model.ResourceItems;
import com.mdff.app.utility.AlertMessage;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Resources extends Fragment implements VolleyCallback, ResoucrceInterface {
    private EditText et_search;
    // public   static  RecyclerView rv_resource;
    public static ListView lv_resource;
    private View view;
    private Home home;
    ProgressDialog pDialog;
    private int connectStatus;
    private CustomResourceAdapter customResourceAdapter;
    private ArrayList<ResourceItems> resourceItemsItemList;
    private AlertMessage alertMessage;
    View viewbottom;
    private SwipeRefreshLayout swipeContainer;
    private boolean _hasLoadedOnce = false; //  boolean field
    private boolean isViewShown = false;
    private ArrayList<ResourceItemContent> resourceItemContents;
    public static ArrayList<ResourceItemContent> resourceItemContentsStatic;
    public static boolean _ResourceLoadedOnce = false;

    public Resources() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_resources, container, false);
        // Inflate the layout for this fragment
        home = ((Home) getActivity());
        connectStatus = ConnectivityReceiver.isConnected(getActivity());
        Log.i("isViewShown", "isViewShown 1 -----" + isViewShown);
        if (!isViewShown) {
            Log.i("isViewShown", "isViewShown 2 -----" + isViewShown);
            linkUIElements(this.view);
            home.appUtil.hideSoftKeyboard(getActivity());
            if (getUserVisibleHint()) { // fragment is visible
                et_search.setCursorVisible(false);
                et_search.setText("");
                swipeContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(true);
                        if (ConnectivityReceiver.isConnected(getActivity()) != NetworkUtils.TYPE_NOT_CONNECTED) {
                            _ResourceLoadedOnce = false;
                            getResourceList();
                        } else {
                            swipeContainer.setRefreshing(false);
                            AlertMessage alertMessage = AlertMessage.newInstance(
                                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                            alertMessage.show(getActivity().getFragmentManager(), "");
                        }
                    }
                });
            }

            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (ConnectivityReceiver.isConnected(getActivity()) != NetworkUtils.TYPE_NOT_CONNECTED) {
                        _ResourceLoadedOnce = false;
                        getResourceList();
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
        } else {
            linkUIElements(this.view);
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
//        _ResourceLoadedOnce=true;
        getResourceList();
    }

    @Override
    public void onPause() {
        super.onPause();
        _ResourceLoadedOnce = true;
    }

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(isFragmentVisible_);
        Log.i("isViewShown", "isViewShown 4 -----" + isViewShown);
        if (getView() != null) {
            isViewShown = true;
            Log.i("isViewShown", "isViewShown 5 -----" + isViewShown);
            // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
            if (isFragmentVisible_ && !_hasLoadedOnce) {
                et_search.setCursorVisible(false);
                et_search.setText("");
                swipeContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(true);
                        if (ConnectivityReceiver.isConnected(getActivity()) != NetworkUtils.TYPE_NOT_CONNECTED) {
                            Log.i("isViewShown", "isViewShown 6 -----" + isViewShown);
                            _ResourceLoadedOnce = false;
                            getResourceList();
                        } else {
                            swipeContainer.setRefreshing(false);
                            AlertMessage alertMessage = AlertMessage.newInstance(
                                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                            alertMessage.show(getActivity().getFragmentManager(), "");
                        }
                    }
                });
            }


        } else {
            Log.i("isViewShown", "isViewShown 7 -----" + isViewShown);
            isViewShown = false;
            Log.i("isViewShown", "isViewShown 8 -----" + isViewShown);
        }
    }

    private void linkUIElements(View view) {
        viewbottom = (View) view.findViewById(R.id.view_bottom);
        et_search = (EditText) view.findViewById(R.id.et_search);
        et_search.setTextColor(getResources().getColor(R.color.black));
        lv_resource = (ListView) view.findViewById(R.id.lv_resource);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        et_search.setCursorVisible(false);
        resourceItemsItemList = new ArrayList<>();
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    home.appUtil.hideSoftKeyboard(getActivity());
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
                                                         customResourceAdapter.getFilter().filter(cs.toString());
                                                         // Do whatever
                                                     }
                                                     else if(et_search.hasFocus() && cs.toString().isEmpty())
                                                     {
                                                         customResourceAdapter = new CustomResourceAdapter(resourceItemsItemList, getActivity(), getActivity().getSupportFragmentManager(), Resources.this);
                                                         lv_resource.setAdapter(customResourceAdapter);
                                                         customResourceAdapter.notifyDataSetChanged();
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
                                         }
        );
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/HelveticaNeue-Light.ttf");
        et_search.setTypeface(face);

    }

    public void getResourceList() {
        if (Feed.pageName.equals("resource")) {
            if (ConnectivityReceiver.isConnected(getActivity()) != NetworkUtils.TYPE_NOT_CONNECTED) {
                try {
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } catch (Exception e) {
                }

                et_search.setText("");
                home.setProfile();
                try {
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.accumulate("app_id", Constant.APP_ID);
                    Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.RESOURCE, jsonParam,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {

                                        //                               ************ start  unread message  counter api   call  ****************
                                        try {
                                            ApiController apiController = new ApiController(getActivity());
                                            apiController.getMessageCount(Resources.this);
                                        } catch (Exception e) {
                                        }
//                                  *********** end unread message  counter api   call  ****************

//                                    pDialog.dismiss();
                                        Log.i("Alpha response", response.toString());
                                        JSONObject jsonParam = new JSONObject(response.toString());
                                        String status = jsonParam.getString("status");
                                        String code = jsonParam.getString("code");
                                        String message = jsonParam.getString("message");
//                                    JSONObject respObj= jsonParam.getJSONObject("data");
                                        if (code.equals("10")) {

                                            JSONArray result = jsonParam.getJSONArray("result");
                                            resourceItemsItemList.clear();
                                            if (result.length() > 0) {
                                                viewbottom.setVisibility(View.VISIBLE);
                                                for (int i = 0; i < result.length(); i++) {
                                                    ResourceItems resourceItems = new ResourceItems();
                                                    JSONObject jsonObject = result.getJSONObject(i);
                                                    for (int j = 0; j < jsonObject.length(); j++) {
                                                        JSONObject jsonObject1 = jsonObject.getJSONObject("asset");
                                                        if (jsonObject1.length() > 0) {
                                                            //  customResourceListAdapter.clear();
                                                            resourceItems.setTopic(jsonObject1.getString("topic"));
                                                            resourceItems.setId(jsonObject1.getString("id"));
                                                            resourceItems.setDescription(jsonObject1.getString("description"));
                                                            resourceItems.setSub_topic_id(jsonObject1.getString("sub_topic_id"));
                                                            resourceItemsItemList.add(resourceItems);
                                                        } else {
//                                                        pDialog.dismiss();
                                                            alertMessage = AlertMessage.newInstance(
                                                                    message, getString(R.string.ok), status);
                                                            alertMessage.show(getActivity().getFragmentManager(), "");
                                                            // Toast.makeText(getActivity(), getString(R.string.noresource), Toast.LENGTH_LONG).show();
                                                        }


                                                    }
                                                    try {
                                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                    } catch (Exception e) {
                                                    }
                                                    setListAdapter();
                                                    swipeContainer.setRefreshing(false);


                                                }
                                                try {
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                } catch (Exception e) {
                                                }
                                                swipeContainer.setRefreshing(false);
                                            } else {
                                                try {
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                } catch (Exception e) {
                                                }
                                                swipeContainer.setRefreshing(false);
//                                            pDialog.dismiss();
                                                alertMessage = AlertMessage.newInstance(
                                                        message, getString(R.string.ok), status);
                                                alertMessage.show(getActivity().getFragmentManager(), "");

                                            }
                                        } else {
                                            try {
                                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                            } catch (Exception e) {
                                            }
                                            swipeContainer.setRefreshing(false);
//                                        pDialog.dismiss();
                                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                        }

                                    } catch (Throwable e) {

//                                    pDialog.dismiss();
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
//                        pDialog.dismiss();
                            try {
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            } catch (Exception e) {
                            }
                            swipeContainer.setRefreshing(false);
                            if (error.networkResponse == null) {
                                if (error.getClass().equals(TimeoutError.class)) {
//                                swipeContainer.setRefreshing(false);

                                    Toast.makeText(getActivity(), "Time out error", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                if (error.networkResponse.statusCode == 401) {
                                    if (ConnectivityReceiver.isConnected(getActivity()) != NetworkUtils.TYPE_NOT_CONNECTED) {

                                        ApiController apiController = new ApiController(getActivity());
                                        apiController.userLogin(Resources.this);
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
                    try {
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    } catch (Exception e1) {
                    }
                    swipeContainer.setRefreshing(false);
                    e.printStackTrace();
//                pDialog.dismiss();

                }

            } else {
                try {
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                } catch (Exception e) {
                }
                swipeContainer.setRefreshing(false);
                AlertMessage alertMessage = AlertMessage.newInstance(
                        getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                alertMessage.show(getActivity().getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void setListAdapter() {
        customResourceAdapter = new CustomResourceAdapter(resourceItemsItemList, getActivity(), getActivity().getSupportFragmentManager(), Resources.this);
        lv_resource.setAdapter(customResourceAdapter);
        customResourceAdapter.notifyDataSetChanged();
    }


    @Override
    public void onSuccessResponse(String code, String message, String status) {
        if (code.equals("10")) {
            if (ConnectivityReceiver.isConnected(getActivity()) != NetworkUtils.TYPE_NOT_CONNECTED) {
                getResourceList();
            } else {
                alertMessage = AlertMessage.newInstance(
                        getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                alertMessage.show(getActivity().getFragmentManager(), "");
            }
        } else {
            home.isFinish = true;
            alertMessage = AlertMessage.newInstance(
                    message, getString(R.string.ok), status);
            alertMessage.show(getActivity().getFragmentManager(), "");

        }
    }

    @Override
    public void displayContent(String id, final FragmentTransaction fragmentTransaction) {
        if (ConnectivityReceiver.isConnected(getActivity()) != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                try {
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } catch (Exception e) {
                }
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
                                        ApiController apiController = new ApiController(getActivity());
                                        apiController.getMessageCount(Resources.this);
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
                                            String topic = "";
                                            try {
                                                topic = jsonParam.getString("topic");
                                            } catch (Exception e) {
                                            }
                                            resourceItemContents = new ArrayList<>();
                                            for (int i = 0; i < result.length(); i++) {
                                                ResourceItemContent resource = new ResourceItemContent();
                                                JSONObject jsonObject = result.getJSONObject(i);
                                                for (int j = 0; j < jsonObject.length(); j++) {
                                                    JSONObject jsonObject1 = jsonObject.getJSONObject("asset");
                                                    if (jsonObject1.length() > 0) {
                                                        resource.setTopic(topic);
                                                        resource.setTopic_id(jsonObject1.getString("topic_id"));
                                                        resource.setImage(jsonObject1.getString("image"));
                                                        resource.setVideo_url(jsonObject1.getString("video_url"));
                                                        resource.setAudio_url(jsonObject1.getString("audio_url"));
                                                        resource.setThumbnail_url(jsonObject1.getString("thumbnail_url"));
                                                        resource.setType(jsonObject1.getString("type"));
                                                        resource.setTitle(jsonObject1.getString("title"));
                                                        resource.setId(jsonObject1.getString("id"));
                                                        resource.setLocation(jsonObject1.getString("location"));
//                                                        resource.setDescription(jsonObject1.getString("description"));
                                                        resource.setDescription(home.appUtil.modifyString(jsonObject1.getString("description")));
                                                        resource.setCreated_at(home.appUtil.getFormattedDateTime(jsonObject1.getString("created_at")));
                                                        resourceItemContents.add(resource);
                                                    } else {
//                                                        pDialog.dismiss();
                                                        //     Toast.makeText(getActivity(), getString(R.string.noresource), Toast.LENGTH_LONG).show();
                                                        alertMessage = AlertMessage.newInstance(
                                                                message, getString(R.string.ok), getString(R.string.alert));
                                                        alertMessage.show(getActivity().getFragmentManager(), "");
                                                    }


                                                }

                                            }

                                            if (resourceItemContents.size() > 0) {
                                                _ResourceLoadedOnce = true;
                                                Intent intent = new Intent(getActivity(), com.mdff.app.activity.ContentSubList.class);
//                                                intent.putExtra("contentsubList", (Serializable) resourceItemContents);
                                                resourceItemContentsStatic=resourceItemContents;
                                                intent.putExtra("title", resourceItemContents.get(0).getTitle());
                                                intent.putExtra("comeFrom", "resource");
                                                startActivity(intent);
                                            }


                                        } else {
//                                            pDialog.dismiss();


                                            alertMessage = AlertMessage.newInstance(
                                                    message, getString(R.string.ok), getString(R.string.alert));
                                            alertMessage.show(getActivity().getFragmentManager(), "");
                                        }
                                        try {
                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        } catch (Exception e) {
                                        }

                                    } else {

                                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                        try {
                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        } catch (Exception e) {
                                        }
//                                        progressDialog.dismiss();
                                    }

                                } catch (Throwable e) {
//                                    pDialog.dismiss();
//                                    progressDialog.dismiss();
                                    try {
                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    } catch (Exception e2) {
                                    }
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
                                Toast.makeText(getActivity(), "Time out error", Toast.LENGTH_LONG).show();
                                try {
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                } catch (Exception e) {
                                }
                            }
                        } else {
                            if (error.networkResponse.statusCode == 401) {
                                if (ConnectivityReceiver.isConnected(getActivity()) != NetworkUtils.TYPE_NOT_CONNECTED) {

                                    ApiController apiController = new ApiController(getActivity());
                                    apiController.userLogin(Resources.this);
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
                try {
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                } catch (Exception e4) {
                }
                e.printStackTrace();
//                progressDialog.dismiss();
            }

        } else {

            AlertMessage alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
            alertMessage.show(getActivity().getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void displaySublist(ResourceItems resource, FragmentTransaction fragmentTransaction) {

        _ResourceLoadedOnce = true;
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Log.i("isViewShown", "isViewShown 9 -----" + isViewShown);

        Intent intent = new Intent(getActivity(), com.mdff.app.activity.ResourceSubList.class);
        intent.putExtra("resource", (Serializable) resource);
        startActivity(intent);
    }


}
