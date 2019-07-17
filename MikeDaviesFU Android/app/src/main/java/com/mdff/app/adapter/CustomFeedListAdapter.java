package com.mdff.app.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdff.app.R;
import com.mdff.app.app_interface.LikeDislike;
import com.mdff.app.app_interface.OnLoadMoreListener;
import com.mdff.app.app_interface.VolleyCallback;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.fragment.Resources;
import com.mdff.app.model.Feed;
import com.mdff.app.utility.ApiController;
import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;


/**
 * Created by Swati.Gupta on 4/16/2018.
 */

public class CustomFeedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Feed> feedList;
    private Context context;
    CallAssetFragment CallAssetFragment;
    private FragmentManager fragmentManager;
    ItemViewHolder hol;
    private ImageLoader imageLoader;
    Fragment fragment;
    private static boolean fragmentExist = false;
    FragmentTransaction fragmentTransaction;// variable to track event time
    private long mLastClickTime = 0;
    private LikeDislike likeDislike;ApiController apiController;Activity activity;
    private boolean isLoading=false;
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public CustomFeedListAdapter(ArrayList<Feed> feedList, Context context, CallAssetFragment CallAssetFragment, FragmentManager fragmentManager, ImageLoader imageLoader,LikeDislike likeDislike,RecyclerView recyclerView) {
        this.feedList = feedList;
        this.context = context;
        this.CallAssetFragment = CallAssetFragment;
        this.fragmentManager = fragmentManager;
        this.imageLoader = imageLoader;
        this.likeDislike=likeDislike;
        isLoading=false;
        if (imageLoader == null)
            this.imageLoader = AlphaApplication.getInstance().getImageLoader();

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                    isLoading = false;
                Log.i("RecyclerView.OnScrol", "1");
//                    if(count<page_count) {
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                        Log.i("RecyclerView.OnScrol", "2");
                        isLoading = true;
                        Log.i("RecyclerView.OnScrol", "3");
                    }

                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setLoaded() {
        Log.i("setLoaded", "71");
        isLoading = false;
    }

    public void setNotLoaded() {
        Log.i("setNotLoaded", "709");
        isLoading = true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh=null;
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.feed_list_items, parent, false);
            vh= new ItemViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.progressbar_item, parent, false);
            vh= new ProgressViewHolder(view);
        }
        return  vh;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        final int pos = position;
        final ItemViewHolder hol;
        if (holder1 instanceof ItemViewHolder) {
            ItemViewHolder holder = (ItemViewHolder) holder1;
            hol = holder;
            hol.tv_pos.setText(String.valueOf(pos));
            hol.tv_date.setText(feedList.get(pos).getDateAndTime());
            hol.tv_socialsite.setText(feedList.get(pos).getSocialPlatformName());
            hol.tv_story.setText(feedList.get(pos).getStoryData());
            hol.tv_likescount.setText(feedList.get(pos).getNumberOfLikes());
            hol.tv_comment_count.setText(feedList.get(pos).getNumberOfComments());
            hol.tv_story.post(new Runnable() {
                @Override
                public void run() {
                    int lineCnt = hol.tv_story.getLineCount();
                    if (lineCnt > 0) {
                        if (hol.tv_story.getLayout().getEllipsisCount(lineCnt - 1) > 0) {
                            hol.tv_readmore.setVisibility(View.VISIBLE);
                        } else {
                            hol.tv_readmore.setVisibility(View.INVISIBLE);
                        }

                    }
                }
            });


                if(feedList.get(pos).getIsLike().equals("0"))
                {
                    hol.iv_like.setBackground(context.getResources().getDrawable(R.drawable.dislike));
                }
                else{
                    hol.iv_like.setBackground(context.getResources().getDrawable(R.drawable.likes));
                }

            if (feedList.get(pos).getSocialPlatformName().equals("Alpha Post")) {
                hol.like_comment_layout.setVisibility(View.VISIBLE);
                hol.tv_socialsite.setText(feedList.get(pos).getTitle());
            } else {
                hol.tv_socialsite.setText(feedList.get(pos).getSocialPlatformName());
                hol.like_comment_layout.setVisibility(View.VISIBLE);

            }
//            comment section start
            if((feedList.get(pos).getNumberOfComments().equals("")?0:Integer.parseInt(feedList.get(pos).getNumberOfComments()))>0)
            {
                hol.comment_layout.setVisibility(View.VISIBLE);
                if(feedList.get(pos).getAlphaCommented().equals("1"))
                {
                    hol.alpha_comment_layout.setVisibility(View.VISIBLE);
                    hol.tv_alpha_comment.setText(feedList.get(pos).getAlpha_comment());
                }
                else   if(feedList.get(pos).getAlphaCommented().equals("0")) {
                    hol.alpha_comment_layout.setVisibility(View.GONE);
                }

                SpannableStringBuilder spannable;
                    spannable = new SpannableStringBuilder(feedList.get(pos).getComment1_user() + ": " + feedList.get(pos).getComment1());
                    spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, feedList.get(pos).getComment1_user().length() + 1, SPAN_EXCLUSIVE_INCLUSIVE);
                    spannable.setSpan(
                            new StyleSpan(BOLD),
                            0, feedList.get(pos).getComment1_user().length() + 1,
                            SPAN_EXCLUSIVE_EXCLUSIVE);
                    hol.tv_comment1.setText(spannable);
                    hol.comment_one_layout.setVisibility(View.VISIBLE);

                    if (Integer.parseInt(feedList.get(pos).getNumberOfComments()) > 1) {
                        spannable = null;
                        spannable = new SpannableStringBuilder(feedList.get(pos).getComment2_user() + ": " + feedList.get(pos).getComment2());
                        spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, feedList.get(pos).getComment2_user().length() + 1, SPAN_EXCLUSIVE_INCLUSIVE);
                        spannable.setSpan(
                                new StyleSpan(BOLD),
                                0, feedList.get(pos).getComment2_user().length() + 1,
                                SPAN_EXCLUSIVE_EXCLUSIVE);
                        hol.tv_comment2.setText(spannable);
                        hol.comment_two_layout.setVisibility(View.VISIBLE);
                    } else {
                        hol.comment_two_layout.setVisibility(View.GONE);
                    }
            }
            else{
                hol.comment_layout.setVisibility(View.GONE);
            }




