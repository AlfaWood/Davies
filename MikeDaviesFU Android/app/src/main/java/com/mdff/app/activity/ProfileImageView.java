package com.mdff.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mdff.app.R;
import com.mdff.app.utility.AppUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class ProfileImageView extends AppCompatActivity {
    String profileImage;
    Boolean profileFromWeb;
    ImageView iv_fullImage;
    LinearLayout backLayout;
    AppUtil appUtil;
    Activity activity;
    CircularProgressBar circularProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image_view);
        Intent intent = getIntent();
        activity = ProfileImageView.this;
        appUtil = new AppUtil(activity);
        circularProgressBar = (CircularProgressBar) findViewById(R.id.homeloader);

        try {
            profileImage = intent.getExtras().getString("profile");
            profileFromWeb = intent.getExtras().getBoolean("profileFromWeb");
        } catch (Exception e) {
            e.printStackTrace();
        }
        iv_fullImage = (ImageView) findViewById(R.id.fullScreenImageView);
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        if (profileFromWeb == true)
            rotateImage(BitmapFactory.decodeFile(profileImage));
        else {

            try {
                if (profileImage.equalsIgnoreCase("")) {
                    circularProgressBar.setVisibility(View.GONE);

                } else {
                    circularProgressBar.setVisibility(View.VISIBLE);
                    Picasso.with(ProfileImageView.this).load(profileImage)

                            .placeholder(R.drawable.user_pic).into(iv_fullImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            circularProgressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError() {
                            circularProgressBar.setVisibility(View.GONE);

                            //   Toast.makeText(activity, "No Image Found", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface1 = null;
        try {
            exifInterface1 = new ExifInterface(profileImage);


        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface1.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            default:
        }
        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        iv_fullImage.setImageBitmap(rotateBitmap);

    }

}
