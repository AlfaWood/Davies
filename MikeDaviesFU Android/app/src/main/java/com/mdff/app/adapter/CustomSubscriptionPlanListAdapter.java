package com.mdff.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdff.app.R;
import com.mdff.app.fragment.CreditCard;
import com.mdff.app.model.SubscriptionPlan;

import java.util.ArrayList;

/**
 * Created by Swati.Gupta on 6/8/2018.
 */

public class CustomSubscriptionPlanListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SubscriptionPlan> subscriptionPlans;
    private Context context;
    private FragmentManager fragmentManager;

    public CustomSubscriptionPlanListAdapter(ArrayList<SubscriptionPlan> subscriptionPlans, Context context, FragmentManager fragmentManager) {
        this.subscriptionPlans = subscriptionPlans;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_plan_list_item, parent, false);
        return new ItemViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        final int pos = position;
        final ItemViewHolder hol;
        if (holder1 instanceof ItemViewHolder) {
            ItemViewHolder holder = (ItemViewHolder) holder1;
            hol = holder;
            holder.tv_name.setText(subscriptionPlans.get(position).getName());
            if (subscriptionPlans.get(position).getStatement_descriptor().equalsIgnoreCase("null"))
                holder.tv_description.setText("");
            else
                holder.tv_description.setText(subscriptionPlans.get(position).getStatement_descriptor());

            holder.tv_amount.setText("$ " + subscriptionPlans.get(position).getAmount());
            holder.tv_subscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreditCard fragment = new CreditCard();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("subscriptionPlan", subscriptionPlans.get(pos));
                    fragment.setArguments(bundle);
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
            });

        }

    }

    @Override
    public int getItemCount() {
        return subscriptionPlans.size();
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_amount, tv_description, tv_subscribe;

        public ItemViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_amount = (TextView) view.findViewById(R.id.tv_amount);
            tv_description = (TextView) view.findViewById(R.id.tv_description);
            tv_subscribe = (TextView) view.findViewById(R.id.tv_subscribe);
            Typeface faceMedium = Typeface.createFromAsset(context.getAssets(),
                    "fonts/helvetica-neue-medium.ttf");
            tv_name.setTypeface(faceMedium);
            tv_amount.setTypeface(faceMedium);
            tv_description.setTypeface(faceMedium);
            tv_subscribe.setTypeface(faceMedium);

        }
    }

}
