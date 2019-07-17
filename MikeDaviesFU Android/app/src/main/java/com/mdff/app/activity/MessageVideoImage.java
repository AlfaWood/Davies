package com.mdff.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.mdff.app.R;
import com.mdff.app.model.MessageItems;
import com.mdff.app.utility.AlertMessage;
import com.mdff.app.utility.ConnectivityReceiver;
import com.mdff.app.utility.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class MessageVideoImage extends AppCompatActivity {
    private ImageView fullScreenImageView, close;
    CircularProgressBar circularProgressBar;
    RelativeLayout imageLayout, videoLayout;
    VideoView videoView;
    private static int RECOVERY_DIALOG_REQUEST = 001;
    private MediaController mediaControls;
    private LinearLayout backLayout, main_toolbar;
    private ProgressBar progressBar;
    View rootView;
    private int connectStatus;
    Bundle bundle;
    MessageItems data;Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resource_video_image);
        activity=MessageVideoImage.this;
        connectStatus = ConnectivityReceiver.isConnected(activity);
        // initialzefindViewIds
        initializeIds();



    }
    private void initializeIds() {
        circularProgressBar = (CircularProgressBar) findViewById(R.id.homeloader);
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        Intent i = getIntent();
        data= (MessageItems)i.getSerializableExtra("messageData");
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

            if (data.getType().equals("image")) {
                videoLayout.setVisibility(View.GONE);
                imageLayout.setVisibility(View.VISIBLE);
                main_toolbar.setVisibility(View.VISIBLE);
                try {

                    circularProgressBar.setVisibility(View.VISIBLE);
                    Picasso.with(activity)
                            .load(data.getAttachment_url())
                            .into(fullScreenImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    circularProgressBar.setVisibility(View.GONE);

                                }

                                @Override
                                public void onError() {
                                    Toast.makeText(activity, "No Image Found", Toast.LENGTH_SHORT).show();
                                    circularProgressBar.setVisibility(View.GONE);

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (data.getType().equals("video")) {
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
                videoView.setVideoPath(data.getAttachment_url());
                videoView.start();
                progressBar.setVisibility(View.VISIBLE);

            } else if (data.getType().equalsIgnoreCase("document"))

            {
               /* Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getAttachment_url()));
                startActivity(browserIntent);*/





               /* Intent intent = new Intent(Intent.ACTION_VIEW);

                intent.setDataAndType(Uri.parse(data.getAttachment_url()), "text/html");
                PackageManager packageManager = getActivity().getPackageManager();
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "There is no Compatible App To Open Document", Toast.LENGTH_SHORT).show();
                }*/

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
