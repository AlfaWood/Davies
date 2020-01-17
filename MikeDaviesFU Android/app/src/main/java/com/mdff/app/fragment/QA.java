package com.mdff.app.fragment;


import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mdff.app.R;
import com.mdff.app.activity.Home;
import com.mdff.app.adapter.CustomQNAListAdapter;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.utility.ApiController;
import com.mdff.app.model.QnA;
import com.mdff.app.utility.AlertMessage;
import com.mdff.app.utility.ConnectivityReceiver;
import com.mdff.app.utility.Constant;
import com.mdff.app.utility.DividerRVDecoration;
import com.mdff.app.utility.NetworkUtils;
import com.mdff.app.app_interface.VolleyCallback;
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
public class QA extends Fragment implements View.OnClickListener ,AlertMessage.NoticeDialogListenerWithoutView,VolleyCallback {
    private RecyclerView rv_QNA;
    private CustomQNAListAdapter customQAListAdapter;
    private View view;
    private  String[] strQ = {"Question", "Question1"};
    private String[] strA = {"Answer", "Answer1"};
    private RelativeLayout rl_submitQuestion;
    private FragmentManager fragmentManager;private AlertMessage alertMessage;
    private TextView tv_top;private Home home; int connectStatus; private ArrayList<QnA> qnaItemList;
    private CustomQNAListAdapter customQNAListAdapter;private ProgressDialog pDialog;
    private SwipeRefreshLayout swipeContainer;
    private boolean _hasLoadedOnce= false; //  boolean field

    public QA() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view=inflater.inflate(R.layout.fragment_q, container, false);
        // Inflate the layout for this fragment
        home=((Home) getActivity());
        connectStatus = ConnectivityReceiver.isConnected(getActivity());
        qnaItemList=new ArrayList<>();
        initializeId(this.view);
        if(getUserVisibleHint()){ // fragment is visible
            swipeContainer.post(new Runnable() {
                @Override
                public void run() {

                    swipeContainer.setRefreshing(true);
                    if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                        getQAList();
                    } else {
                        swipeContainer.setRefreshing(false);
                        AlertMessage alertMessage = AlertMessage.newInstance(
                                getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                        alertMessage.show(getActivity().getFragmentManager(), "");
                    }
                }
            });
        }

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                     getQAList();
                } else {
                    swipeContainer.setRefreshing(false);
                    AlertMessage alertMessage = AlertMessage.newInstance(
                            getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                    alertMessage.show(getActivity().getFragmentManager(), "");
                }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.header_bg);
        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(isFragmentVisible_);
