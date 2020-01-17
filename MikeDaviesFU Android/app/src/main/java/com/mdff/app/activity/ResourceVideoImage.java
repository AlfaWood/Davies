package com.mdff.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.mdff.app.R;
import com.mdff.app.model.ResourceItemContent;
import com.mdff.app.utility.AlertMessage;
import com.mdff.app.utility.ConnectivityReceiver;
import com.mdff.app.utility.NetworkUtils;
import com.squareup.picasso.Picasso;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class ResourceVideoImage extends AppCompatActivity {
    private ImageView fullScreenImageView,close;
    String imagevalue;
    Bundle bundle;
    CircularProgressBar circularProgressBar;
    RelativeLayout imageLayout, videoLayout;
    VideoView videoView;
    private MediaController mediaControls;
    private LinearLayout backLayout,main_toolbar;
    private ProgressBar progressBar;
    private int connectStatus;
    ResourceItemContent resourceItemContent;
   Activity activity;Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resource_video_image);
        activity=ResourceVideoImage.this;
        connectStatus = ConnectivityReceiver.isConnected(activity);
         i=getIntent();
        bundle = i.getExtras();
        initializeIds();
    }

    private void initializeIds() {
        circularProgressBar = (CircularProgressBar) findViewById(R.id.homeloader);
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        resourceItemContent = (ResourceItemContent) i.getSerializableExtra("resurceSubItem");
        videoLayout = (RelativeLayout) findViewById(R.id.videoLayout);
        videoView = (VideoView) findViewById(R.id.videoView);
        fullScreenImageView = (ImageView) findViewById(R.id.fullScreenImageView);
        imageLayout = (RelativeLayout) findViewById(R.id.imageLayout);
        videoLayout = (RelativeLayout) findViewById(R.id.videoLayout);
        videoView = (VideoView) findViewById(R.id.videoView);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        close = (ImageView) findViewById(R.id.close);
        main_toolbar = (LinearLayout) findViewById(R.id.main_toolbar);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

            if (resourceItemContent.getType().equals("image")) {
                videoLayout.setVisibility(View.GONE);
                imageLayout.setVisibility(View.VISIBLE);
                main_toolbar.setVisibility(View.VISIBLE);

                try {
                    Picasso.get().load(resourceItemContent.getImage())
                            .into(fullScreenImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resourceItemContent.getType().equals("video")) {
                main_toolbar.setVisibility(View.GONE);

                imageLayout.setVisibility(View.GONE);
                videoLayout.setVisibility(View.VISIBLE);
                if (mediaControls == null) {
                    // create an object of media controller class
                    mediaControls = new MediaController(activity);
                    mediaControls.setAnchorView(videoView);
                }
                // set the media controller for video view
                videoView.setMediaController(mediaControls);
                videoView.setVideoPath(resourceItemContent.getVideo_url());
                videoView.start();
                progressBar.setVisibility(View.VISIBLE);

            } else if (resourceItemContent.getType().equals("both")) {
                if (bundle != null) {
                    imagevalue = bundle.getString("video");
                    //The key argument here must match that used in the other activity

                    if (imagevalue.equalsIgnoreCase("video")) {
                        main_toolbar.setVisibility(View.GONE);
                        videoLayout.setVisibility(View.VISIBLE);
                        imageLayout.setVisibility(View.GONE);

                        if (mediaControls == null) {
                            // create an object of media controller class
                            mediaControls = new MediaController(activity);
                            mediaControls.setAnchorView(videoView);
                        }
                        // set the media controller for video view
                        videoView.setMediaController(mediaControls);
                        videoView.setVideoPath(resourceItemContent.getVideo_url());
                        videoView.start();
                        progressBar.setVisibility(View.VISIBLE);


                    } else {
                        try {
                            imageLayout.setVisibility(View.VISIBLE);
                            main_toolbar.setVisibility(View.VISIBLE);

                            videoLayout.setVisibility(View.GONE);
                            Picasso.get()
                                    .load(resourceItemContent.getImage())
                                    .into(fullScreenImageView);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

            } else {

                AlertMessage alertMessage = AlertMessage.newInstance(
                        getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                alertMessage.show(getFragmentManager(), "");
            }


            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.start();
                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                       int arg2) {
                            // TODO Auto-generated method stub
                            progressBar.setVisibility(View.GONE);
                            mp.start();
                        }
                    });
                }
            });


        }
    }
}
