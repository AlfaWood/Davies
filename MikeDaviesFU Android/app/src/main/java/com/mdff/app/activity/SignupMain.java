package com.mdff.app.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mdff.app.R;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.fragment.AccountCredential;
import com.mdff.app.model.User;
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

public class SignupMain extends AppCompatActivity implements AlertMessage.NoticeDialogListenerWithoutView{
    public AppUtil appUtil;private AccountCredential accountCredential;Activity activity;public User user;
    public static final int REQUEST_CODE=101;ProgressDialog progressDialog;boolean displayLogin=false;AlertMessage alertMessage;
    private LinearLayout backLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_main);
        activity=SignupMain.this;
        appUtil=new AppUtil(activity);
        user=new User();
        linkUIElements();
    }

    private void linkUIElements()
    {
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        accountCredential = new AccountCredential();
        Bundle bundle = new Bundle();
        accountCredential.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, accountCredential);
        transaction.commit();
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {
        if(displayLogin) {
            displayLogin=false;
            dialog.dismiss();
            Intent intent = new Intent(activity, MainActivity.class);
            intent.putExtra("comeFrom","signup");
            startActivity(intent);
            finish();
        }
        else{
            displayLogin=false;
            dialog.dismiss();
        }
    }



    public void userSignUp() {
        int connectStatus = ConnectivityReceiver.isConnected(activity);
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Processing");
                progressDialog.show();
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("first_name", user.getFname());
                jsonParam.accumulate("last_name", user.getLname());
                jsonParam.accumulate("name", user.getUname());
                jsonParam.accumulate("email", user.getEmail());
                jsonParam.accumulate("password", user.getPassword());
                jsonParam.accumulate("payment_nonce", user.getPaymentNonce());
                jsonParam.accumulate("app_id", Constant.APP_ID);
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.SIGN_UP, jsonParam,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("Alpha response", response.toString());
                                    JSONObject jsonParam = new JSONObject(response.toString());
                                    String status = jsonParam.getString("status");
                                    String code = jsonParam.getString("code");
                                    String message = jsonParam.getString("message");
//                                    JSONObject respObj= jsonParam.getJSONObject("data");


                                    if (code.equals("10")) {
                                        JSONArray respObj= jsonParam.getJSONArray("result");

                                        for(int i=0;i<respObj.length();i++) {
                                            appUtil.setPrefrence("accessToken",respObj.getJSONObject(i).getString("accessToken"));
                                            appUtil.setPrefrence("token",respObj.getJSONObject(i).getString("token"));
                                            appUtil.setPrefrence("name",respObj.getJSONObject(i).getString("name"));
                                            appUtil.setPrefrence("email",respObj.getJSONObject(i).getString("email"));
                                            appUtil.setPrefrence("password_status",respObj.getJSONObject(i).getString("password_status"));
                                            appUtil.setPrefrence("user_id",respObj.getJSONObject(i).getString("user_id"));
                                            appUtil.setPrefrence("created_at",respObj.getJSONObject(i).getString("created_at"));
                                            appUtil.setPrefrence("isLogin","yes");
                                        }


                                        progressDialog.dismiss();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        displayLogin=true;
                                        alertMessage.show(getFragmentManager(), "");
//                                        Toast.makeText(activity, "token=" + token, Toast.LENGTH_LONG).show();


                                    }
                                    else  {
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        displayLogin=false;
                                        alertMessage.show(getFragmentManager(), "");
                                        progressDialog.dismiss();
                                    }

                                } catch (Throwable e) {
                                    progressDialog.dismiss();
                                    Log.i("Excep", "error----" + e.getMessage());
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley error resp", "error----" + error.getMessage());
                        progressDialog.dismiss();

                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {

                                Toast.makeText(activity, "Time out error", Toast.LENGTH_LONG).show();
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
                progressDialog.dismiss();
            }

        } else {

            alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
            alertMessage.show(getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            Intent intent = new Intent(activity, Login.class);
            startActivity(intent);
            finish();
        }
    }

}
