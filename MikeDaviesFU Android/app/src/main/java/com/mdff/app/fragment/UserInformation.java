package com.mdff.app.fragment;


import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mdff.app.R;
import com.mdff.app.activity.EULA;
import com.mdff.app.activity.SignupMain;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.utility.AlertMessage;
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

import org.json.JSONObject;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInformation extends Fragment implements View.OnClickListener, AlertMessage.NoticeDialogListenerWithoutView{
    private EditText et_firstname,et_lastname,et_uname;  private Button proceedBtn;
    private SignupMain signup;View view;private ScrollView scrollView;AlertMessage alertMessage;
    private LinearLayout mainLayout;private ProgressDialog progressDialog;

    public UserInformation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_information, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view=view;
//        Bundle bundle = this.getArguments();
        signup=((SignupMain) getActivity());
        linkUIElements(this.view);

    }

    private void linkUIElements(View view)
    {
        et_firstname=(EditText)view.findViewById(R.id.et_firstname);
        et_lastname=(EditText)view.findViewById(R.id.et_lastname);
        et_uname=(EditText)view.findViewById(R.id.et_uname);
        proceedBtn=(Button) view.findViewById(R.id.proceedBtn);
        scrollView = (ScrollView)view.findViewById(R.id.scrollView);
        mainLayout=(LinearLayout)view.findViewById(R.id.mainLayout);
        clickListener();
        try {
            setData();
        }
        catch (Exception e)
        {}
        et_uname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doSignup();
                    handled = true;
                }
                return handled;
            }
        });
    }
    private void setData()
    {
        try{
            et_firstname.setText( signup.appUtil.getPrefrence("tempFname"));
            et_lastname.setText( signup.appUtil.getPrefrence("tempLname"));
            et_uname.setText( signup.appUtil.getPrefrence("tempUname"));
        }
        catch (Exception e)
        {}

    }
    private void clickListener()
    {
        proceedBtn.setOnClickListener(this);
    }

    private void doSignup()
    {
        if(et_firstname.getText().toString().isEmpty())
        {
            displayAlert(mainLayout,et_firstname,getString(R.string.input_fname),getString(R.string.alert));
        }
        else  if(et_lastname.getText().toString().isEmpty())
        {
            displayAlert(et_firstname,et_lastname,getString(R.string.input_lname),getString(R.string.alert));
        }
        else  if(et_uname.getText().toString().isEmpty())
        {
            displayAlert(et_lastname,et_uname,getString(R.string.input_uname),getString(R.string.alert));
        }
        else if (!signup.appUtil.isUserNameCorrect(et_uname.getText().toString()))
        {
            displayAlert(et_lastname,et_uname,getString(R.string.uname_valid),getString(R.string.alert));
        }



        else{
            checkUserNameExist();
        }

    }

    private void checkUserNameExist() {
        int connectStatus = ConnectivityReceiver.isConnected(getActivity());
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                 progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Processing");
                progressDialog.show();
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("username", et_uname.getText().toString());
                jsonParam.accumulate("app_id", Constant.APP_ID);
                jsonParam.accumulate("device_type", "andr");
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.CHECK_USER_NAME, jsonParam,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("Alpha response", response.toString());
                                    JSONObject jsonParam = new JSONObject(response.toString());
                                    String status = jsonParam.getString("status");
                                    String message = jsonParam.getString("message");
                                    String code = jsonParam.getString("code");
                                    String url = jsonParam.getString("url");

                                    if (code.equals("10")) {
                                        /*JSONArray respObj= jsonParam.getJSONArray("result");
                                        Log.i("Alpha respObj", respObj.toString());*/
                                        progressDialog.dismiss();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),getString(R.string.alert));
                                        alertMessage.show(getActivity().getFragmentManager(), "");
                                    }

                                    else {
                                        progressDialog.dismiss();
                                        signup.user.setFname(et_firstname.getText().toString());
                                        signup.user.setLname(et_lastname.getText().toString());
                                        signup.user.setUname(et_uname.getText().toString());
                                        signup.user.setPaymentNonce("abc");
                                        signup.appUtil.setPrefrence("tempFname",et_firstname.getText().toString());
                                        signup.appUtil.setPrefrence("tempLname",et_lastname.getText().toString());
                                        signup.appUtil.setPrefrence("tempUname",et_uname.getText().toString());
                                        Intent intent=new Intent(getActivity(), EULA.class);
                                        intent.putExtra("type", "signup");
                                        intent.putExtra("url", url);
                                        intent.putExtra("user", (Serializable)signup.user);
                                        startActivity(intent);

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
            alertMessage.show(getActivity().getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.proceedBtn:
                doSignup();
                break;
            default:
                break;
        }


    }

    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {

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
        alertMessage.show(getActivity().getFragmentManager(), "");
    }


}