//            comment section end
            if (feedList.get(pos).getAsset().get(0).getType().equals("text")) {
                hol.imv_layout.setVisibility(View.GONE);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        3.0f
                );
                hol.content_layout.setLayoutParams(param);
            } else {
                hol.imv_layout.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        2.0f
                );
                hol.content_layout.setLayoutParams(param);
                try {
                    String url = (feedList.get(pos).getAsset().get(0).getType().equals("video") ? feedList.get(pos).getAsset().get(0).getThumbnail_url() : feedList.get(pos).getAsset().get(0).getUrl());

                    if (feedList.get(pos).getAsset().get(0).getType().equals("video")) {
                        hol.iv_play.setVisibility(View.VISIBLE);

                    } else {
                        hol.iv_play.setVisibility(View.GONE);

                    }
                    Picasso.with(context).load(url)//download URL
                            .into(hol.imv_story);//imageview

//                    hol.imv_story.setImageUrl(url, imageLoader);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            hol.iv_likeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
String likeSt=feedList.get(pos).getIsLike().equals("0")?"1":"0";
                    @SuppressLint("ResourceType") Animation blink = AnimationUtils.loadAnimation(context.getApplicationContext(),R.animator.blink);
                    hol.iv_like.startAnimation(blink);
//                    apiController=new ApiController((Activity) context);
                   likeDislike.postLikeDislike(feedList.get(pos).getFeed_unique_id(),feedList.get(pos).getFeed_id(),likeSt ,(VolleyCallback) context,pos,"feedList", hol.iv_like,feedList.get(pos),(Activity) context,hol.tv_likescount,hol.iv_likeLayout);
//                    String st=apiController.postLike(feedList.get(pos).getFeed_id(),likeSt, (VolleyCallback) context);


                }


            });

            hol.fullLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    } else {
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Resources._ResourceLoadedOnce=false;
                        Intent intent = new Intent(context, com.mdff.app.activity.FeedDetails.class);
                        intent.putExtra("feeddetails", (Serializable) feedList.get(pos));
                        context.startActivity(intent);
                    }
                }


            });
        }
        else {
            ((ProgressViewHolder) holder1).progressBar.setIndeterminate(true);
        }
    }



    @Override
    public int getItemCount() {
        return feedList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_comment2,tv_comment1,tv_alpha_comment,tv_title, tv_date, tv_socialsite, tv_story, tv_likescount, tv_comment_count, tv_readmore, tv_pipe, tv_pos;
        private LinearLayout iv_likeLayout,fullLayout,content_layout, content_layout1, like_comment_layout,comment_two_layout,comment_layout,alpha_comment_layout,comment_one_layout;
        private RelativeLayout imv_layout;
        private LinearLayout ll_text;
        private ImageView imv_story, iv_play;ImageButton iv_like;

        //private NetworkImageView imv_story;
        public ItemViewHolder(View view) {
            super(view);
            tv_alpha_comment= (TextView) view.findViewById(R.id.tv_alpha_comment);
            tv_comment1= (TextView) view.findViewById(R.id.tv_comment1);
            tv_comment2= (TextView) view.findViewById(R.id.tv_comment2);
            fullLayout=(LinearLayout) view.findViewById(R.id.fullLayout);
            iv_likeLayout=(LinearLayout) view.findViewById(R.id.iv_likeLayout);
            comment_layout= (LinearLayout) view.findViewById(R.id.comment_layout);
            comment_one_layout= (LinearLayout) view.findViewById(R.id.comment_one_layout);
            comment_two_layout= (LinearLayout) view.findViewById(R.id.comment_two_layout);
            alpha_comment_layout= (LinearLayout) view.findViewById(R.id.alpha_comment_layout);
            iv_like= (ImageButton) view.findViewById(R.id.iv_like);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_pos = (TextView) view.findViewById(R.id.tv_pos);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            tv_pipe = (TextView) view.findViewById(R.id.tv_pipe);
            tv_socialsite = (TextView) view.findViewById(R.id.tv_socialsite);
            tv_story = (TextView) view.findViewById(R.id.tv_story);
            Typeface faceLightItalic = Typeface.createFromAsset(context.getAssets(),
                    "fonts/HelveticaNeue-LightItalic.ttf");
            Typeface faceMedium = Typeface.createFromAsset(context.getAssets(),
                    "fonts/helvetica-neue-medium.ttf");
            Typeface faceRegular = Typeface.createFromAsset(context.getAssets(),
                    "fonts/helvetica-neue-regular.ttf");
            Typeface faceLight = Typeface.createFromAsset(context.getAssets(),
                    "fonts/HelveticaNeue-Light.ttf");
            tv_likescount = (TextView) view.findViewById(R.id.tv_likescount);
            tv_comment_count = (TextView) view.findViewById(R.id.tv_comment_count);
            tv_readmore = (TextView) view.findViewById(R.id.tv_readmore);
            imv_story = (ImageView) view.findViewById(R.id.imv_story);
            iv_play = (ImageView) view.findViewById(R.id.iv_play);
//            imv_story = (NetworkImageView) view.findViewById(R.id.imv_story);
            content_layout = (LinearLayout) view.findViewById(R.id.content_layout);
            content_layout1 = (LinearLayout) view.findViewById(R.id.content_layout1);
//            imv_layout = (FrameLayout) view.findViewById(R.id.imv_layout);
            imv_layout = (RelativeLayout) view.findViewById(R.id.imv_layout);
            like_comment_layout = (LinearLayout) view.findViewById(R.id.like_comment_layout);
            tv_story.setTypeface(faceMedium);
            tv_readmore.setTypeface(faceLightItalic);
            tv_alpha_comment.setTypeface(faceLightItalic);
            tv_comment1.setTypeface(faceMedium);
            tv_comment2.setTypeface(faceMedium);
            tv_comment1.setTextColor(Color.parseColor("#a7a7a7"));
            tv_comment2.setTextColor(Color.parseColor("#a7a7a7"));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

        }
    }

    // Clean all elements of the recycler
    public void clear() {
        feedList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(ArrayList<Feed> list) {
        notifyDataSetChanged();

    }
    @Override
    public int getItemViewType(int position) {
//        return position;
        return feedList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private  class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }
    public interface CallAssetFragment {
        public void displayFragment(String type, String url, String pf);

        public void displayFragment(Feed f, Bitmap bmpImage);

    }

}

