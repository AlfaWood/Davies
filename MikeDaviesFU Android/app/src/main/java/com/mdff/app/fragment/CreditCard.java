package com.mdff.app.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdff.app.R;
import com.mdff.app.activity.PaymentActivity;
import com.mdff.app.model.SubscriptionPlan;
import com.mdff.app.utility.AlertMessage;
import com.mdff.app.utility.Constant;
import com.mdff.app.utility.NetworkUtils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreditCard extends Fragment {

    private CardInputWidget mCardInputWidget;private Button proceedBtn;
    private PaymentActivity paymentActivity;private View view;private AlertMessage alertMessage;
    private SubscriptionPlan selectedPlan;private TextView tv_name,tv_amount;private ProgressDialog progressDialog;
    private LinearLayout subscriptionLayout;
    public CreditCard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_credit_card, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view=view;
//        Bundle bundle = this.getArguments();
        paymentActivity=((PaymentActivity) getActivity());
        linkUIElements();
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paymentActivity.connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                    try {
                        createToken();
                    } catch (Exception e) {
                    }
                }
                else {
                    alertMessage = AlertMessage.newInstance(
                            getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                    alertMessage.show(getActivity().getFragmentManager(), "");
                }
            }
        });


    }
    private void linkUIElements()
    {
        mCardInputWidget = (CardInputWidget)view.findViewById(R.id.card_input_widget);
        proceedBtn= (Button)view.findViewById(R.id.proceedBtn);
        tv_name= (TextView)view.findViewById(R.id.tv_name);
        tv_amount= (TextView)view.findViewById(R.id.tv_amount);
        subscriptionLayout=(LinearLayout)view.findViewById(R.id.subscriptionLayout);
//        progressDialog=(ProgressBar) view.findViewById(R.id.progressBar);
        if(paymentActivity.type.equalsIgnoreCase("signup")) {
            selectedPlan = (SubscriptionPlan) getArguments().getSerializable("subscriptionPlan");
            subscriptionLayout.setVisibility(View.VISIBLE);
            tv_name.setText(selectedPlan.getName());
            tv_amount.setText("$ "+selectedPlan.getAmount());

        }
        else{
            subscriptionLayout.setVisibility(View.INVISIBLE);

        }

}
    private void createToken()
    {
        paymentActivity.progressDialog = new ProgressDialog(getActivity());
        paymentActivity.progressDialog .setMessage("Processing");
        paymentActivity.progressDialog .show();
        // Remember that the card object will be null if the user inputs invalid data.
        Card card = mCardInputWidget.getCard();
        if (card == null) {
            // Do not continue token creation.
//            Toast.makeText(mContext,"Invalid card detail",Toast.LENGTH_LONG ).show();
            paymentActivity.progressDialog.dismiss();
            alertMessage = AlertMessage.newInstance(
                    getString(R.string.invalid_card), getString(R.string.ok),getString(R.string.alert));
            alertMessage.show(getActivity().getFragmentManager(), "");
            mCardInputWidget.clear();
        }
        else{

            Stripe stripe = new Stripe(getActivity(), Constant.STRIPE_API_PUB_KEY);
            stripe.createToken(
                    card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            // Send token to your server
                            System.out.print(token);
                            paymentActivity.tokenId=token.getId();

                            if (paymentActivity.connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                                if(paymentActivity.type.equalsIgnoreCase("signup")) {
                                    paymentActivity.userSignUp(token.getId(), selectedPlan.getProduct(), selectedPlan.getAmount(),mCardInputWidget);

                                }
                                else{
                                    paymentActivity.addNewCard(token.getId(),mCardInputWidget);
                                }

                            }
                            else{
                                 paymentActivity.progressDialog.dismiss();;
                                AlertMessage alertMessage = AlertMessage.newInstance(
                                        getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                                alertMessage.show(getActivity().getFragmentManager(), "");
                            }

                        }
                        public void onError(Exception error) {
                            // Show localized error message
                             paymentActivity.progressDialog.dismiss();;
                            alertMessage = AlertMessage.newInstance(
                                    error.getMessage(), getString(R.string.ok),getString(R.string.alert));
                            alertMessage.show(getActivity().getFragmentManager(), "");
//                            Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_LONG ).show();
                        }
                    }
            );
        }
    }



}
