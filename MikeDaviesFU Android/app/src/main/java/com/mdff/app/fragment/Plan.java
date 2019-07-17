package com.mdff.app.fragment;


import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mdff.app.R;
import com.mdff.app.activity.PaymentActivity;
import com.mdff.app.adapter.CustomSubscriptionPlanListAdapter;
import com.mdff.app.app_interface.VolleyCallback;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.model.SubscriptionPlan;
import com.mdff.app.utility.AlertMessage;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Plan extends Fragment implements AlertMessage.NoticeDialogListenerWithoutView,VolleyCallback {
    private AlertMessage alertMessage;private PaymentActivity paymentActivity;
    private View view;ArrayList<SubscriptionPlan> subscriptionPlans;private RecyclerView rv_plan;
    CustomSubscriptionPlanListAdapter customSubscriptionPlanListAdapter;private ProgressDialog progressDialog;
    RelativeLayout planListLayout;
    public Plan() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view=view;

//        Bundle bundle = this.getArguments();
        paymentActivity=((PaymentActivity) getActivity());
//        paymentActivity.appUtil.hideSoftKeyboard(getActivity());
        subscriptionPlans=new ArrayList<SubscriptionPlan>();
        InputMethodManager inputManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = (getActivity()).getCurrentFocus();
try {
    if (v != null)
//            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
}
catch(Exception e)
{}
        linkUIElements();
        if (paymentActivity.connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            getPlans();
        }
        else{
            AlertMessage alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
            alertMessage.show(getActivity().getFragmentManager(), "");
        }



    }
    private void linkUIElements()
    {
        planListLayout=(RelativeLayout) view.findViewById(R.id.planListLayout);
        rv_plan=(RecyclerView)view.findViewById(R.id.rv_plan);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_plan.setLayoutManager(linearLayoutManager);
    }
    public void getPlans() {

        if (paymentActivity.connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

             progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Processing");
                progressDialog.show();


            try {
                JSONObject jsonParam1 = new JSONObject();
                jsonParam1.accumulate("app_id",Constant.APP_ID);
                Log.i("Alpha", "jsonparam -----" + jsonParam1.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.PLAN, jsonParam1,
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
                                        JSONArray result = jsonParam.getJSONArray("result");
                                        for (int i = 0; i < result.length(); i++) {
                                            SubscriptionPlan subscriptionPlan=new SubscriptionPlan();
                                            subscriptionPlan.setId(result.getJSONObject(i).getString("id"));
                                            subscriptionPlan.setAmount(result.getJSONObject(i).getString("amount"));
                                            subscriptionPlan.setStatement_descriptor(result.getJSONObject(i).getString("statement_descriptor"));
                                            subscriptionPlan.setCurrency(result.getJSONObject(i).getString("currency"));
                                            subscriptionPlan.setInterval(result.getJSONObject(i).getString("interval"));
                                            subscriptionPlan.setProduct(result.getJSONObject(i).getString("product"));
                                            subscriptionPlan.setName(result.getJSONObject(i).getString("name"));
                                            subscriptionPlans.add(subscriptionPlan);
                                        }
                                        if(subscriptionPlans.size()>0)
                                        {
                                        customSubscriptionPlanListAdapter = new CustomSubscriptionPlanListAdapter(subscriptionPlans, getActivity(), getActivity().getSupportFragmentManager());
                                        rv_plan.setAdapter(customSubscriptionPlanListAdapter);
                                        planListLayout.setVisibility(View.VISIBLE);

                                        }
                                        else {
                                            planListLayout.setVisibility(View.GONE);
                                        }
                                        progressDialog.dismiss();

                                    }
                                    else  {
                                        planListLayout.setVisibility(View.GONE);
                                        progressDialog.dismiss();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);

                                        alertMessage.show(getActivity().getFragmentManager(), "");
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

                                Toast.makeText(getActivity(), "Time out error", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                }){    //this is the part, that adds the header to the request
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json ;charset=utf-8");
                        params.put("Authorization", "Bearer "+paymentActivity.appUtil.getPrefrence("accessToken"));
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
            alertMessage.show(getActivity().getFragmentManager(), "");
        }
    }

    @Override
    public void onSuccessResponse(String code, String message, String status) {



    }

    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {
        dialog.dismiss();

    }
}