//        Toast.makeText(home,"QA Fragment",Toast.LENGTH_LONG).show();
         if (this.isVisible()) {
             // we check that the fragment is becoming visible
             if (isFragmentVisible_ && !_hasLoadedOnce) {
//                 _hasLoadedOnce = true;
                 swipeContainer.post(new Runnable() {
                     @Override
                     public void run() {

                         swipeContainer.setRefreshing(true);
                         if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                             getQAList();
                         } else {
                             swipeContainer.setRefreshing(false);
                             AlertMessage alertMessage = AlertMessage.newInstance(
                                     getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                             alertMessage.show(getActivity().getFragmentManager(), "");
                         }
                     }
                 });
             }
         }else{
//            System.out.println("----------Enter in QA-------");
         }


    }

    private void initializeId(View view) {
        tv_top = (TextView) view.findViewById(R.id.tv_top);
        Typeface faceMedium = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/helvetica-neue-medium.ttf");
        tv_top.setTypeface(faceMedium);

     /*   home.appUtil.setFontTypeFaceLight(tv_top);*/
        rv_QNA = (RecyclerView) view.findViewById(R.id.rv_qna);
        rl_submitQuestion = (RelativeLayout) view.findViewById(R.id.rl_submitQues);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_QNA.setLayoutManager(linearLayoutManager);
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        rv_QNA.addItemDecoration(new DividerRVDecoration(dividerDrawable));
        customQAListAdapter = new CustomQNAListAdapter(qnaItemList, getActivity(),getActivity().getSupportFragmentManager());
        rv_QNA.setAdapter(customQAListAdapter);
        rl_submitQuestion.setOnClickListener(this);
    }
    public void getQAList() {
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            try {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
            catch (Exception e)
            {}
            home.setProfile();
            try {
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("app_id", Constant.APP_ID);
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.GET_QA, jsonParam,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    //                               ************ start  unread message  counter api   call  ****************
                                    try {
                                        ApiController apiController = new ApiController(getActivity());
                                        apiController.getMessageCount(QA.this);
                                    }
                                    catch(Exception e)
                                    {}
//                                  *********** end unread message  counter api   call  ****************

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
                                            qnaItemList.clear();
                                            customQAListAdapter.clear();

                                       for (int i = 0; i < result.length(); i++) {
                                                QnA qnA = new QnA();
                                                JSONObject jsonObject = result.getJSONObject(i);
                                                for (int j = 0; j < jsonObject.length(); j++) {
                                                    JSONObject jsonObject1 = jsonObject.getJSONObject("asset");
                                                    if (jsonObject1.length() > 0) {
                                                        //  customResourceListAdapter.clear();
                                                        qnA.setName(jsonObject1.getString("name"));
                                                        qnA.setId(jsonObject1.getString("id"));
                                                        qnA.setQuestion(jsonObject1.getString("question"));
                                                        qnA.setAnswer(jsonObject1.getString("answer"));
                                                        qnaItemList.add(qnA);
                                                    }
                                                    else {
//                                                        pDialog.dismiss();
                                                        alertMessage = AlertMessage.newInstance(
                                                                message, getString(R.string.ok), status);
                                                        alertMessage.show(getActivity().getFragmentManager(), "");
                                                        // Toast.makeText(getActivity(), getString(R.string.noresource), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }
                                            if(qnaItemList.size()>0)
                                            {
//                                                pDialog.dismiss();
//                                            setListAdapter();
                                                customQAListAdapter.addAll();
                                                rv_QNA.setAdapter(customQAListAdapter);
                                                try {
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                }
                                                catch (Exception e){}
                                                swipeContainer.setRefreshing(false);
                                            }
                                            else {
                                               try {
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                }
                                                catch (Exception e){}
                                                swipeContainer.setRefreshing(false);
//                                                pDialog.dismiss();
                                                alertMessage = AlertMessage.newInstance(
                                                        getString(R.string.no_qa), getString(R.string.ok), status);
                                                alertMessage.show(getActivity().getFragmentManager(), "");
                                            }

                                        } else {
//                                            pDialog.dismiss();
                                           try {
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                }
                                                catch (Exception e){}
                                                swipeContainer.setRefreshing(false);
                                            alertMessage = AlertMessage.newInstance(
                                                    message, getString(R.string.ok), status);
                                            alertMessage.show(getActivity().getFragmentManager(), "");

                                        }
                                    }
                                    else {
//                                        pDialog.dismiss();
                                       try {
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                }
                                                catch (Exception e){}
                                                swipeContainer.setRefreshing(false);
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                    }

                                } catch (Throwable e) {
//                                    pDialog.dismiss();
                                  try {
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                }
                                                catch (Exception e1){}
                                                swipeContainer.setRefreshing(false);
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
                           try {
                                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                }
                                                catch (Exception e){}
                                                swipeContainer.setRefreshing(false);
                            if (error.getClass().equals(TimeoutError.class)) {
//                                swipeContainer.setRefreshing(false);
                                Toast.makeText(getActivity(), "Time out error", Toast.LENGTH_LONG).show();
                            }
                        }

                        else{
                            if(error.networkResponse.statusCode==401)
                            {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                    ApiController apiController =new ApiController(getActivity());
                                    apiController.userLogin(QA.this);
                                }
                                else{
                                    try {
                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    }
                                    catch (Exception e){}
                                    swipeContainer.setRefreshing(false);
                                    alertMessage = AlertMessage.newInstance(
                                            getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                                    alertMessage.show(getActivity().getFragmentManager(), "");
                                }

                            }
                        }
                    }
                }) {    //this is the part, that adds the header to the request
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json ;charset=utf-8");
                        params.put("Authorization", "Bearer " + home.appUtil.getPrefrence("accessToken"));
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
                try {
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                }
                catch (Exception e1){}
                swipeContainer.setRefreshing(false);
                e.printStackTrace();
//                pDialog.dismiss();

            }

        } else {
            try {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
            catch (Exception e){}
            swipeContainer.setRefreshing(false);
             alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
            alertMessage.show(getActivity().getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }


    private void setListAdapter() {
        customQNAListAdapter=new CustomQNAListAdapter(qnaItemList,getActivity(),getActivity().getSupportFragmentManager());
        rv_QNA.setAdapter(customQNAListAdapter);
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        rv_QNA.addItemDecoration(new DividerRVDecoration(dividerDrawable));
        if (qnaItemList.size() == 0) {
            Toast.makeText(getActivity(), getString(R.string.no_qa), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rl_submitQues:
                Fragment fragment = new TypeQuestion();
                if (fragment != null) {
                    fragmentManager=getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.animator.enter
                            ,
                            R.animator.exit,
                            R.animator.left_to_right,
                            R.animator.right_to_left

                    );
                    fragmentTransaction.add(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;
        }


    }

    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {
        System.out.print("hi");
    }

    @Override
    public void onSuccessResponse(String code, String message, String status) {
        if(code.equals("10")) {
            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                getQAList();
            }
            else{
                alertMessage = AlertMessage.newInstance(
                        getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                alertMessage.show(getActivity().getFragmentManager(), "");
            }
        }
        else{
            swipeContainer.setRefreshing(false);
            home.isFinish=true;
            alertMessage = AlertMessage.newInstance(
                    message, getString(R.string.ok),status);
            alertMessage.show(getActivity().getFragmentManager(), "");

        }
    }
}
