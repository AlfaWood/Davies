package com.mdff.app.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mdff.app.R;
import com.mdff.app.activity.Home;
import com.mdff.app.activity.MessageDetails;

import com.mdff.app.app_interface.OnLoadMoreListener;
import com.mdff.app.app_interface.VolleyCallback;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.model.MessageItems;
import com.mdff.app.utility.AlertMessage;
import com.mdff.app.utility.ApiController;
import com.mdff.app.utility.AppUtil;
import com.mdff.app.utility.ConnectivityReceiver;
import com.mdff.app.utility.Constant;
import com.mdff.app.utility.DividerRVDecoration;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Messages extends Fragment implements  VolleyCallback {
    private boolean _hasLoadedOnce = false;
    private View view;
    private Home home;
    private int connectStatus;
    CustomMessageListAdapter customMessageListAdapter;
    private RecyclerView rv_message;
    private EditText et_search;
    String[] content = {"a", "b"};
    private SwipeRefreshLayout swipeContainer;
    private AlertMessage alertMessage;
    ArrayList<MessageItems> MessageList;
    private AppUtil appUtil;
    FragmentManager fragmentManager;
    double page_count = 0;
    int count=0;
    private Handler mHandler;



    public Messages() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        if(home.comeFrom.equals("messageDetail")) {
            home.comeFrom="";
            et_search.setCursorVisible(false);
            et_search.setText("");
            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);
                    if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                        count=0;
                        page_count=0;
                        customMessageListAdapter.setLoaded();
                        getMessageList();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_messages, container, false);

        home = ((Home) getActivity());
        connectStatus = ConnectivityReceiver.isConnected(getActivity());
        appUtil = new AppUtil(getActivity());
        linkUIElements(this.view);
        home.appUtil.hideSoftKeyboard(getActivity());
        if (getUserVisibleHint()) { // fragment is visible
            et_search.setCursorVisible(false);
            et_search.setText("");
            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);
                    if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                        count=0;
                        page_count = 0;
                        customMessageListAdapter.setLoaded();
                        getMessageList();
                    } else {
                        swipeContainer.setRefreshing(false);
                        AlertMessage alertMessage = AlertMessage.newInstance(
                                getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                        alertMessage.show(getActivity().getFragmentManager(), "");
                    }
                }
            });
        }
        customMessageListAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i("OnLoadMoreListener", "1");
                MessageList.add(null);
                mHandler = new Handler();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        customMessageListAdapter.notifyItemInserted(MessageList.size() - 1);
                    }
                });
//                ++count;
                if(page_count >count) {
                    Log.i("OnLoadMoreListener", "2");
                 count++;
                    getMessageList2();
                }

                else {
                    Log.i("OnLoadMoreListener", "3");
                    mHandler = new Handler();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MessageList.remove(MessageList.size() - 1);
                            customMessageListAdapter.notifyItemRemoved(MessageList.size());
                        }
                    });
                    Log.i("OnLoadMoreListener", "4");
                    customMessageListAdapter.setNotLoaded();
                }
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                    count=0;
                    page_count = 0;
                    customMessageListAdapter.setLoaded();
                    getMessageList();
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
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(isFragmentVisible_);
        if (isFragmentVisible_ && !_hasLoadedOnce && view != null) {
//                _hasLoadedOnce = true;
            et_search.setCursorVisible(false);
            et_search.setText("");
            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    swipeContainer.setRefreshing(true);
                    if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                        count=0;
                        page_count = 0;
                        customMessageListAdapter.setLoaded();
                        getMessageList();
                    } else {
                        swipeContainer.setRefreshing(false);
                        AlertMessage alertMessage = AlertMessage.newInstance(
                                getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                        alertMessage.show(getActivity().getFragmentManager(), "");
                    }
                }
            });
        }

