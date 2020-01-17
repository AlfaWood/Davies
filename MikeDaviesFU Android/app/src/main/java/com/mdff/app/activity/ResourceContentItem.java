package com.mdff.app.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.mdff.app.R;
import com.mdff.app.model.ResourceItemContent;
import com.mdff.app.utility.AppUtil;
import com.mdff.app.utility.Utilities;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.io.Serializable;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ResourceContentItem extends AppCompatActivity implements  SeekBar.OnSeekBarChangeListener {
    ResourceItemContent resourceItemContent;
    TextView tv_title, tv_description, tv_createdDate;
    ImageView iv_story, iv_video_thumbnail, iv_play;
    public static boolean isfont = true;
    CircularProgressBar circularProgressBar, circularProgressBar1;
    LinearLayout backLayout;
    String toUserMobileNo;
    AppUtil appUtil;
    final int radius = 10;
    final int margin = 5;
    String date;
    Transformation transformation;
    private WebView wv_description;
    //    iframe
    private MyWebChromeClient mWebChromeClient = null;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    Activity activity;
    private RelativeLayout rl_audio, rl_video;
    private ImageButton forwardbtn, backwardbtn, pausebtn, playbtn;
    private MediaPlayer mPlayer;
    private TextView songName, startTime, songTime;
    private SeekBar songPrgs;
    private static int oTime = 0, sTime = 0, eTime = 0, fTime = 5000, bTime = 5000;
    private Handler hdlr = new Handler();
    private ImageView imgLogo;
    private ProgressDialog progressDialog;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    Utilities utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_item_view);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        activity = ResourceContentItem.this;
        Intent i = getIntent();
        resourceItemContent = (ResourceItemContent) i.getSerializableExtra("resourcesubList");
        appUtil = new AppUtil(activity);
        mPlayer = new MediaPlayer();
        utils = new Utilities();
        // initialzefindViewIds
        initializeIds();
        appUtil.hideSoftKeyboard(activity);


        backwardbtn = (ImageButton) findViewById(R.id.btnBackward);
        forwardbtn = (ImageButton) findViewById(R.id.btnForward);
        playbtn = (ImageButton) findViewById(R.id.btnPlay);
        pausebtn = (ImageButton) findViewById(R.id.btnPause);
        startTime = (TextView) findViewById(R.id.txtStartTime);
        songTime = (TextView) findViewById(R.id.txtSongTime);

        songPrgs = (SeekBar) findViewById(R.id.sBar);
        songPrgs.setOnSeekBarChangeListener(this);
        songPrgs.setClickable(true);

        pausebtn.setEnabled(false);


        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.start();
                sTime = mPlayer.getCurrentPosition();
                if (oTime == 0) {
                    songPrgs.setMax(eTime);
                    oTime = 1;
                }

                songPrgs.setProgress(sTime);
                updateProgressBar();
                pausebtn.setEnabled(true);
                playbtn.setEnabled(false);
            }
        });

        pausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.pause();
                pausebtn.setEnabled(false);
                playbtn.setEnabled(true);

            }
        });

        forwardbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get current song position
                int currentPosition = mPlayer.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if(currentPosition + seekForwardTime <= mPlayer.getDuration()){
                    // forward song
                    mPlayer.seekTo(currentPosition + seekForwardTime);
                }else{
                    // forward to end position
                    mPlayer.seekTo(mPlayer.getDuration());
                }

                if (!playbtn.isEnabled()) {
                    playbtn.setEnabled(true);
                }
            }
        });

        backwardbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current song position
                int currentPosition = mPlayer.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0){
                    // forward song
                    mPlayer.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // backward to starting position
                    mPlayer.seekTo(0);
                }
                if (!playbtn.isEnabled()) {
                    playbtn.setEnabled(true);
                }
            }
        });

        mPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
              /*  final Toast toast = Toast.makeText(ResourceContentItem.this, "Buffering...", Toast.LENGTH_SHORT);
                toast.show();*/
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      //  toast.cancel();
                    }
                }, 500);
            }
        });


    }


    public void updateProgressBar() {
        hdlr.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mPlayer.getDuration();
            long currentDuration = mPlayer.getCurrentPosition();
            // Displaying Total Duration time

            // Displaying time completed playing

            // Displaying Total Duration time
            songTime.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            startTime.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songPrgs.setProgress(progress);

            // Running this thread after 100 milliseconds
            hdlr.postDelayed(this, 100);
        }
    };

    private void initializeIds() {
        imgLogo= (ImageView) findViewById(R.id.imgLogo);
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv_video_thumbnail = (ImageView) findViewById(R.id.iv_video_thumbnail);
        circularProgressBar = (CircularProgressBar) findViewById(R.id.homeloader);
        circularProgressBar1 = (CircularProgressBar) findViewById(R.id.homeloader1);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_createdDate = (TextView) findViewById(R.id.tv_created_date);
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_description.setVisibility(View.GONE);
        wv_description = (WebView) findViewById(R.id.wv_description);
        wv_description.setWebViewClient(new CustomWebViewClient());
        iv_story = (ImageView) findViewById(R.id.iv_story);



        Typeface faceMedium = Typeface.createFromAsset(activity.getAssets(),
                "fonts/helvetica-neue-medium.ttf");
        tv_description.setTypeface(faceMedium);
        date = resourceItemContent.getCreated_at();
        String datePM = "";
        try {
            datePM = date.replace("pm", "PM");
            datePM = datePM.replace("am", "AM");
        } catch (Exception e) {
        }
        try {
            tv_createdDate.setText(datePM);
            tv_title.setText(resourceItemContent.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onBackPressed();
            }
        });

        rl_audio = findViewById(R.id.rl_audio);
        rl_video = findViewById(R.id.rl_video);

        try {
//            wv_description.loadDataWithBaseURL(null, resourceItemContent.getDescription(), "text/html", "utf-8", null);

           /* String html = "<html><body>Some text goes here<br>"
                    + "<iframe src=\"https://www.youtube.com/embed/kyC2Q2ZfINY?rel=0&amp;amp;controls=0\" height=\"360\" width=\"640\" allowfullscreen=\"\" frameborder=\"0\"/></iframe></body></html>";
            String html1="<html>\n" +
        " <head></head>\n" +
        " <body link=\"#9B9B9B\" style=\"word-wrap: break-word;font-family: Helvetica;font-size: 18px;\">\n" +
        "  <p><iframe margin-left=\"auto\" margin-right=\"auto\" width=\"390\" height=\"200\" src=\"http://view.vzaar.com/16206631/player\" frameborder=\"0\" allow=\"autoplay; encrypted-media\" allowfullscreen></iframe><br></p>\n" +
        " </body>\n" +
        "</html>";*/
            wv_description.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
            wv_description.getSettings().setJavaScriptEnabled(true);
            wv_description.getSettings().setAllowFileAccess(true);
            mWebChromeClient = new MyWebChromeClient();
            wv_description.setWebChromeClient(mWebChromeClient);
//            wv_description.setWebChromeClient(new WebChromeClient());
//            wv_description.setWebViewClient(new WebViewClient());

//            wv_description.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

            wv_description.loadDataWithBaseURL(null, resourceItemContent.getDescription(), "text/html", "utf-8", null);
        } catch (Exception e) {
            System.out.println(e);
        }
        Typeface face = Typeface.createFromAsset(activity.getAssets(),
                "fonts/HelveticaNeue-Light.ttf");


        tv_createdDate.setTypeface(face, Typeface.ITALIC);
        transformation = new RoundedCornersTransformation(radius, margin);


        if (resourceItemContent.getType().equals("text")) {
            iv_story.setVisibility(View.GONE);
            iv_video_thumbnail.setVisibility(View.GONE);

        } else if (resourceItemContent.getType().equalsIgnoreCase("both")) {

            String imgUri = resourceItemContent.getImage();
            if (imgUri != null && !imgUri.equals("")) {
                Picasso.get().load(imgUri).into(iv_story);
            }


            if (resourceItemContent.getAudio_url() != null && !resourceItemContent.getAudio_url().equals("")) {
                rl_audio.setVisibility(View.VISIBLE);
                rl_video.setVisibility(View.GONE);


                String imageUrl = resourceItemContent.getThumbnail_url();
                if (imageUrl != null && !imageUrl.equals("")) {
                    Picasso.get().load(imageUrl).into(imgLogo);
                }







                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                mPlayer = new MediaPlayer();
                String url = resourceItemContent.getAudio_url();// your URL here


                if (url != null && !url.equals("")) {
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mPlayer.setDataSource(url);
                        mPlayer.prepareAsync();
                        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                if (mp.equals(mPlayer)) {
                                    updateProgressBar();
                                }
                                if (progressDialog != null && progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                        });


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (resourceItemContent.getVideo_url() != null && !resourceItemContent.getVideo_url().equals("")) {
                rl_audio.setVisibility(View.GONE);
                rl_video.setVisibility(View.VISIBLE);

                iv_story.setVisibility(View.VISIBLE);
                iv_video_thumbnail.setVisibility(View.VISIBLE);
                iv_play.setVisibility(View.VISIBLE);

                String videoUri;
                try {


                    videoUri = resourceItemContent.getThumbnail_url();
                    Picasso.get().load(videoUri)//download URL
                            .into(iv_video_thumbnail);//imageview


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (resourceItemContent.getType().equalsIgnoreCase("video")) {
            iv_story.setVisibility(View.GONE);
            rl_video.setVisibility(View.VISIBLE);
            iv_video_thumbnail.setVisibility(View.VISIBLE);
            iv_play.setVisibility(View.VISIBLE);
            try {
                String imageUri;
                imageUri = resourceItemContent.getThumbnail_url();
                Picasso.get().load(imageUri)//download URL
                        .into(iv_video_thumbnail);//imageview
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resourceItemContent.getType().equalsIgnoreCase("image")) {
            iv_story.setVisibility(View.VISIBLE);
            iv_video_thumbnail.setVisibility(View.GONE);
            iv_play.setVisibility(View.GONE);
            try {
                String imageUri;
                imageUri = resourceItemContent.getImage();
                Picasso.get().load(imageUri)//download URL
                        .into(iv_story);//imageview
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resourceItemContent.getType().equalsIgnoreCase("audio")) {
            rl_audio.setVisibility(View.VISIBLE);
            rl_video.setVisibility(View.GONE);

            String imageUrl = resourceItemContent.getThumbnail_url();
            if (imageUrl != null && !imageUrl.equals("")) {
                Picasso.get().load(imageUrl).into(imgLogo);
            }


            String imgUri = resourceItemContent.getImage();
            if (imgUri != null && !imgUri.equals("")) {
                Picasso.get().load(imgUri).into(iv_story);
            }
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);

            String url = resourceItemContent.getAudio_url();
            // your URL here
            if (url != null && !url.equals("")) {

                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mPlayer.setDataSource(url);
                    mPlayer.prepareAsync();
                    mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            if (mp.equals(mPlayer)) {
                                updateProgressBar();
                            }
                            if (progressDialog != null && progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }



            }


        } else {
            rl_audio.setVisibility(View.GONE);
            rl_video.setVisibility(View.VISIBLE);
        }


        iv_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, ResourceVideoImage.class);
                Bundle bundle = new Bundle();
                i.putExtra("resurceSubItem", (Serializable) resourceItemContent);
                bundle.putString("video", "image");
                i.putExtras(bundle);
                startActivity(i);

            }
        });

        iv_video_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(activity, ResourceVideoImage.class);
                Bundle bundle = new Bundle();
                i.putExtra("resurceSubItem", (Serializable) resourceItemContent);
                bundle.putString("video", "video");
                i.putExtras(bundle);
                startActivity(i);

            }
        });
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        hdlr.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        hdlr.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mPlayer.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
        // forward or backward to certain seconds
        mPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView wv, String url) {
            boolean rt = false;
            try {

                if (url.startsWith("tel:")) {
                    toUserMobileNo = url.substring(4);
                    phoneCall(toUserMobileNo);
                /*Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(url));
                startActivity(intent);*/
                    rt = true;
                } else if (url.startsWith("email:")) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{url.substring(6)});
                    startActivity(intent);
                    rt = true;
                } else if (url.startsWith("url:")) {
                    if (url.substring(4).contains("http://") || url.substring(4).contains("https://")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url.substring(4)));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://" + url.substring(4)));
                        startActivity(intent);

                    }
                    return true;
                } else if (url.startsWith("http://") || url.startsWith("https://")) {

                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://" + url));
                    startActivity(intent);
                    return true;
                }

            } catch (Exception e) {

            }

            return rt;
        }


    }


    //    phone call method
    public void phoneCall(String toUserMobileNo) {
        try {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 11);

            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
//            callIntent.setData(Uri.parse("tel:" + "9336515501"));//change the number
                callIntent.setData(Uri.parse("tel:" + toUserMobileNo));
                startActivity(callIntent);
            }
        } catch (Exception e) {
        }
    }

    // grant permission code
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 11:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    phoneCall(toUserMobileNo);

                } else {

                }
                break;

            default:
                break;

        }
    }


    public class MyWebChromeClient extends WebChromeClient {

        FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            try {

                mContentView = (RelativeLayout) findViewById(R.id.detail);
//            mContentView.setBackgroundColor(getResources().getColor(R.color.black));
                mContentView.setVisibility(View.GONE);
                mCustomViewContainer = new FrameLayout(activity);
                mCustomViewContainer.setLayoutParams(LayoutParameters);
//            mCustomViewContainer.setBackgroundResource(android.R.color.black);
//                setActivityBackgroundColor(getResources().getColor(R.color.black));
//                mCustomViewContainer.setBackgroundColor(getResources().getColor(R.color.black));
                view.setLayoutParams(LayoutParameters);
                mCustomViewContainer.addView(view);
                mCustomView = view;
//                mCustomView.setBackgroundColor(getResources().getColor(R.color.black));
                mCustomViewCallback = callback;
                mCustomViewContainer.setVisibility(View.VISIBLE);
                activity.setContentView(mCustomViewContainer);

            } catch (Exception e) {
            }
        }

        public void setActivityBackgroundColor(int color) {
            View view = activity.getWindow().getDecorView();
            view.setBackgroundColor(color);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            } else {
                try {
                    // Hide the custom view.

                    mCustomView.setVisibility(View.GONE);
                    // Remove the custom view from its container.
                    mCustomViewContainer.removeView(mCustomView);
                    mCustomView = null;

                    mCustomViewContainer.setVisibility(View.GONE);
                    mCustomViewContainer = null;
                    mCustomViewCallback.onCustomViewHidden();
                    // Show the content view.
//                mContentView.setBackgroundColor(getResources().getColor(R.color.white));
                    mContentView.setVisibility(View.VISIBLE);
                    activity.setContentView(mContentView);
//                    setActivityBackgroundColor(getResources().getColor(R.color.white));
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (mCustomViewContainer != null)
            try {
                mWebChromeClient.onHideCustomView();
            } catch (Exception e) {
            }
        else if (wv_description.canGoBack())
            try {
                wv_description.goBack();
            } catch (Exception e) {
            }
        else
            try {
                super.onBackPressed();
            } catch (Exception e) {
            }
    }


    @Override
    protected void onStop() {
        super.onStop();
        try {
            mPlayer.stop();
            mPlayer.release();
            hdlr.removeCallbacks(mUpdateTimeTask);
        }
        catch (Exception ex)
        {

        }

    }
}
