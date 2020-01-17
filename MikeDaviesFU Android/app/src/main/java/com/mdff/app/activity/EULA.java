package com.mdff.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdff.app.R;
import com.mdff.app.model.User;

import java.io.Serializable;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class EULA extends AppCompatActivity implements View.OnClickListener {
    private WebView mWebview;
    TextView tv_decline, tv_accept;
    CircularProgressBar circularProgressBar;
    String url, type;
    private User user;
    private LinearLayout backLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eul);
        url = getIntent().getStringExtra("url");
        type = getIntent().getStringExtra("type");
        user = (User) getIntent().getSerializableExtra("user");
        initializeIds();
    }

    private void initializeIds() {
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        circularProgressBar = (CircularProgressBar) findViewById(R.id.homeloader);
        mWebview = (WebView) findViewById(R.id.webView);
        tv_accept = (TextView) findViewById(R.id.tv_accept);
        tv_decline = (TextView) findViewById(R.id.tv_decline);
        tv_accept.setEnabled(false);
        tv_decline.setOnClickListener(this);
        tv_accept.setOnClickListener(this);
        circularProgressBar.setVisibility(View.VISIBLE);
        WebSettings settings = mWebview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        mWebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebview.setScrollbarFadingEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mWebview.loadUrl(url);
        mWebview.setWebViewClient(new myWebClient());
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;

        }

        public void onPageFinished(WebView view, String url) {
            try {
                circularProgressBar.setVisibility(View.GONE);
                tv_accept.setEnabled(true);
            } catch (Exception exception) {
                exception.printStackTrace();
                circularProgressBar.setVisibility(View.GONE);
            }
        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_accept:
                Intent intent = new Intent(EULA.this, PaymentActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("user", (Serializable) user);
                startActivity(intent);
                break;

            case R.id.tv_decline:
                onBackPressed();
                break;
        }

    }
}
