package com.mdff.app.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mdff.app.R;
import com.mdff.app.controller.AlphaApplication;
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

public class  Login extends AppCompatActivity implements View.OnClickListener, AlertMessage.NoticeDialogListenerWithoutView
{
    private EditText unameEdit,pwdEdit;private Button loginBtn,signupBtn;private TextView forgetPwdTx,alertTx,alertTx1;private LinearLayout alertLayout;
    private Activity activity;private ProgressDialog progressDialog;private AppUtil appUtil;private AlertMessage alertMessage;private ScrollView scrollView;
    private boolean apiError=false;private int connectStatus;boolean isPayment=false;
    //comments test
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity=Login.this;
        appUtil=new AppUtil(activity);
        connectStatus = ConnectivityReceiver.isConnected(activity);
        linkUIElements();
        pwdEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doLogin();
                    handled = true;
                }
                return handled;
            }
        });
        unameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
//                datalv.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                if (s.length()==0) {
                    unameEdit.setBackgroundResource(R.drawable.edittext_red_rounded_corner);
                    unameEdit.setTextColor(getResources().getColor(R.color.header_bg));
                }
                else if(s.length()==1)
                {
                    alertLayout.setVisibility(View.INVISIBLE);
                    unameEdit.setBackgroundResource(R.drawable.edittext_rounded_corner);
                    unameEdit.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        pwdEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
//                datalv.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                if (s.length()==0) {
                    pwdEdit.setBackgroundResource(R.drawable.edittext_red_rounded_corner);
                    pwdEdit.setTextColor(getResources().getColor(R.color.header_bg));
                }
                else if(s.length()==1)
                {
                    alertLayout.setVisibility(View.INVISIBLE);
                    pwdEdit.setBackgroundResource(R.drawable.edittext_rounded_corner);
                    pwdEdit.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
    }

    private void linkUIElements()
    {
        unameEdit=(EditText)findViewById(R.id.unameEdit);
        pwdEdit=(EditText)findViewById(R.id.pwdEdit);
        loginBtn=(Button) findViewById(R.id.loginBtn);
        signupBtn=(Button) findViewById(R.id.signupBtn);
        forgetPwdTx=(TextView)findViewById(R.id.forgetPwdTx);
        alertTx=(TextView)findViewById(R.id.alertTx);
        alertTx1=(TextView)findViewById(R.id.alertTx1);
        alertLayout=(LinearLayout) findViewById(R.id.alertLayout);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        clickListener();
    }

    private void clickListener()
    {   signupBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        alertTx1.setOnClickListener(this);
//        Open
        forgetPwdTx.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.signupBtn:
                Intent intent = new Intent(this, SignupMain.class);
                startActivity(intent);
                finish();

                break;
            case R.id.loginBtn:
                doLogin();
                break;
            case R.id.forgetPwdTx:
                appUtil.hideSoftKeyboard(activity);
                if(unameEdit.getText().toString().isEmpty())
                {   alertTx.setText(getString(R.string.input_email));
                    alertLayout.setVisibility(View.VISIBLE);
                    alertTx1.setVisibility(View.GONE);
                    unameEdit.requestFocus();
                    unameEdit.setBackgroundResource(R.drawable.edittext_red_rounded_corner);
                    scrollView.scrollTo(0, scrollView.getTop());

                }

                else if (!appUtil.isEmailCorrect(unameEdit.getText().toString()))
                {
                    alertTx.setText(getString(R.string.valid_email));
                    alertLayout.setVisibility(View.VISIBLE);
                    alertTx1.setVisibility(View.GONE);
                    unameEdit.requestFocus();
                    unameEdit.setBackgroundResource(R.drawable.edittext_red_rounded_corner);
                    scrollView.scrollTo(0, scrollView.getTop());
                }
                else
                {
                    if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                        try {
                            resetPassword();
                        }
                        catch (Exception e)
                        { }

                    } else {
                        alertMessage = AlertMessage.newInstance(
                                getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                        alertMessage.show(getFragmentManager(), "");
                    }
                }
                break;
            default:
                break;
        }

    }

    private void doLogin()
    {

        appUtil.hideSoftKeyboard(activity);
        if(unameEdit.getText().toString().isEmpty())
        {   alertTx.setText(getString(R.string.input_email));
            alertLayout.setVisibility(View.VISIBLE);
            alertTx1.setVisibility(View.GONE);
            unameEdit.requestFocus();
            unameEdit.setBackgroundResource(R.drawable.edittext_red_rounded_corner);
            scrollView.scrollTo(0, scrollView.getTop());

        }

        else if (!appUtil.isEmailCorrect(unameEdit.getText().toString()))
        {
            alertTx.setText(getString(R.string.valid_email));
            alertLayout.setVisibility(View.VISIBLE);
            alertTx1.setVisibility(View.GONE);
            unameEdit.requestFocus();
            unameEdit.setBackgroundResource(R.drawable.edittext_red_rounded_corner);
            scrollView.scrollTo(0, scrollView.getTop());
        }
        else  if(pwdEdit.getText().toString().isEmpty())
        {   alertTx.setText(getString(R.string.input_pwd));
            alertLayout.setVisibility(View.VISIBLE);
            alertTx1.setVisibility(View.GONE);
            pwdEdit.requestFocus();
            pwdEdit.setBackgroundResource(R.drawable.edittext_red_rounded_corner);
            scrollView.scrollTo(0, scrollView.getTop());
        }

        else if(pwdEdit.getText().toString().length()<6)
        {
            alertTx.setText(getString(R.string.min_pwd));
            alertLayout.setVisibility(View.VISIBLE);
            alertTx1.setVisibility(View.GONE);
            pwdEdit.requestFocus();
            pwdEdit.setBackgroundResource(R.drawable.edittext_red_rounded_corner);
            scrollView.scrollTo(0, scrollView.getTop());
        }
        else{
            try {
                userLogin();
            }
            catch (Exception e)
            {

            }
        }


    }

    private void resetPassword() {

        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Processing");
                progressDialog.show();
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("email", unameEdit.getText().toString());
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                jsonParam.accumulate("app_id",Constant.APP_ID);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.RESET_PWD, jsonParam,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("Alpha response", response.toString());
                                    JSONObject jsonParam = new JSONObject(response.toString());
                                    String status = jsonParam.getString("status");
                                    String message = jsonParam.getString("message");
                                    String code = jsonParam.getString("code");
                                    if (code.equals("10")) {
                                        JSONArray respObj= jsonParam.getJSONArray("result");
                                        Log.i("Alpha respObj", respObj.toString());
                                        progressDialog.dismiss();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),getString(R.string.alert));
                                        alertMessage.show(getFragmentManager(), "");
                                    }

                                    else {
                                        progressDialog.dismiss();
//                                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        alertMessage.show(getFragmentManager(), "");
                                    }
                                } catch (Throwable e) {
                                    progressDialog.dismiss();
                                    Log.i("Excep", "error----" + e.getMessage());
                                    e.printStackTrace();
//                                    displayAlert();
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
//                            displayAlert();
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
//                displayAlert();
            }

        } else {

            alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
            alertMessage.show(getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }


    private void userLogin() {

        if (ConnectivityReceiver.isConnected(activity) != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Processing");
                progressDialog.show();
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("password", pwdEdit.getText().toString());
                jsonParam.accumulate("email", unameEdit.getText().toString());
                jsonParam.accumulate("app_id",Constant.APP_ID);
                jsonParam.accumulate("device_token", appUtil.getPrefrence("deviceToken"));

                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.LOGIN, jsonParam,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("Alpha response", response.toString());
                                    JSONObject jsonParam = new JSONObject(response.toString());
                                    String status = jsonParam.getString("status");
                                    String message = jsonParam.getString("message");
                                    String code = jsonParam.getString("code");
//                                    JSONArray respToken= jsonParam.getJSONArray("token");

                                    if (code.equals("10")) {
                                        JSONArray respObj= jsonParam.getJSONArray("result");
//                                        String full_name = jsonParam.getString("full_name");
//                                        String user_email = jsonParam.getString("user_email");
                                        Log.i("Alpha respObj", respObj.toString());
                                        appUtil.setPrefrence("isLogin","yes");
                                        for(int i=0;i<respObj.length();i++) {
                                            appUtil.setPrefrence("accessToken",respObj.getJSONObject(i).getString("accessToken"));
                                            appUtil.setPrefrence("password_status",respObj.getJSONObject(i).getString("password_status"));
                                            appUtil.setPrefrence("customer_id",respObj.getJSONObject(i).getString("customer_id"));
                                            appUtil.setPrefrence("subscription_id",respObj.getJSONObject(i).getString("subscription_id"));
                                            appUtil.setPrefrence("profile_pic",respObj.getJSONObject(i).getString("profile_pic"));
                                            appUtil.setPrefrence("uname",unameEdit.getText().toString());
                                            appUtil.setPrefrence("pwd",pwdEdit.getText().toString());
                                            appUtil.setPrefrence("is_follower",respObj.getJSONObject(i).getString("is_follower"));

                                        }
                                        progressDialog.dismiss();
                                        if(appUtil.getPrefrence("password_status").equals("pending"))
                                        {
                                            Intent intent = new Intent(activity, UpdatePassword.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else if(appUtil.getPrefrence("password_status").equals("completed")) {
                                            Intent intent = new Intent(activity, MainActivity.class);
                                            intent.putExtra("comeFrom","login");
                                            startActivity(intent);
                                            finish();
                                        }


                                    }
                                    else if (code.equals("2")) {
                                        SpannableString ss = new SpannableString(getString(R.string.error_login));
                                        ClickableSpan clickableSpan = new ClickableSpan() {
                                            @Override
                                            public void onClick(View textView) {
                                                startActivity(new Intent(activity, SignupMain.class));
                                                finish();
                                            }
                                            @Override
                                            public void updateDrawState(TextPaint ds) {
                                                super.updateDrawState(ds);
                                                ds.setUnderlineText(true);
                                            }
                                        };
                                        ss.setSpan(clickableSpan, 60, 68, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        alertTx.setText(ss);
                                        alertTx.setMovementMethod(LinkMovementMethod.getInstance());
                                        alertTx.setHighlightColor(Color.TRANSPARENT);
                                        appUtil.setPrefrence("isLogin","no");
//                                        alertTx1.setVisibility(View.VISIBLE);
                                        //                alertTx.setText(html);
                                        alertLayout.setVisibility(View.VISIBLE);
                                        unameEdit.setBackgroundResource(R.drawable.edittext_red_rounded_corner);
                                        unameEdit.setTextColor(getResources().getColor(R.color.red));
                                        progressDialog.dismiss();
                                    }

                                    else if (code.equals("0")){
                                        progressDialog.dismiss();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        alertMessage.show(getFragmentManager(), "");

                                    }
                                    else if (code.equals("3")){
                                        progressDialog.dismiss();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        alertMessage.show(getFragmentManager(), "");

                                    }
                                    else if (code.equals("4")){
                                        isPayment=true;
                                        appUtil.setPrefrence("uname",unameEdit.getText().toString());
                                        appUtil.setPrefrence("pwd",pwdEdit.getText().toString());
                                        progressDialog.dismiss();
                                        /*Intent i=new Intent(activity, PaymentActivity.class);
                                        activity.startActivity(i);
                                        activity.finish();*/
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        alertMessage.show(getFragmentManager(), "");

                                    }


                                    else {
                                        progressDialog.dismiss();
                                        appUtil.setPrefrence("isLogin","no");
//                                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        alertMessage.show(getFragmentManager(), "");
                                    }
                                } catch (Throwable e) {
                                    progressDialog.dismiss();
                                    Log.i("Excep", "error----" + e.getMessage());
                                    e.printStackTrace();
//                                    displayAlert();
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
//                            displayAlert();
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
//                displayAlert();
            }

        } else {

            alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
            alertMessage.show(getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {

        if(isPayment)
        {
            isPayment=false;
            dialog.dismiss();
            Intent i=new Intent(activity, PaymentActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra("type","updatesubscription");
            activity.startActivity(i);
            activity.finish();
        }
        else {
            dialog.dismiss();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("Token",">>"+token);
                        appUtil.setPrefrence("deviceToken",token);

                    }
                });
    }
}