//        }
//     Toast.makeText(home,"Resource Fragment",Toast.LENGTH_LONG).show();

    }


    private void linkUIElements(View view) {
        et_search = (EditText) view.findViewById(R.id.et_search);

        et_search.setTextColor(getResources().getColor(R.color.black));
        et_search.setCursorVisible(false);
        rv_message = (RecyclerView) view.findViewById(R.id.rv_messages);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_message.setLayoutManager(linearLayoutManager);
        MessageList = new ArrayList<>();
        setListAdapter();
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

                                                     if (et_search.hasFocus() && !cs.toString().isEmpty()) {                                                         //    customResourceListAdapter.getFilter().filter(cs.toString());
                                                         customMessageListAdapter.getFilter().filter(cs.toString());
                                                         // Do whatever
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

    public void getMessageList() {
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
            catch (Exception e)
            {}
            et_search.setText("");
            home.setProfile();
            try {
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("page", 1);
                jsonParam.accumulate("app_id", Constant.APP_ID);
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.MESSAGE, jsonParam,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
//                                    pDialog.dismiss();

                                    try {
                                        ApiController apiController = new ApiController(getActivity());
                                        apiController.getMessageCount(Messages.this);
                                    }
                                    catch(Exception e)
                                    {}
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
                                            page_count= (int) Math.ceil(pages / 10);
                                            count=0;
                                            customMessageListAdapter.setLoaded();

                                        }
                                        catch (Exception e)
                                        {

                                        }
                                        JSONArray result = jsonParam.getJSONArray("result");
                                        if (result.length() > 0) {
                                            MessageList.clear();
                                            customMessageListAdapter.clear();
                                            for (int i = 0; i < result.length(); i++) {
                                                MessageItems messageItems = new MessageItems();
                                                JSONObject jsonObject = result.getJSONObject(i);
                                                for (int j = 0; j < jsonObject.length(); j++) {
                                                    JSONObject jsonObject1 = jsonObject.getJSONObject("asset");
                                                    if (jsonObject1.length() > 0) {
                                                        //  customResourceListAdapter.clear();
                                                        messageItems.setId(jsonObject1.getString("id"));
                                                        messageItems.setFrom(jsonObject1.getString("from"));
                                                        messageItems.setTo(jsonObject1.getString("to"));
                                                        messageItems.setTitle(jsonObject1.getString("title"));
                                                        messageItems.setType(jsonObject1.getString("type"));
                                                        messageItems.setAttachment_url(jsonObject1.getString("attachment_url"));
//                                                        messageItems.setCreated_at(appUtil.getFormattedDateTime(jsonObject1.getString("created_at")));
                                                        messageItems.setCreated_at(jsonObject1.getString("created_at"));
                                                        messageItems.setThumbnail_url(jsonObject1.getString("thumbnail_url"));
                                                        messageItems.setDescription(home.appUtil.modifyString(jsonObject1.getString("description")));
                                                        messageItems.setMessage_read(jsonObject1.getInt("message_read"));
                                                        messageItems.setTextDescription(jsonObject1.getString("description_text"));
                                                        MessageList.add(messageItems);
//                                                        setListAdapter();
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

                                                }
                                                catch (Exception e){}
                                                swipeContainer.setRefreshing(false);
                                            }
                                            if (MessageList.size() > 0) {
                                                customMessageListAdapter.addAll(MessageList);
                                                rv_message.setAdapter(customMessageListAdapter);
                                                customMessageListAdapter.setLoaded();
                                                try {
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                }
                                                catch (Exception e){}
                                                swipeContainer.setRefreshing(false);
                                            } else {
                                                try {
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                }
                                                catch (Exception e){}
                                                swipeContainer.setRefreshing(false);
//                                            pDialog.dismiss();
                                                alertMessage = AlertMessage.newInstance(
                                                        message, getString(R.string.ok), status);
                                                alertMessage.show(getActivity().getFragmentManager(), "");

                                            }

                                        } else {
                                            try {
                                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                            }
                                            catch (Exception e){}
                                            swipeContainer.setRefreshing(false);
//                                            pDialog.dismiss();
                                            alertMessage = AlertMessage.newInstance(
                                                    message, getString(R.string.ok), status);
                                            alertMessage.show(getActivity().getFragmentManager(), "");

                                        }
                                    } else {
                                        try {
                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        }
                                        catch (Exception e){}
                                        swipeContainer.setRefreshing(false);
//                                        pDialog.dismiss();
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                    }

                                } catch (Throwable e) {

//                                    pDialog.dismiss();
                                    try {
                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    }
                                    catch (Exception e1){}
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

                        }
                        catch (Exception e){}
                        swipeContainer.setRefreshing(false);
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {

                                Toast.makeText(getActivity(), "Time out error", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (error.networkResponse.statusCode == 401) {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                    ApiController apiController = new ApiController(getActivity());
                                    apiController.userLogin(Messages.this);
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

                }
                catch (Exception e1){}
                swipeContainer.setRefreshing(false);
                e.printStackTrace();
//                pDialog.dismiss();

            }

        } else {
            try {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
            catch (Exception e){}
            swipeContainer.setRefreshing(false);
            AlertMessage alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
            alertMessage.show(getActivity().getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }

    public void getMessageList2() {
        if(count<page_count) {
            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                try {
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } catch (Exception e) {
                }
//            et_search.setText("");
                home.setProfile();
                try {
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.accumulate("page", count + 1);
                    jsonParam.accumulate("app_id", Constant.APP_ID);
                    Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.MESSAGE, jsonParam,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {
//                                    pDialog.dismiss();

                                        try {
                                            ApiController apiController = new ApiController(getActivity());
                                            apiController.getMessageCount(Messages.this);
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
                                           /* MessageList.clear();
                                            customMessageListAdapter.clear();*/
                                                if (count > 0) {
                                                    MessageList.remove(MessageList.size() - 1);
                                                    customMessageListAdapter.notifyItemRemoved(MessageList.size());
                                                }

                                                for (int i = 0; i < result.length(); i++) {
                                                    MessageItems messageItems = new MessageItems();
                                                    JSONObject jsonObject = result.getJSONObject(i);
                                                    for (int j = 0; j < jsonObject.length(); j++) {
                                                        JSONObject jsonObject1 = jsonObject.getJSONObject("asset");
                                                        if (jsonObject1.length() > 0) {
                                                            //  customResourceListAdapter.clear();
                                                            messageItems.setId(jsonObject1.getString("id"));
                                                            messageItems.setFrom(jsonObject1.getString("from"));
                                                            messageItems.setTo(jsonObject1.getString("to"));
                                                            messageItems.setTitle(jsonObject1.getString("title"));
                                                            messageItems.setType(jsonObject1.getString("type"));
                                                            messageItems.setAttachment_url(jsonObject1.getString("attachment_url"));
//                                                        messageItems.setCreated_at(appUtil.getFormattedDateTime(jsonObject1.getString("created_at")));
                                                            messageItems.setCreated_at(jsonObject1.getString("created_at"));
                                                            messageItems.setThumbnail_url(jsonObject1.getString("thumbnail_url"));
                                                            messageItems.setDescription(home.appUtil.modifyString(jsonObject1.getString("description")));
                                                            messageItems.setMessage_read(jsonObject1.getInt("message_read"));
                                                            messageItems.setTextDescription(jsonObject1.getString("description_text"));
                                                            MessageList.add(messageItems);
                                                            mHandler = new Handler();
                                                            mHandler.post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    customMessageListAdapter.notifyItemInserted(MessageList.size());
                                                                }
                                                            });
//                                                        setListAdapter();
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
                                                    swipeContainer.setRefreshing(false);
                                                }
                                                customMessageListAdapter.setLoaded();

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

                                                if (count > 0) {
                                                    count--;
                                                    mHandler = new Handler();
                                                    mHandler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            MessageList.remove(MessageList.size() - 1);
                                                            customMessageListAdapter.notifyItemRemoved(MessageList.size());
                                                            customMessageListAdapter.setLoaded();
                                                        }
                                                    });

                                                }
                                            }
                                        } else {
                                            try {
                                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                            } catch (Exception e) {
                                            }
                                            swipeContainer.setRefreshing(false);
//                                        pDialog.dismiss();
                                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                                            if (count > 0) {
                                                count--;
                                                mHandler = new Handler();
                                                mHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        MessageList.remove(MessageList.size() - 1);
                                                        customMessageListAdapter.notifyItemRemoved(MessageList.size());
                                                        customMessageListAdapter.setLoaded();
                                                    }
                                                });

                                            }
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

                                        if (count > 0) {
                                            count--;
                                            mHandler = new Handler();
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MessageList.remove(MessageList.size() - 1);
                                                    customMessageListAdapter.notifyItemRemoved(MessageList.size());
                                                    customMessageListAdapter.setLoaded();
                                                }
                                            });

                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("Volley error resp", "error----" + error.getMessage());


