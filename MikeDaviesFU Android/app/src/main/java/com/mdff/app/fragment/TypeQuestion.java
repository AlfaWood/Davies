package com.mdff.app.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mdff.app.R;
import com.mdff.app.activity.Home;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.utility.ApiController;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Deepika.Mishra on 4/26/2018.
 */

public class TypeQuestion extends Fragment implements View.OnClickListener,AlertMessage.NoticeDialogListenerWithoutView,VolleyCallback {
    private View rootView;private int connectStatus;private RadioGroup rg_uname_type;
    private FragmentManager fragmentManager;RelativeLayout rl_submit;private EditText et_que;
    private Home home;private AlertMessage alertMessage;private boolean isAdded=false;
    private RadioButton rb_uname,rb_anonymous;
private ProgressDialog progressDialog;private LinearLayout backLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.qa_typequestion, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView =view;
        rootView.setBackgroundColor(Color.WHITE);
        connectStatus = ConnectivityReceiver.isConnected(getActivity());
        home=((Home) getActivity());
        initialzefindViewIds();


    }

    private void initialzefindViewIds() {
        rl_submit=(RelativeLayout)rootView.findViewById(R.id.rl_submitQues);
        rg_uname_type=(RadioGroup) rootView.findViewById(R.id.rg_uname_type);
        rb_anonymous=(RadioButton) rootView.findViewById(R.id.rb_anonymous);
        rb_uname=(RadioButton) rootView.findViewById(R.id.rb_uname);
        et_que=(EditText) rootView.findViewById(R.id.et_que);
        backLayout= (LinearLayout) rootView.findViewById(R.id.backLayout);
        et_que.setCursorVisible(true);
        Typeface faceMedium = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/helvetica-neue-medium.ttf");
        et_que.setTypeface(faceMedium);
        rb_anonymous.setTypeface(faceMedium);
        rb_uname.setTypeface(faceMedium);
        et_que.setTextColor(getResources().getColor(R.color.black));
        et_que.requestFocus();
        rl_submit.setOnClickListener(this);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        rootView. findViewById(R.id.mainLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
        rootView. findViewById(R.id.mainLayout1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
        et_que.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {

                if (view.getId() == R.id.et_que) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()&MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_submitQues:
                home.appUtil.hideSoftKeyboard(getActivity());
                if(et_que.getText().toString().isEmpty())
                {
                    alertMessage = AlertMessage.newInstance(
                            getString(R.string.input_que), getString(R.string.ok),getString(R.string.alert));
                    alertMessage.show(getActivity().getFragmentManager(), "");
                }
                else{

                    if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                        insertQuestion();
                    }
                    else{
                        alertMessage = AlertMessage.newInstance(
                                getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                        alertMessage.show(getActivity().getFragmentManager(), "");
                    }
                }

                break;
        }    }





    private void insertQuestion() {
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Processing");
                progressDialog.show();
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("question", et_que.getText().toString());
                jsonParam.accumulate("anonymous", (rg_uname_type.getCheckedRadioButtonId()==R.id.rb_uname)?"0":"1");
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.INSERT_Q, jsonParam,
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
                                        isAdded=true;
                                        progressDialog.dismiss();
                                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                        builder.setTitle(status);
                                        builder.setMessage(message);
                                        builder.setPositiveButton(getString(R.string.ok),new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                // TODO Auto-generated method stub

                                                et_que.setText("");
                                                rb_anonymous.setSelected(true);
                                                rg_uname_type.check(R.id.rb_anonymous);
                                                arg0.dismiss();

                                            }
                                        });

                                        Dialog dialog=builder.create();
                                        dialog.setCancelable(false);
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.show();

                                    }

                                    else {
                                        isAdded=false;
                                        progressDialog.dismiss();
//                                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        alertMessage.show(getActivity().getFragmentManager(), "");
                                    }
                                } catch (Throwable e) {
                                    isAdded=false;
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
                        isAdded=false;
                        progressDialog.dismiss();
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                Toast.makeText(getActivity(), "Time out error", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            if(error.networkResponse.statusCode==401)
                            {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                                    ApiController apiController =new ApiController(getActivity());
                                    apiController.userLogin(TypeQuestion.this);
                                }
                                else{
                                    alertMessage = AlertMessage.newInstance(
                                            getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                                    alertMessage.show(getActivity().getFragmentManager(), "");
                                }
                            }
                        }
                    }
                }){    //this is the part, that adds the header to the request
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json ;charset=utf-8");
                        params.put("Authorization", "Bearer "+home.appUtil.getPrefrence("accessToken"));
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
                isAdded=false;
                e.printStackTrace();
                progressDialog.dismiss();
//                displayAlert();
            }

        } else {
            isAdded=false;
            alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
            alertMessage.show(getActivity().getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {
        if(isAdded)
        {
            isAdded=false;
            dialog.dismiss();
            et_que.setText("");
            rb_anonymous.setSelected(true);
        }
        else{
            dialog.dismiss();

        }




    }

    @Override
    public void onSuccessResponse(String code, String message, String status) {

        if(code.equals("10")) {
            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                insertQuestion();
            }
            else{
                alertMessage = AlertMessage.newInstance(
                        getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                alertMessage.show(getActivity().getFragmentManager(), "");
            }
        }
        else{

            home.isFinish=true;
            alertMessage = AlertMessage.newInstance(
                    message, getString(R.string.ok),status);
            alertMessage.show(getActivity().getFragmentManager(), "");

        }

    }
}
