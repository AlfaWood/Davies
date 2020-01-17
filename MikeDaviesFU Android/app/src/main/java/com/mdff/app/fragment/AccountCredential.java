package com.mdff.app.fragment;


import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountCredential extends Fragment implements View.OnClickListener, AlertMessage.NoticeDialogListenerWithoutView{
    private EditText et_email,et_password,et_confirmpassword;  private Button proceedBtn;
    private SignupMain signup;View view;private ScrollView scrollView;AlertMessage alertMessage;
    private LinearLayout mainLayout;private int connectStatus;private ProgressDialog progressDialog;

    public AccountCredential() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_credential, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view=view;
//        Bundle bundle = this.getArguments();
        signup=((SignupMain) getActivity());
        connectStatus = ConnectivityReceiver.isConnected(getActivity());
        linkUIElements(this.view);

    }

    private void linkUIElements(View view)
    {
        et_email=(EditText)view.findViewById(R.id.et_email);
        et_password=(EditText)view.findViewById(R.id.et_password);
        et_confirmpassword=(EditText)view.findViewById(R.id.et_confirmpassword);
        proceedBtn=(Button) view.findViewById(R.id.proceedBtn);
        scrollView = (ScrollView)view.findViewById(R.id.scrollView);
        mainLayout=(LinearLayout)view.findViewById(R.id.mainLayout);
        clickListener();
        try {
            setData();
        }
        catch (Exception e)
        {}

        et_confirmpassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
    et_email.setText( signup.appUtil.getPrefrence("tempEmail"));
    et_password.setText( signup.appUtil.getPrefrence("tempPwd"));
    et_confirmpassword.setText( signup.appUtil.getPrefrence("tempPwd"));
    }
    catch (Exception e)
    {}

}
    private void doSignup()
    {
        if(et_email.getText().toString().isEmpty())
        {
            displayAlert(mainLayout,et_email,getString(R.string.input_email),getString(R.string.alert));

        }
        else if (!signup.appUtil.isEmailCorrect(et_email.getText().toString()))
        {
            displayAlert(mainLayout,et_email,getString(R.string.valid_email),getString(R.string.alert));
        }
        else  if(et_password.getText().toString().isEmpty())
        {
            displayAlert(et_email,et_password,getString(R.string.input_pwd),getString(R.string.alert));
        }
        else  if(et_password.getText().toString().length()<6)
        {
            displayAlert(et_email,et_password,getString(R.string.pwd_valid),getString(R.string.alert));

        }
        else  if(et_confirmpassword.getText().toString().isEmpty())
        {
            displayAlert(et_password,et_confirmpassword,getString(R.string.input_cnf_pwd),getString(R.string.alert));

        }
        else if(!et_password.getText().toString().trim().equals(et_confirmpassword.getText().toString().trim()))
        {
            displayAlert(et_password,et_confirmpassword,getString(R.string.pwd_not_match),getString(R.string.alert));
        }
        else{
            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                try {
                    checkEmailExist();
                }
                catch (Exception e)
                { }

            } else {
                alertMessage = AlertMessage.newInstance(
                        getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                alertMessage.show(getActivity().getFragmentManager(), "");
            }
        }
    }

    void openFragment()
    {
        signup.user.setEmail(et_email.getText().toString());
        signup.user.setPassword(et_password.getText().toString());
        signup.appUtil.setPrefrence("tempEmail",et_email.getText().toString());
        signup.appUtil.setPrefrence("tempPwd",et_password.getText().toString());
        UserInformation userInformation = new UserInformation();
        Bundle bundle = new Bundle();
        userInformation.setArguments(bundle);
        FragmentTransaction transaction = signup.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, userInformation);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void clickListener()
    {
        proceedBtn.setOnClickListener(this);
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


    private void checkEmailExist() {

        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Processing");
                progressDialog.show();
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("email", et_email.getText().toString());
                jsonParam.accumulate("app_id",Constant.APP_ID);
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.CHECK_MAIL, jsonParam,
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
                                        /*JSONArray respObj= jsonParam.getJSONArray("result");
                                        Log.i("Alpha respObj", respObj.toString());*/
                                        progressDialog.dismiss();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),getString(R.string.alert));
                                        alertMessage.show(getActivity().getFragmentManager(), "");
                                    }

                                    else {
                                        progressDialog.dismiss();
                                        openFragment();
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
                                Toast.makeText(getActivity(), "Time out error", Toast.LENGTH_LONG).show();
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
            alertMessage.show(getActivity().getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }


}
