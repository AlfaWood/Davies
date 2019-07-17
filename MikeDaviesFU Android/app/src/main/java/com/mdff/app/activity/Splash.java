package com.mdff.app.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mdff.app.R;
import com.mdff.app.controller.AlphaApplication;

import com.mdff.app.model.MessageItems;
import com.mdff.app.utility.AlertMessage;
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

public class Splash extends AppCompatActivity implements AlertMessage.NoticeDialogListenerWithoutView {
    private AppUtil appUtil;
    ProgressDialog progressDialog;
    AlertMessage alertMessage;
    Activity activity;
    Intent i;
    String comeFrom;
    MessageItems messageItems;
    boolean isNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = Splash.this;
        appUtil = new AppUtil(activity);
        String s = appUtil.getPrefrence("deviceToken");
        System.out.print(s);
        int connectStatus = ConnectivityReceiver.isConnected(activity);
        System.out.println("-----------Testdata-----//" + getIntent().getExtras());
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            getTokenizationkey();
        } else {
            alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
            alertMessage.show(getFragmentManager(), "");

        }

    }

    private void getTokenizationkey() {
        int connectStatus = ConnectivityReceiver.isConnected(activity);
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("app_id", Constant.APP_ID);
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.APP_KEY, jsonParam,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("Alpha response", response.toString());
                                    JSONObject jsonParam = new JSONObject(response.toString());
                                    String code = jsonParam.getString("code");
                                    String status = jsonParam.getString("status");
                                    String message = jsonParam.getString("message");

                                    if (code.equals("10")) {
                                        try {
                                            JSONArray respObj = jsonParam.getJSONArray("result");
                                            if (respObj != null) {
                                                appUtil.setPrefrence("tokenKey", respObj.getJSONObject(0).getString("key"));
                                                if (getIntent().getExtras() != null) {
                                                    System.out.println("-------------value----------/"+getIntent().getExtras());
                                                    ArrayList<String> ar1=new ArrayList<>();
                                                    for (String key : getIntent().getExtras().keySet()) {
                                                        ar1.add(key);
                                                    }
                                                    if(ar1.contains(("asset"))) {
                                                        try {
                                                            messageItems = new MessageItems();
                                                            JSONObject jsonObject1 = new JSONObject(getIntent().getExtras().getString("asset"));
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
                                                                messageItems.setDescription(appUtil.modifyString(jsonObject1.getString("description")));
                                                                messageItems.setMessage_read(jsonObject1.getInt("message_read"));
                                                                messageItems.setTextDescription(jsonObject1.getString("description_text"));

                                                                Intent intent = new Intent(getApplicationContext(), MessageDetails.class);
                                                                intent.putExtra("messagedetails", (Serializable) messageItems);
                                                                intent.putExtra("comeFrom", "notify");
                                                                appUtil.setPrefrence("comeFrom", "notify");
                                                                appUtil.setPrefrence("unread_count", jsonObject1.getString("unreadMessages"));
                                                                Intent in = new Intent();
                                                                in.setAction("message.came");
                                                                sendBroadcast(in);
                                                                startActivity(intent);
                                                                finish();
                                                            }
//                                                        setListAdapter();
                                                        } catch (Exception e) {
                                                            System.out.println("----------error-----" + e);
//                                                            Toast.makeText(Splash.this, "on extra 1", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                    else if ((appUtil.getPrefrence("isLogin").equals("yes") && appUtil.getPrefrence("password_status").equals("completed"))) {
//                                                        Toast.makeText(Splash.this, "on extra 2", Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(activity, MainActivity.class);
                                                        intent.putExtra("comeFrom", "splash");
                                                        startActivity(intent);
                                                        finish();

                                                        }
                                                        else if (appUtil.getPrefrence("isLogin").equals("yes") && appUtil.getPrefrence("password_status").equals("pending")) {
                                                        Intent intent = new Intent(activity, UpdatePassword.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    else {
                                                        Intent intent = new Intent(activity, Login.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                                 else if ((appUtil.getPrefrence("isLogin").equals("yes") && appUtil.getPrefrence("password_status").equals("completed"))) {
//                                                    Toast.makeText(Splash.this, "on extra 2", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(activity, MainActivity.class);
                                                    intent.putExtra("comeFrom", "splash");
                                                    startActivity(intent);
                                                    finish();
                                                } else if (appUtil.getPrefrence("isLogin").equals("yes") && appUtil.getPrefrence("password_status").equals("pending")) {
                                                    Intent intent = new Intent(activity, UpdatePassword.class);
                                                    startActivity(intent);
                                                    finish();
                                                }


                                                else {
                                                    Intent intent = new Intent(activity, Login.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            alertMessage = AlertMessage.newInstance(
                                                    getString(R.string.something), getString(R.string.ok), getString(R.string.error));
                                            alertMessage.show(getFragmentManager(), "");
                                        }
//                                        Toast.makeText(activity, "token=" + token, Toast.LENGTH_LONG).show();

                                    } else {
//                                        progressDialog.dismiss();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok), status);
                                        alertMessage.show(getFragmentManager(), "");
//                                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                    }

                                } catch (Throwable e) {
//                                    progressDialog.dismiss();
                                    alertMessage = AlertMessage.newInstance(
                                            getString(R.string.something), getString(R.string.ok), getString(R.string.error));
                                    alertMessage.show(getFragmentManager(), "");
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
                                alertMessage = AlertMessage.newInstance(
                                        getString(R.string.timeout), getString(R.string.ok), getString(R.string.error));
                                alertMessage.show(getFragmentManager(), "");
                                Toast.makeText(activity, "Time out error", Toast.LENGTH_LONG).show();
                            } else {
                                alertMessage = AlertMessage.newInstance(
                                        getString(R.string.something), getString(R.string.ok), getString(R.string.error));
                                alertMessage.show(getFragmentManager(), "");
                            }
                        }
                    }
                });

                RetryPolicy policy = new DefaultRetryPolicy
                        (50000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);

                AlphaApplication.getInstance().addToRequestQueue(jsonObjectRequest);

            } catch (Exception e) {
                e.printStackTrace();
                alertMessage = AlertMessage.newInstance(
                        getString(R.string.something), getString(R.string.ok), getString(R.string.error));
                alertMessage.show(getFragmentManager(), "");
//                progressDialog.dismiss();
            }

        } else {

            alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
            alertMessage.show(getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {
        dialog.dismiss();
        System.exit(0);

    }
}
