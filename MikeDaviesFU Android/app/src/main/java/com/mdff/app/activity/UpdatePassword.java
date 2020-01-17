package com.mdff.app.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.mdff.app.R;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.utility.ApiController;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdatePassword extends AppCompatActivity implements AlertMessage.NoticeDialogListenerWithoutView,VolleyCallback {
    private EditText et_old_password,et_new_password,et_confirmpassword;private AlertMessage alertMessage;
    private Button updateBtn;private Activity activity;private int connectStatus;private AppUtil appUtil;
    private ScrollView scrollView; private LinearLayout mainLayout;private ProgressDialog progressDialog;
    private boolean isUpdate=false;private boolean isFinish=false;LinearLayout backLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        activity=UpdatePassword.this;
        appUtil=new AppUtil(activity);
        connectStatus = ConnectivityReceiver.isConnected(activity);
        progressDialog = new ProgressDialog(activity);
        linkUIElements();
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doUpdatePwd();
            }
        });
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void linkUIElements()
    {        backLayout = (LinearLayout) findViewById(R.id.backLayout);

        et_old_password=(EditText)findViewById(R.id.et_old_password);
        et_new_password=(EditText)findViewById(R.id.et_new_password);
        et_confirmpassword=(EditText)findViewById(R.id.et_confirmpassword);
        updateBtn=(Button) findViewById(R.id.updateBtn);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        mainLayout=(LinearLayout)findViewById(R.id.mainLayout);


    }

    private void doUpdatePwd()
    {
        if(et_old_password.getText().toString().isEmpty()) {
            displayAlert(mainLayout, et_old_password, getString(R.string.input_old_pwd), getString(R.string.alert));

        }
        else if(et_old_password.getText().toString().length()<6) {
            displayAlert(mainLayout, et_old_password, getString(R.string.pwd_valid), getString(R.string.alert));

        }

        else  if(et_new_password.getText().toString().isEmpty())
        {
            displayAlert(et_old_password,et_new_password,getString(R.string.input_new_pwd),getString(R.string.alert));

        }
        else  if(et_new_password.getText().toString().length()<6)
        {
            displayAlert(et_old_password,et_new_password,getString(R.string.pwd_valid),getString(R.string.alert));

        }
        else  if(et_confirmpassword.getText().toString().isEmpty())
        {
            displayAlert(et_new_password,et_confirmpassword,getString(R.string.input_cnf_pwd),getString(R.string.alert));

        }
        else if(!et_new_password.getText().toString().trim().equals(et_confirmpassword.getText().toString().trim()))
        {
            displayAlert(et_new_password,et_confirmpassword,getString(R.string.pwd_not_match),getString(R.string.alert));
        }
        else {

            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                try {
                    updatePassword();
                }
                catch (Exception e)
                { }

            } else {
                alertMessage = AlertMessage.newInstance(
                        getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                alertMessage.show(getFragmentManager(), "");
            }

        }

    }



    private void updatePassword() {
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {

                progressDialog.setMessage("Processing");
                progressDialog.show();
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("old", et_old_password.getText().toString());
                jsonParam.accumulate("new", et_new_password.getText().toString());
                jsonParam.accumulate("confirm", et_confirmpassword.getText().toString());
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.UPDATE_PWD, jsonParam,
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
                                        appUtil.setPrefrence("isLogin","no");
                                        appUtil.setPrefrence("password_status","complete");
                                        isUpdate=true;
                                        progressDialog.dismiss();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),getString(R.string.alert));
                                        alertMessage.show(getFragmentManager(), "");
                                    }

                                    else {
                                        isUpdate=false;
                                        progressDialog.dismiss();
//                                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        alertMessage.show(getFragmentManager(), "");
                                    }
                                } catch (Throwable e) {
                                    isUpdate=false;
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
                        isUpdate=false;

                        if (error.networkResponse == null) {
                            progressDialog.dismiss();
                            if (error.getClass().equals(TimeoutError.class)) {
                                Toast.makeText(activity, "Time out error", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            if(error.networkResponse.statusCode==401)
                            {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                    ApiController apiController =new ApiController(activity);
                                    apiController.userLogin(UpdatePassword.this);
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
                isUpdate=false;
                e.printStackTrace();
                progressDialog.dismiss();
//                displayAlert();
            }

        } else {
            isUpdate=false;
            alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
            alertMessage.show(getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {

        if(isUpdate)
        {

            dialog.dismiss();
            Intent intent = new Intent(activity, Login.class);
            startActivity(intent);
            finish();
        }
        else if(isFinish)
        {
            isFinish=false;
            dialog.dismiss();
            finish();

        }
        else{
            dialog.dismiss();
        }

    }


    void displayAlert(final View focus, EditText et, String msg,String t){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, focus.getTop());
            }
        });
        et.requestFocus();
        alertMessage = AlertMessage.newInstance(
                msg, getString(R.string.ok),t);
        alertMessage.show(getFragmentManager(), "");
    }

    @Override
    public void onSuccessResponse(String code, String message, String status) {
        if(code.equals("10")) {

            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                updatePassword();
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
