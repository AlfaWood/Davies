package com.mdff.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdff.app.R;
import com.mdff.app.model.ResourceItemContent;
import com.mdff.app.utility.AppUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.Serializable;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ResourceContentItem extends AppCompatActivity {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_item_view);
        activity=ResourceContentItem.this;
        Intent i = getIntent();
        resourceItemContent = (ResourceItemContent) i.getSerializableExtra("resourcesubList");
        appUtil = new AppUtil(activity);
        // initialzefindViewIds
        initializeIds();
        appUtil.hideSoftKeyboard(activity);
    }

    private void initializeIds() {
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
            iv_story.setVisibility(View.VISIBLE);
            iv_video_thumbnail.setVisibility(View.VISIBLE);
            iv_play.setVisibility(View.VISIBLE);

            String imgUri, videoUri;
            try {
               /* circularProgressBar.setVisibility(View.VISIBLE);
                circularProgressBar1.setVisibility(View.VISIBLE);
*/

                imgUri = resourceItemContent.getImage();
                videoUri = resourceItemContent.getThumbnail_url();

                Picasso.with(activity).load(imgUri)//download URL
                        .into(iv_story);//imageview
                Picasso.with(activity).load(videoUri)//download URL
                        .into(iv_video_thumbnail);//imageview


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resourceItemContent.getType().equalsIgnoreCase("video")) {
            iv_story.setVisibility(View.GONE);
            iv_video_thumbnail.setVisibility(View.VISIBLE);
            iv_play.setVisibility(View.VISIBLE);
            try {
                String imageUri;
                imageUri = resourceItemContent.getThumbnail_url();
                Picasso.with(activity).load(imageUri)//download URL
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
                Picasso.with(activity).load(imageUri)//download URL
                        .into(iv_story);//imageview
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        iv_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, com.mdff.app.activity.ResourceVideoImage.class);
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

                Intent i = new Intent(activity, com.mdff.app.activity.ResourceVideoImage.class);
                Bundle bundle = new Bundle();
                i.putExtra("resurceSubItem", (Serializable) resourceItemContent);
                bundle.putString("video", "video");
                i.putExtras(bundle);
                startActivity(i);

            }
        });
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
//            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
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
            try{

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

            }
            catch (Exception e){}
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
                try{
                    // Hide the custom view.

                    mCustomView.setVisibility(View.GONE);
                    // Remove the custom view from its container.
                    mCustomViewContainer.removeView(mCustomView);
                    mCustomView = null;

                    mCustomViewContainer.setVisibility(View.GONE);
                    mCustomViewContainer=null;
                    mCustomViewCallback.onCustomViewHidden();
                    // Show the content view.
//                mContentView.setBackgroundColor(getResources().getColor(R.color.white));
                    mContentView.setVisibility(View.VISIBLE);
                    activity.setContentView(mContentView);
//                    setActivityBackgroundColor(getResources().getColor(R.color.white));
                }
                catch (Exception e){}
            }
        }
    }

    @Override
    public void onBackPressed() {
        
        if (mCustomViewContainer != null)
            try {
                mWebChromeClient.onHideCustomView();
            }
            catch (Exception e){}
        else if (wv_description.canGoBack())
            try{
                wv_description.goBack();
            }
            catch (Exception e){}
        else
            try{
                super.onBackPressed();
            }
            catch (Exception e){}
    }

}