//                        pDialog.dismiss();
                            try {
                                if (count > 0) {
                                    count--;
                                    mHandler = new Handler();
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            MessageList.remove(MessageList.size() - 1);
                                            customMessageListAdapter.notifyItemRemoved(MessageList.size());
                                            customMessageListAdapter.setLoaded();
                                        }
                                    });

                                }
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            } catch (Exception e) {
                            }
                            swipeContainer.setRefreshing(false);
                            if (error.networkResponse == null) {
                                if (error.getClass().equals(TimeoutError.class)) {

                                    Toast.makeText(getActivity(), "Time out error", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                if (error.networkResponse.statusCode == 401) {
                                    if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                        ApiController apiController = new ApiController(getActivity());
                                        apiController.userLogin(Messages.this);
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
                        if (count > 0) {
                            count--;
                            mHandler = new Handler();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MessageList.remove(MessageList.size() - 1);
                                    customMessageListAdapter.notifyItemRemoved(MessageList.size());
                                    customMessageListAdapter.setLoaded();
                                }
                            });

                        }
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    } catch (Exception e1) {
                    }
                    swipeContainer.setRefreshing(false);
                    e.printStackTrace();
//                pDialog.dismiss();
                }
            } else {

                try {
                    if (count > 0) {
                        count--;
                        mHandler = new Handler();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                MessageList.remove(MessageList.size() - 1);
                                customMessageListAdapter.notifyItemRemoved(MessageList.size());
                                customMessageListAdapter.setLoaded();
                            }
                        });

                    }
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
        else{
            Toast.makeText(home, getString(R.string.no_msg), Toast.LENGTH_SHORT).show();
            mHandler = new Handler();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    MessageList.remove(MessageList.size() - 1);
                    customMessageListAdapter.notifyItemRemoved(MessageList.size());
                    customMessageListAdapter.setLoaded();
                }
            });

        }

    }



    private void setListAdapter() {
        customMessageListAdapter = new CustomMessageListAdapter(MessageList, getActivity(), getActivity().getSupportFragmentManager(),rv_message);
        rv_message.setAdapter(customMessageListAdapter);
        customMessageListAdapter.notifyDataSetChanged();
        rv_message.setAdapter(customMessageListAdapter);
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        rv_message.addItemDecoration(new DividerRVDecoration(dividerDrawable));
    }

    @Override
    public void onSuccessResponse(String code, String message, String status) {
        if (code.equals("10")) {
            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                count=0;
                page_count = 0;
                customMessageListAdapter.setLoaded();
                getMessageList();
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






    public void getMessageStatusUpdated( final MessageItems itemlist, final int postion) {
//        MessageItems itemlist=item;
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED)
        {
            try {
               /* pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Please wait.");
                pDialog.setCancelable(false);
                pDialog.show();*/
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
//                                    JSONObject respObj= jsonParam.getJSONObject("data");
                                    if (code.equals("10")) {
                                        JSONArray result = jsonParam.getJSONArray("result");
                                        if (result.length() > 0) {
                                            for(int i=0;i<result.length();i++) {
                                                JSONObject jsonObject = result.getJSONObject(i);
                                                int badge_count = jsonObject.getInt("unreadMessages");
                                                appUtil.setPrefrence("unread_count",String.valueOf(badge_count));
                                                Intent intent = new Intent(getActivity(), MessageDetails.class);
                                                intent.putExtra("messagedetails", itemlist);
                                                intent.putExtra("comeFrom", "messageList");
                                                intent.setAction("message.came");
                                                getActivity().sendBroadcast(intent);
                                                startActivity(intent);
                                                getActivity().finish();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                        }
                                    } else {
//                                        pDialog.dismiss();
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
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

                                Toast.makeText(getActivity(), "Time out error", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (error.networkResponse.statusCode == 401) {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                    ApiController apiController = new ApiController(getActivity());
                                    apiController.userLogin(Messages.this);
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
//                pDialog.dismiss();

            }

        } else

        {
            AlertMessage alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
            alertMessage.show(getActivity().getFragmentManager(), "");
            //  Toast.makeText(getActivity(), "No internet", Toast.LENGTH_LONG).show();
        }
    }


    public class CustomMessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

        private AppUtil appUtil;
        String content;
        String dataString;
        int position;
        private Home home;
        private int connectStatus;
        private Context context;
        FragmentManager fragmentManager;
        ArrayList<MessageItems> messageItems;
        ArrayList<MessageItems> messageItemsForSearch;
        private AlertMessage alertMessage;
        private boolean isLoading=false;
        private OnLoadMoreListener onLoadMoreListener;
        private int visibleThreshold = 1;
        private int lastVisibleItem, totalItemCount;
        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;


        public CustomMessageListAdapter(ArrayList<MessageItems> messageItems, Context context, FragmentManager fragmentManager,RecyclerView recyclerView) {
            appUtil = new AppUtil(context);
            home = ((Home) context);
            connectStatus = ConnectivityReceiver.isConnected(context);
            this.messageItemsForSearch = messageItems;
//            this.messageItems=messageItems;
            this.context = context;
            this.fragmentManager = fragmentManager;



            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
//                    isLoading = false;
                    Log.i("RecyclerView.OnScrol", "1");
//                    if(count<page_count) {
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                            Log.i("RecyclerView.OnScrol", "2");
                            isLoading = true;
                            Log.i("RecyclerView.OnScrol", "3");
                        }
                    }
                }
            });
        }


        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.onLoadMoreListener = mOnLoadMoreListener;
        }

        public void setLoaded() {
            Log.i("setLoaded", "71");
            isLoading = false;
        }

        public void setNotLoaded() {
            Log.i("setNotLoaded", "709");
            isLoading = true;
        }

        @Override
        public int getItemViewType(int position) {
            return messageItemsForSearch.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh=null;
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(context).inflate(R.layout.message_item_list, parent, false);
                vh= new ItemViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(context).inflate(R.layout.progressbar_item, parent, false);
                vh= new ProgressViewHolder(view);
            }
            return  vh;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder1,  final int pos) {
            if (holder1 instanceof ItemViewHolder) {
                final ItemViewHolder holder = (ItemViewHolder) holder1;
                if (messageItemsForSearch.get(pos).getMessage_read() == 1) {
                    Typeface faceMedium = Typeface.createFromAsset(context.getAssets(),
                            "fonts/helvetica-neue-medium.ttf");

                    holder.tv_sender.setTextColor((getResources().getColor(R.color.sender_read)));
                    holder.tv_subject.setTextColor(getResources().getColor(R.color.subject_read));
                    holder.tv_date.setTextColor(getResources().getColor(R.color.date_read));
                    holder.tv_sender.setTypeface(faceMedium);
                    holder.tv_subject.setTypeface(faceMedium);
                    holder.tv_date.setTypeface(faceMedium);

                } else {
                    Typeface faceMedium = Typeface.createFromAsset(context.getAssets(),
                            "fonts/helvetica-neue-medium.ttf");

                    holder.tv_sender.setTextColor(getResources().getColor(R.color.black));
                    holder.tv_subject.setTextColor(getResources().getColor(R.color.header_bg));
                    holder.tv_date.setTextColor(getResources().getColor(R.color.grey));
                    holder.tv_sender.setTypeface(faceMedium,Typeface.BOLD);
                    holder.tv_subject.setTypeface(faceMedium,Typeface.BOLD);
                    holder.tv_date.setTypeface(faceMedium,Typeface.BOLD);


                }

                holder.tv_subject.setText(messageItemsForSearch.get(pos).getTitle());
                holder.tv_content.setText(messageItemsForSearch.get(pos).getTextDescription());
                holder.tv_date.setText(messageItemsForSearch.get(pos).getCreated_at());
                holder.tv_sender.setText(messageItemsForSearch.get(pos).getFrom());
                holder.ll_top.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (messageItemsForSearch.get(pos).getMessage_read() == 1) {
                            Intent intent = new Intent(getActivity(), MessageDetails.class);
                            intent.putExtra("messagedetails", messageItemsForSearch.get(pos));
                            intent.putExtra("comeFrom", "messageList");
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            getMessageStatusUpdated(messageItemsForSearch.get(pos), pos);
                        }
                    }

                });
            }

            else {
                ((ProgressViewHolder) holder1).progressBar.setIndeterminate(true);
            }


        }


        @Override
        public int getItemCount() {
            return messageItemsForSearch.size();
        }

        private  class ProgressViewHolder extends RecyclerView.ViewHolder {
            public ProgressBar progressBar;

            public ProgressViewHolder(View v) {
                super(v);
                progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
            }
        }
        private class ItemViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_sender, tv_content, tv_subject, tv_date;
            LinearLayout ll_top;
            private WebView webView;

            public ItemViewHolder(View view) {
                super(view);
                tv_content = (TextView) view.findViewById(R.id.tv_content);
                tv_subject = (TextView) view.findViewById(R.id.tv_subject);
                tv_sender = (TextView) view.findViewById(R.id.tv_sender);
                tv_date = (TextView) view.findViewById(R.id.tv_date);

                webView = (WebView) view.findViewById(R.id.webView);
                ll_top = (LinearLayout) view.findViewById(R.id.ll_top);
                Typeface faceMedium = Typeface.createFromAsset(context.getAssets(),
                        "fonts/helvetica-neue-medium.ttf");

                tv_sender.setTypeface(faceMedium);
                tv_subject.setTypeface(faceMedium);
                tv_date.setTypeface(faceMedium);
                tv_content.setTypeface(faceMedium);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }


        }


        // Add a list of items -- change to type used
        public void addAll(ArrayList<MessageItems> messageItems) {

            messageItemsForSearch=messageItems;
            notifyDataSetChanged();
        }
        // Clean all elements of the recycler
        public void clear() {
            messageItemsForSearch.clear();
            notifyDataSetChanged();
        }


        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    System.out.println(messageItemsForSearch);
                    System.out.println(results.values);
                    messageItemsForSearch = (ArrayList<MessageItems>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
//                }
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<MessageItems> FilteredArrList = new ArrayList<MessageItems>();
                    if (messageItems == null) {
                        messageItems = new ArrayList<MessageItems>(messageItemsForSearch); // saves the original data in recordsList
                    }
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = messageItems.size();
                        results.values = messageItems;
                    } else {

                        constraint = constraint.toString().toLowerCase();
                        constraint = constraint.toString().replace("\n", "");
                        for (int i = 0; i < messageItems.size(); i++) {
                            if (messageItems.get(i).getFrom() != null) {
//                                dataString = messageItems.get(i).getFrom().trim() + messageItems.get(i).getTextDescription().trim() + messageItems.get(i).getTitle().trim();
                                dataString = messageItems.get(i).getFrom().trim() + messageItems.get(i).getTitle().trim();
                                if (dataString.toLowerCase().contains(constraint.toString().trim().toLowerCase())) {

                                    FilteredArrList.add(new MessageItems(
                                            messageItems.get(i).getFrom(),
                                            messageItems.get(i).getDescription(),
                                            messageItems.get(i).getTitle(),
                                            messageItems.get(i).getMessage_read(),
                                            messageItems.get(i).getId(),messageItems.get(i
                                    ).getThumbnail_url(),messageItems.get(i).getAttachment_url(),messageItems.get(i).getCreated_at(),messageItems.get(i).getType(),messageItems.get(i).getTextDescription(),messageItems.get(i).getTo()));

                                }
                            } else {
//                                dataString = messageItems.get(i).getFrom().trim() + messageItems.get(i).getTextDescription().trim() + messageItems.get(i).getTitle().trim();
                                dataString = messageItems.get(i).getFrom().trim() + messageItems.get(i).getTitle().trim();
                                if (dataString.toLowerCase().contains(constraint.toString().trim().toLowerCase())) {
                                    FilteredArrList.add(new MessageItems(
                                            messageItems.get(i).getFrom(),
                                            messageItems.get(i).getDescription(),
                                            messageItems.get(i).getTitle(),
                                            messageItems.get(i).getMessage_read(),
                                            messageItems.get(i).getId(),messageItems.get(i
                                    ).getThumbnail_url(),messageItems.get(i).getAttachment_url(),messageItems.get(i).getCreated_at(),messageItems.get(i).getType(),messageItems.get(i).getTextDescription(),messageItems.get(i).getTo()));
                                }

                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
            return filter;

        }

    }


}

