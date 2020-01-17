package com.mdff.app.utility;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.mdff.app.R;
import com.mdff.app.activity.PaymentActivity;
import com.mdff.app.app_interface.VolleyCallback;
import com.mdff.app.controller.AlphaApplication;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Swati.Gupta on 5/9/2018.
 */

public class ApiController implements AlertMessage.NoticeDialogListenerWithoutView{

    private Activity activity;private  ProgressDialog   progressDialog;
    private AppUtil appUtil;private boolean loginStatus=false;private AlertMessage alertMessage;
    private String message="";
    private String code="";private String status="";private boolean isPayment=false;
    public ApiController(Activity activity)
    {
        this.activity=activity;
        appUtil=new AppUtil(this.activity);

    }

    public void userLogin(final VolleyCallback volleyCallback ) {

        if (ConnectivityReceiver.isConnected(activity) != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("password", appUtil.getPrefrence("pwd"));
                jsonParam.accumulate("email", appUtil.getPrefrence("uname"));
                jsonParam.accumulate("app_id", Constant.APP_ID);
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.LOGIN, jsonParam,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("Alpha response", response.toString());
                                    JSONObject jsonParam = new JSONObject(response.toString());
                                     status = jsonParam.getString("status");
                                     message = jsonParam.getString("message");
                                     code = jsonParam.getString("code");
                                    if (code.equals("10")) {
                                        JSONArray respObj= jsonParam.getJSONArray("result");
                                        Log.i("Alpha respObj", respObj.toString());
                                        appUtil.setPrefrence("isLogin","yes");
                                        for(int i=0;i<respObj.length();i++) {
                                            appUtil.setPrefrence("accessToken",respObj.getJSONObject(i).getString("accessToken"));
                                            appUtil.setPrefrence("password_status",respObj.getJSONObject(i).getString("password_status"));
                                        }
                                        volleyCallback.onSuccessResponse(code,message,status);
                                    }
                                    else if(code.equals("4"))
                                    {

                                       /* appUtil.setPrefrence("isLogin","no");
                                        appUtil.setPrefrence("isPayment","no");
                                        alertMessage = AlertMessage.newInstance(
                                                message, activity.getString(R.string.ok),status);
                                        alertMessage.show(activity.getFragmentManager(), "");*/

                                        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(activity);
                                        builder1.setTitle(status);
                                        builder1.setMessage(message)
                                                .setCancelable(true)
                                                .setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //do things
                                                        dialog.dismiss();
                                                         appUtil.setPrefrence("isLogin","no");
                                                         appUtil.setPrefrence("isPayment","no");
                                                        Intent i=new Intent(activity, PaymentActivity.class);
                                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        i.putExtra("type", "updatesubscription");
                                                        activity.startActivity(i);
                                                        activity.finish();
                                                    }
                                                });
                                        androidx.appcompat.app.AlertDialog alert1 = builder1.create();
                                        alert1.show();

                                    }

                                    else {

                                        appUtil.setPrefrence("isLogin","no");
                                        volleyCallback.onSuccessResponse(code,message,status);
//                                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();

                                    }
                                } catch (Throwable e) {
                                    Log.i("Excep", "error----" + e.getMessage());
                                    e.printStackTrace();
                                    appUtil.setPrefrence("isLogin","no");
                                    try {
                                        volleyCallback.onSuccessResponse(code, message, status);
                                    }
                                    catch (Exception e4)
                                    {}

//                                    displayAlert();
                                }


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley error resp", "error----" + error.getMessage());
                        appUtil.setPrefrence("isLogin","no");
                        volleyCallback.onSuccessResponse(code,message,status);
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

//                displayAlert();
            }

        } else {
//            appUtil.setPrefrence("isLogin","no");
//            volleyCallback.onSuccessResponse(code,message);
            alertMessage = AlertMessage.newInstance(
                    activity.getString(R.string.noInternet), activity.getString(R.string.ok),activity.getString(R.string.alert));
            alertMessage.show(activity.getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }
    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {


        if(appUtil.getPrefrence("isPayment").equals("no"))
        {
            dialog.dismiss();
            Intent i=new Intent(activity, PaymentActivity.class);
            i.putExtra("type", "updatesubscription");
            activity.startActivity(i);
            activity.finish();
        }
        else {
            dialog.dismiss();
        }
//       dialog.dismiss();
    }

    public void getMessageCount(final VolleyCallback volleyCallback) {
        if (ConnectivityReceiver.isConnected(activity) != NetworkUtils.TYPE_NOT_CONNECTED) {

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
                                                int badge_count = jsonObject.getInt("unreadMessages");
                                                appUtil.setPrefrence("unread_count",String.valueOf(badge_count));
                                                Intent intent = new Intent();
                                                intent.setAction("message.came");
                                                activity.sendBroadcast(intent);
                                            }
                                        } else {
//                                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                        }
                                    } else {
//                                        pDialog.dismiss();
//                                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
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
                        }
                        else{
                            if(error.networkResponse.statusCode==401)
                            {
                                if (ConnectivityReceiver.isConnected(activity) != NetworkUtils.TYPE_NOT_CONNECTED) {

//                                    ApiController loginController=new ApiController(MainActivity.this);
                                    userLogin(volleyCallback);
                                }
                                else{
                                    alertMessage = AlertMessage.newInstance(
                                            activity.getString(R.string.noInternet), activity.getString(R.string.ok),activity.getString(R.string.alert));
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
                e.printStackTrace();
//                pDialog.dismiss();

            }

        } else

        {
            AlertMessage alertMessage = AlertMessage.newInstance(
                    activity.getString(R.string.noInternet),  activity.getString(R.string.ok),  activity.getString(R.string.alert));
            alertMessage.show( activity.getFragmentManager(), "");
            //  Toast.makeText(getActivity(), "No internet", Toast.LENGTH_LONG).show();
        }
    }




}
