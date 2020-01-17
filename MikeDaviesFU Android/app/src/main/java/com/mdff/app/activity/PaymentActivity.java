package com.mdff.app.activity;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.mdff.app.app_interface.VolleyCallback;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.fragment.CreditCard;
import com.mdff.app.fragment.Plan;
import com.mdff.app.model.User;
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

import com.mdff.app.R;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements AlertMessage.NoticeDialogListenerWithoutView,VolleyCallback {
    private Context mContext;private Spinner planSpinner;
  public   int connectStatus;private AlertMessage alertMessage;private Activity activity;
    private LinearLayout backLayout;private User user;public ProgressDialog progressDialog;boolean displayLogin=false,displayCardList=false;
    public AppUtil appUtil;public String type;public String tokenId;private boolean isFinish=false;private CardInputWidget mCardInputWidget;
boolean loginScr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mContext=PaymentActivity.this;
        activity=PaymentActivity.this;
        connectStatus = ConnectivityReceiver.isConnected(activity);
        appUtil = new AppUtil(activity);
        type=getIntent().getStringExtra("type");
        linkUIElements();
    }

    private void linkUIElements()
    {
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        if(type.equalsIgnoreCase("signup")) {
            user = (User) getIntent().getSerializableExtra("user");
            Plan plan = new Plan();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, plan);
            transaction.commit();
        }
        else{

            CreditCard creditCard = new CreditCard();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, creditCard);
            transaction.commit();
        }


        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                // check if no view has focus:
                View v = getCurrentFocus();
                try {
                    if (v != null)
                        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                catch(Exception e)
                {}
              onBackPressed();
            }
        });



    }



    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {

        if(type.equalsIgnoreCase("signup")) {
        if(displayLogin) {
            displayLogin=false;
            dialog.dismiss();
            Intent intent = new Intent(activity, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("comeFrom","signup");
            startActivity(intent);
            finish();
        }
        else{
            displayLogin=false;
            dialog.dismiss();
        }
        }
        else if(type.equalsIgnoreCase("updatesubscription"))
        {
            if(loginScr) {
                loginScr=false;
                dialog.dismiss();
                Intent intent = new Intent(activity, Login.class);
//                intent.putExtra("comeFrom","signup");
                startActivity(intent);
                finish();
            }
            else{
                dialog.dismiss();
            }
        }

        else{


            if(displayCardList)
            {
                displayCardList=false;
                dialog.dismiss();
                UserProfile.comeFrom="Payment";
                finish();
            }
            else

                if(isFinish)
            {
                isFinish=false;
                dialog.dismiss();
                finish();

            }
            else{
                displayCardList=false;
                dialog.dismiss();
            }
        }
    }

    @Override
    public void onSuccessResponse(String code, String message, String status) {

        if(code.equals("10")) {
            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

              addNewCard(tokenId,mCardInputWidget);
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

    public void userSignUp(String token,String planId,String amount,final CardInputWidget mCardInputWidget) {
        this.mCardInputWidget=mCardInputWidget;

        int connectStatus = ConnectivityReceiver.isConnected(activity);
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {

                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("first_name", user.getFname());
                jsonParam.accumulate("last_name", user.getLname());
                jsonParam.accumulate("name", user.getUname());
                jsonParam.accumulate("email", user.getEmail());
                jsonParam.accumulate("password", user.getPassword());
                jsonParam.accumulate("payment_nonce", user.getPaymentNonce());
                jsonParam.accumulate("app_id", Constant.APP_ID);
                jsonParam.accumulate("token", token);
                jsonParam.accumulate("amount", amount);
                jsonParam.accumulate("plan_id", planId);
                jsonParam.accumulate("device_token", appUtil.getPrefrence("deviceToken"));
                jsonParam.accumulate("device_type", "andr");
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
                                            appUtil.setPrefrence("customer_id",respObj.getJSONObject(i).getString("customer_id"));
                                            appUtil.setPrefrence("subscription_id",respObj.getJSONObject(i).getString("subscription_id"));
                                            appUtil.setPrefrence("profile_pic",respObj.getJSONObject(i).getString("profile_pic"));
                                            appUtil.setPrefrence("uname",respObj.getJSONObject(i).getString("email"));
                                            appUtil.setPrefrence("pwd",user.getPassword());
                                            appUtil.setPrefrence("is_follower","1");
                                        }
                                        progressDialog.dismiss();
                                        clearData();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        displayLogin=true;
                                        alertMessage.show(getFragmentManager(), "");
//                                        Toast.makeText(activity, "token=" + token, Toast.LENGTH_LONG).show();


                                    }
                                    else  if (code.equals("17")) {
                                        progressDialog.dismiss();
                                        mCardInputWidget.clear();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        alertMessage.show(getFragmentManager(), "");
                                    }
                                    else  {
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        displayLogin=false;
                                        alertMessage.show(getFragmentManager(), "");
                                        progressDialog.dismiss();
                                    }
                                }
                                catch (Throwable e) {
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
    void clearData()
    {
        try{
           appUtil.setPrefrence("tempFname","");
           appUtil.setPrefrence("tempLname","");
           appUtil.setPrefrence("tempUname","");
           appUtil.setPrefrence("tempEmail","");
           appUtil.setPrefrence("tempPwd","");
       }
        catch (Exception e)
        {}
    }

    public void addNewCard(String token, final CardInputWidget mCardInputWidget) {

this.mCardInputWidget=mCardInputWidget;
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("token", token);
                jsonParam.accumulate("email", appUtil.getPrefrence("uname"));
                jsonParam.accumulate("type", type);
                jsonParam.accumulate("app_id",  Constant.APP_ID);
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.CARD_ADD, jsonParam,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("Alpha response", response.toString());
                                    JSONObject jsonParam = new JSONObject(response.toString());
                                    String status = jsonParam.getString("status");
                                    String code = jsonParam.getString("code");
                                    String message = jsonParam.getString("message");
                                    if(type.equals("updatesubscription"))
                                    {
                                        if (code.equals("10"))

                                        {

                                            progressDialog.dismiss();
                                        loginScr=true;
                                            alertMessage = AlertMessage.newInstance(
                                                    message, getString(R.string.ok), status);
                                            alertMessage.show(getFragmentManager(), "");

                                        }
                                        else{

                                            progressDialog.dismiss();
                                            alertMessage = AlertMessage.newInstance(
                                                    message, getString(R.string.ok), status);
                                            alertMessage.show(getFragmentManager(), "");

                                        }
                                    }

                                   else {
                                        if (code.equals("10")) {
                                            progressDialog.dismiss();
                                        displayCardList=true;
                                            alertMessage = AlertMessage.newInstance(
                                                    message, getString(R.string.ok), status);
                                            alertMessage.show(getFragmentManager(), "");
                                        } else if (code.equals("17")) {
                                            progressDialog.dismiss();
                                            mCardInputWidget.clear();
                                            alertMessage = AlertMessage.newInstance(
                                                    message, getString(R.string.ok), status);
                                            alertMessage.show(getFragmentManager(), "");
                                        } else {

                                            progressDialog.dismiss();
                                            alertMessage = AlertMessage.newInstance(
                                                    message, getString(R.string.ok), status);
                                            alertMessage.show(getFragmentManager(), "");
                                        }
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
                        else{
                            if(error.networkResponse.statusCode==401)
                            {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                    ApiController apiController =new ApiController(PaymentActivity.this);
                                    apiController.userLogin(PaymentActivity.this);
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
                progressDialog.dismiss();
                e.printStackTrace();
            }

        } else {

            alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
            alertMessage.show(getFragmentManager(), "");
        }
    }


}
