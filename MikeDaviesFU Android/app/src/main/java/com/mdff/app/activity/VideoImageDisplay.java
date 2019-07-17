package com.mdff.app.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.media.MediaPlayer;
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
import com.mdff.app.utility.AlertMessage;
import com.mdff.app.utility.ConnectivityReceiver;
import com.mdff.app.utility.Constant;
import com.mdff.app.utility.NetworkUtils;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class VideoImageDisplay extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener, AlertMessage.NoticeDialogListenerWithoutView {
    private ImageView fullScreenImageView,close;LinearLayout main_toolbar;
    Bundle bundle;
    CircularProgressBar circularProgressBar;
    RelativeLayout imageLayout, videoLayout, youTubevideoLayout;
    VideoView videoView;
    YouTubePlayerView yt_videoView;
    private static int RECOVERY_DIALOG_REQUEST = 001;
    Activity activity;
    private MediaController mediaControls;
    private LinearLayout backLayout;
    private ProgressBar progressBar;
    private int connectStatus;
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_image_display);
        bundle = getIntent().getExtras();
        activity = VideoImageDisplay.this;
        connectStatus = ConnectivityReceiver.isConnected(activity);
        linkUIElements();
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
            if (bundle != null) {
                if (bundle.getString("type").equals("image")) {
                    videoLayout.setVisibility(View.GONE);
                    youTubevideoLayout.setVisibility(View.GONE);
                    imageLayout.setVisibility(View.VISIBLE);
                    main_toolbar.setVisibility(View.VISIBLE);

                    try {
                         Picasso.with(activity).load(bundle.getString("url"))//download URL
                         .into(fullScreenImageView);//imageview
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                else if (bundle.getString("type").equalsIgnoreCase("video")) {
                    main_toolbar.setVisibility(View.GONE);
                    if (bundle.getString("pf").equalsIgnoreCase("Youtube")) {
                        imageLayout.setVisibility(View.GONE);
                        youTubevideoLayout.setVisibility(View.VISIBLE);
                        videoLayout.setVisibility(View.GONE);
                        yt_videoView.initialize(Constant.API_DEV_KEY, VideoImageDisplay.this);
                    } else {
                        imageLayout.setVisibility(View.GONE);
                        youTubevideoLayout.setVisibility(View.GONE);
                        videoLayout.setVisibility(View.VISIBLE);
                        if (mediaControls == null) {
                            // create an object of media controller class
                            mediaControls = new MediaController(activity);
                            mediaControls.setAnchorView(videoView);
                        }
                        // set the media controller for video view
                        videoView.setMediaController(mediaControls);
                        videoView.setVideoPath(bundle.getString("url"));
                        videoView.start();
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {

            AlertMessage alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
            alertMessage.show(activity.getFragmentManager(), "");
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

    private void linkUIElements() {
        circularProgressBar = (CircularProgressBar) findViewById(R.id.homeloader);
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        fullScreenImageView = (ImageView) findViewById(R.id.fullScreenImageView);
        close = (ImageView) findViewById(R.id.close);
        imageLayout = (RelativeLayout) findViewById(R.id.imageLayout);
        videoLayout = (RelativeLayout) findViewById(R.id.videoLayout);
        videoView = (VideoView) findViewById(R.id.videoView);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        youTubevideoLayout = (RelativeLayout) findViewById(R.id.youTubevideoLayout);
        yt_videoView = (YouTubePlayerView) findViewById(R.id.yt_videoView);
        main_toolbar = (LinearLayout) findViewById(R.id.main_toolbar);
// implement on completion listener on video view
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                Toast.makeText(getApplicationContext(), "Thank You...!!!", Toast.LENGTH_LONG).show(); // display a toast when an video is completed
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                return false;
            }
        });

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


    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

        if (!b) {
            youTubePlayer.loadVideo(extractYoutubeVideoId(bundle.getString("url")));

            // Hiding player controls
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(activity, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    "There was an error initializing the YouTubePlayer (%1$s)", youTubeInitializationResult.toString());
            Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Constant.API_DEV_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.yt_videoView);
    }

    public static String extractYoutubeVideoId(String ytUrl) {

        String vId = null;

        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(ytUrl);

        if (matcher.find()) {
            vId = matcher.group();
        }
        return vId;
    }

    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}
