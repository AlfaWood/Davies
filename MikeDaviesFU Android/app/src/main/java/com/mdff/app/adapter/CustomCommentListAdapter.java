package com.mdff.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdff.app.R;
import com.mdff.app.model.Comment;

import java.util.ArrayList;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;

/**
 * Created by Swati.Gupta on 6/29/2018.
 */

public class CustomCommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Comment> comments;private Context context;private FragmentManager fragmentManager;

    public CustomCommentListAdapter(ArrayList<Comment> comments, Context context, FragmentManager fragmentManager)
    {
        this.comments=comments;
        this.context=context;
        this.fragmentManager = fragmentManager;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        final int pos=position; final ItemViewHolder hol;
        if (holder1 instanceof ItemViewHolder) {
            final ItemViewHolder holder = (ItemViewHolder) holder1;
            hol=holder;
            final SpannableStringBuilder spannable;
            spannable = new SpannableStringBuilder(comments.get(position).getUserName()+": "+comments.get(position).getContent());
            spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, comments.get(position).getUserName().length()+1, SPAN_EXCLUSIVE_INCLUSIVE);
            spannable.setSpan(
                    new StyleSpan(BOLD),
                    0, comments.get(position).getUserName().length()+1,
                    SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tv_comment.setText(spannable);
        }

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_comment,tv_readmore;LinearLayout comment_layout;
        public ItemViewHolder(View view) {
            super(view);
            tv_comment = (TextView) view.findViewById(R.id.tv_comment);
            tv_readmore = (TextView) view.findViewById(R.id.tv_readmore);
            comment_layout = (LinearLayout) view.findViewById(R.id.comment_layout);
            Typeface faceMedium = Typeface.createFromAsset(context.getAssets(),
                    "fonts/helvetica-neue-medium.ttf");
            Typeface faceLightItalic = Typeface.createFromAsset(context.getAssets(),
                    "fonts/HelveticaNeue-LightItalic.ttf");
            tv_comment.setTypeface(faceMedium);
            tv_comment.setTextColor(Color.parseColor("#a7a7a7"));
            tv_readmore.setTypeface(faceLightItalic);
        }
    }





    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(ArrayList<Comment> list) {

        notifyDataSetChanged();
    }


}