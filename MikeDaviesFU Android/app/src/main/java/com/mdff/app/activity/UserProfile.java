package com.mdff.app.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mdff.app.BuildConfig;
import com.mdff.app.R;
import com.mdff.app.app_interface.VolleyCallback;
import com.mdff.app.controller.AlphaApplication;
import com.mdff.app.fragment.UserInformation;
import com.mdff.app.model.Profile;
import com.mdff.app.utility.AlertMessage;
import com.mdff.app.utility.ApiController;
import com.mdff.app.utility.AppUtil;
import com.mdff.app.utility.ConnectivityReceiver;
import com.mdff.app.utility.Constant;
import com.mdff.app.utility.NetworkUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class UserProfile extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener, AlertMessage.NoticeDialogListenerWithoutView, VolleyCallback {
    EditText et_firstName, et_lastname, et_alias, et_email;TextView tv_accounInfo, tv_userProfile, tv_memberdate,
            tv_first_name, tv_last_name, tv_alias, tv_email, tv_password,
            tv_changePassword,tv_addnewCard,tv_editAccount, tv_expiryDate, tv_accountNumber, tv_cardno,tv_submit,tv_cancelMembership;
    ImageView iv_userImage, iv_cameraImage;
    RelativeLayout rl_profile;ProgressDialog progressDialog;boolean isFinish=false;
    int connectStatus;
     Activity activity;
    Profile profile;
    String encodeImage;
    CircularProgressBar circularProgressBar, circularProgressBar1;
    AppUtil appUtil;
    LinearLayout backLayout, mainLayout,billingLayout;
    private AlertMessage alertMessage;
    private boolean clicked = false, isImageFromWeb = false;
    String mCurrentPhotoPath, ImageFileLocation;
    Bitmap rotateBitmap;
     private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int CAMERA_PERMISSION_REQUEST = 3;
    private static final int READ_STORAGE_PERMISSION_REQUEST = 4;
    private static final int WRITE_STORAGE_PERMISSION_REQUEST = 5;
    UserInformation userInformation;
    ScrollView scrollView;String type;public static String comeFrom="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        activity = UserProfile.this;
        connectStatus = ConnectivityReceiver.isConnected(this);
        appUtil = new AppUtil(activity);
        userInformation = new UserInformation();
        initializeIds();
        getProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!comeFrom.equalsIgnoreCase("onActivityResult")) {
            isImageFromWeb=false;
            getProfile();
        }
    }

    private void initializeIds() {
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        circularProgressBar1 = (CircularProgressBar) findViewById(R.id.homeloader1);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        billingLayout = (LinearLayout) findViewById(R.id.billingLayout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        circularProgressBar = (CircularProgressBar) findViewById(R.id.homeloader);
        et_firstName = (EditText) findViewById(R.id.et_firstname);
        et_lastname = (EditText) findViewById(R.id.et_lastname);
        et_alias = (EditText) findViewById(R.id.et_uname);
        et_email = (EditText) findViewById(R.id.et_email);
        tv_first_name = (TextView) findViewById(R.id.tv_firstname);
        tv_alias = (TextView) findViewById(R.id.tv_alias);
        tv_last_name = (TextView) findViewById(R.id.tv_lastName);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_memberdate = (TextView) findViewById(R.id.tv_memberdate);
        tv_userProfile = (TextView) findViewById(R.id.tv_userProfile);
        tv_accounInfo = (TextView) findViewById(R.id.tv_accounInfo);
        tv_password = (TextView) findViewById(R.id.tv_password);
        tv_changePassword = (TextView) findViewById(R.id.passwordChange);
        tv_addnewCard = (TextView) findViewById(R.id.tv_addnewCard);
        tv_editAccount = (TextView) findViewById(R.id.tv_editAccount);
//        tv_expiryDate = (TextView) findViewById(R.id.tv_expiryDate);
        tv_cancelMembership = (TextView) findViewById(R.id.tv_cancelMembership);
        tv_cardno = (TextView) findViewById(R.id.tv_cardno);
        tv_accountNumber = (TextView) findViewById(R.id.tv_accountNumber);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        iv_userImage = (ImageView) findViewById(R.id.iv_profilePic);
        iv_cameraImage = (ImageView) findViewById(R.id.iv_camera);
        rl_profile = (RelativeLayout) findViewById(R.id.rl_profile);
        Typeface faceMedium = Typeface.createFromAsset(activity.getAssets(),
                "fonts/helvetica-neue-medium.ttf");
        tv_userProfile.setTypeface(faceMedium);
        tv_accounInfo.setTypeface(faceMedium);
        tv_first_name.setTypeface(faceMedium);
        tv_last_name.setTypeface(faceMedium);
        tv_email.setTypeface(faceMedium);
        tv_alias.setTypeface(faceMedium);
        tv_password.setTypeface(faceMedium);
        et_firstName.setTypeface(faceMedium);
        et_lastname.setTypeface(faceMedium);
        et_email.setTypeface(faceMedium);
        et_alias.setTypeface(faceMedium);
        tv_editAccount.setTypeface(faceMedium);
        tv_cancelMembership.setTypeface(faceMedium);
        tv_cardno.setTypeface(faceMedium);
//        tv_expiryDate.setTypeface(faceMedium);
        tv_accountNumber.setTypeface(faceMedium);
        tv_addnewCard.setTypeface(faceMedium);
        tv_memberdate.setTypeface(faceMedium);
        tv_changePassword.setTypeface(faceMedium);
        iv_userImage.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        tv_changePassword.setOnClickListener(this);
        tv_addnewCard.setOnClickListener(this);
        tv_editAccount.setOnClickListener(this);
        tv_cancelMembership.setOnClickListener(this);
//        tv_cancelMembership.setEnabled(false);
        backLayout.setOnClickListener(this);
        iv_cameraImage.setOnClickListener(this);
        iv_cameraImage.setEnabled(false);
        iv_userImage.setEnabled(false);
        if(appUtil.getPrefrence("is_follower").equals("1"))
        {
            billingLayout.setVisibility(View.VISIBLE);
        }
        else{
            billingLayout.setVisibility(View.GONE);

        }


    }

    private void checkCamera() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 11);

        } else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 11);

        } else {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // Error occurred while creating the File
                    return;
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = null;
                    try {
                        photoURI = FileProvider.getUriForFile(UserProfile.this,
                                BuildConfig.APPLICATION_ID + ".provider",
                                createImageFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
            }
        }

    }


    public void openAttachmentDialog() {
        try {
            final CharSequence[] items = {"Choose from Camera", "Choose from Gallery"};
            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
            builder.setTitle("Add Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Choose from Camera")) {
                        checkCamera();
                    } else if (items[item].equals("Choose from Gallery")) {
                        if (ActivityCompat.checkSelfPermission(UserProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(UserProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST);
                        } else {
                            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, GALLERY_REQUEST_CODE);
                        }
                        //galleryIntent();
                    }
                }


            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public String getRealPathFromURI(Uri uri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
comeFrom="onActivityResult";
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == CAMERA_REQUEST_CODE) { // Show the thumbnail on ImageView

                try {
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    File file = new File(imageUri.getPath());
                    ImageFileLocation = imageUri.getPath();
                    rotateImage(setReducedImageSize(ImageFileLocation));
                    isImageFromWeb = true;
                    profile.setProfile_pic(ImageFileLocation);
                    bitmapToBase64(rotateBitmap);
                    InputStream ims = new FileInputStream(file);

                } catch (FileNotFoundException e) {
                    return;
                }
            }
        }
    }


    private Bitmap setReducedImageSize(String filepath) {
        int targetImageWidth = iv_cameraImage.getWidth();
        int targetImageHeight = iv_cameraImage.getHeight();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, opts);
        int cameraImagewidth = opts.outWidth;
        int cameraImageheight = opts.outHeight;
        int scaleFactor = Math.min(cameraImagewidth / targetImageWidth, cameraImageheight / targetImageHeight);
        opts.inSampleSize = scaleFactor;
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filepath, opts);

    }

    private void rotateImage(Bitmap bitmap) {

        BitmapFactory.Options options;
        options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        File file = new File(ImageFileLocation);
        Bitmap newBmp = BitmapFactory.decodeFile(file.getAbsolutePath(), options);


        ExifInterface exifInterface1 = null;
        try {
            exifInterface1 = new ExifInterface(ImageFileLocation);


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


        rotateBitmap = Bitmap.createBitmap(newBmp, 0, 0, newBmp.getWidth(), newBmp.getHeight(), matrix, true);
        iv_userImage.setImageBitmap(rotateBitmap);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        encodeImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encodeImage;
    }


    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        BitmapFactory.Options options;

        try {
            options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Uri selectedImage = data.getData();
            String realImgPath = getRealPathFromURI(selectedImage);
            String[] filePath = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String mImagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            InputStream stream = getContentResolver().openInputStream(selectedImage);
            Bitmap yourSelectedImage;
            File file = new File(mImagePath);
            long fileInKb = file.length() / 1024;

            if (fileInKb > 800) {
                yourSelectedImage = BitmapFactory.decodeStream(stream, null, options);
            } else {
                yourSelectedImage = BitmapFactory.decodeStream(stream);
            }
            stream.close();
            //---orientation---

            int rotate = 0;
            try {
                ExifInterface exif = new ExifInterface(mImagePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            yourSelectedImage = Bitmap.createBitmap(yourSelectedImage, 0, 0, yourSelectedImage.getWidth(), yourSelectedImage.getHeight(), matrix, true);
            //  String base64 = encodeImage(yourSelectedImage);
            bitmapToBase64(yourSelectedImage);

            iv_userImage.setImageBitmap(yourSelectedImage);
            isImageFromWeb = true;
            profile.setProfile_pic(realImgPath);
            //  appUtil.setPrefrence("userProfile",realImgPath);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 11) {

            checkCamera();

        } else if (requestCode == WRITE_STORAGE_PERMISSION_REQUEST) {
            if (ActivityCompat.checkSelfPermission(UserProfile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_REQUEST_CODE);
            }
        } else if (requestCode == READ_STORAGE_PERMISSION_REQUEST) {
            if (ActivityCompat.checkSelfPermission(UserProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_camera:
                clicked = true;
                //   selectImage();
                openAttachmentDialog();

                break;
            case R.id.tv_submit:
                doSignup();
                break;

            case R.id.passwordChange:
                Intent intent = new Intent(this, UpdatePassword.class);
                startActivity(intent);
                break;

            case R.id.iv_profilePic:
                Intent i = new Intent(this, ProfileImageView.class);
                i.putExtra("profile", profile.getProfile_pic());
                i.putExtra("profileFromWeb", isImageFromWeb);
                startActivity(i);
                break;
            case R.id.tv_addnewCard:
                android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(activity);
                builder1.setMessage(getString(R.string.ask_changecard))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.dismiss();
                                try {
                                    if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                                         Intent intent1=new Intent(UserProfile.this, PaymentActivity.class);
                                         intent1.putExtra("type", "addcard");
                                         startActivity(intent1);



                                    } else {
                                        alertMessage = AlertMessage.newInstance(
                                                getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                                        alertMessage.show(getFragmentManager(), "");
                                    }
                                } catch (Exception e) {
                                }

                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.dismiss();
                            }
                        });
                android.support.v7.app.AlertDialog alert1 = builder1.create();
                alert1.show();



                break;
            case R.id.tv_editAccount:
               /* Intent intent2 = new Intent(activity, CardList.class);
                intent2.putExtra("type","delete");
                startActivity(intent2);*/
                break;
            case R.id.tv_cancelMembership:

android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
                builder.setMessage(getString(R.string.areYouSureCancelMember))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.dismiss();
                                try {
                                    if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                                        cancelMembership();
                                    } else {
                                        alertMessage = AlertMessage.newInstance(
                                                getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                                        alertMessage.show(getFragmentManager(), "");
                                    }
                                } catch (Exception e) {
                                }

                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.dismiss();
                            }
                        });
                android.support.v7.app.AlertDialog alert = builder.create();
                alert.show();
                break;

            case R.id.backLayout:
                this.onBackPressed();
        }

    }



    public void displayAlert(final View focus, EditText et, String msg, String t) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, focus.getTop());
            }
        });
        et.requestFocus();
        alertMessage = AlertMessage.newInstance(
                msg, getString(R.string.ok), t);
        alertMessage.show(this.getFragmentManager(), "");
    }


    private void doSignup() {
        updateProfile();
    }

    public void updateProfile() {
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

            circularProgressBar1.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            try {
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("first_name", et_firstName.getText().toString().trim());
                jsonParam.put("last_name", et_lastname.getText().toString().trim());
                jsonParam.put("username", et_alias.getText().toString().trim());
                if (encodeImage != null)
                    jsonParam.put("profile_pic", encodeImage);
                else
                    jsonParam.put("profile_pic", "");


                final String mRequestBody = jsonParam.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.WEB_URL + Constant.UPDATEPROFILE, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        circularProgressBar1.setVisibility(View.GONE);

                        Log.i("VOLLEY", response);

                        try {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            et_firstName.requestFocus();
                            JSONObject jsonParam = new JSONObject(response.toString());
                            String status = jsonParam.getString("status");
                            String code = jsonParam.getString("code");
                            String message = jsonParam.getString("message");
                            if (code.equals("10")) {
                                JSONArray result = jsonParam.getJSONArray("result");


                                // circularProgressBar.setVisibility(View.VISIBLE);
                                if (result.length() > 0) {
                                    alertMessage = AlertMessage.newInstance(
                                            message, getString(R.string.ok), status);
                                    alertMessage.show(UserProfile.this.getFragmentManager(), "");
                                    for (int i = 0; i < result.length(); i++) {
                                        profile = new Profile();
                                        JSONObject jsonObject = result.getJSONObject(i);
                                        JSONObject jsonObject1 = jsonObject.getJSONObject("updated_profile");
                                        try {
                                            appUtil.setPrefrence("first_name", jsonObject1.getString("first_name"));
                                            appUtil.setPrefrence("last_name", jsonObject1.getString("last_name"));
                                            profile.setUsername(jsonObject1.getString("username"));
                                            isImageFromWeb = false;
                                            profile.setProfile_pic(jsonObject1.getString("profile_pic"));
                                            appUtil.setPrefrence("profile_pic",jsonObject1.getString("profile_pic"));
                                            et_firstName.setText(appUtil.getPrefrence("first_name"));
                                            et_lastname.setText(appUtil.getPrefrence("last_name"));
                                            et_email.setText(appUtil.getPrefrence("email"));
                                            et_alias.setText(profile.getUsername());


                                        } catch (Exception e) {
                                            circularProgressBar1.setVisibility(View.GONE);

                                            e.printStackTrace();
                                        }


                                    }

                                } else {

                                    alertMessage = AlertMessage.newInstance(
                                            message, getString(R.string.ok), getString(R.string.alert));
                                    alertMessage.show(UserProfile.this.getFragmentManager(), "");
                                    // Toast.makeText(UserProfile.this, message, Toast.LENGTH_LONG).show();
                                }


                            } else {

                                alertMessage = AlertMessage.newInstance(
                                        message, getString(R.string.ok), status);
                                alertMessage.show(UserProfile.this.getFragmentManager(), "");
                                //  Toast.makeText(UserProfile.this, message, Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            et_firstName.requestFocus();

                            circularProgressBar.setVisibility(View.GONE);
                            circularProgressBar1.setVisibility(View.GONE);

                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        circularProgressBar.setVisibility(View.GONE);
                        circularProgressBar1.setVisibility(View.GONE);

                        Log.i("Volley error resp", "error----" + error.getMessage());
                        // progressDialog.dismiss();

                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                Toast.makeText(UserProfile.this, "Time out error", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (error.networkResponse.statusCode == 401) {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                    ApiController apiController = new ApiController(UserProfile.this);
                                    apiController.userLogin(UserProfile.this);
                                } else {
                                    alertMessage = AlertMessage.newInstance(
                                            getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                                    alertMessage.show(UserProfile.this.getFragmentManager(), "");
                                }

                            }
                        }

                    }


                })
                {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }


                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                            return null;
                        }
                    }


                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json ;charset=utf-8");
                        headers.put("Authorization", "Bearer " + appUtil.getPrefrence("accessToken"));
                        ;
                        //  headers.put("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjZiYTQ0YzAxMmRjYmE0YjBkMjRmYTQ3N2JmNTQ3M2M5YWUyOTU0NGEyODdjMTJlNmI3YzhjNjU1ZjJjNmQwZGU3NmEwYzVmZjkwYTQ4OWEwIn0.eyJhdWQiOiIxIiwianRpIjoiNmJhNDRjMDEyZGNiYTRiMGQyNGZhNDc3YmY1NDczYzlhZTI5NTQ0YTI4N2MxMmU2YjdjOGM2NTVmMmM2ZDBkZTc2YTBjNWZmOTBhNDg5YTAiLCJpYXQiOjE1MjgwODY2MjAsIm5iZiI6MTUyODA4NjYyMCwiZXhwIjoxNTU5NjIyNjIwLCJzdWIiOiIxODciLCJzY29wZXMiOltdfQ.LSIZV1YaHcZXNL7j8RfincUf0o_trNByt0rXLuOiaAfJKzBmbZ4vaNZWK_CbDK-BiAWlTtFajkeeUdvIBtPX4edz7dvxQhtrmgw1v7lqEJ24_pnmE0OGfL7MDVONQoICFeIYnok3S-9WUnsLNLicbbTqLAET5BZG3Dfmx2B6fVR7twdesfHRGZx6RMP7gWUfUH_Y8z4MKipOf1vspV4C0ySI1BcifAI8qEwvT3GQ4bDNaMwy_RBlMvXe5LYsZHtUVZ5O08TievgJ3G5z5yWaJfNR_AKlKpJkB8MSYwK2smKmpBLtbhKA3rOSZjauOzfiQ0_kTeW5X_cAoKAm5-I-XbxJdytdV3DRHLb60LsfUed0d8c78KOEQc3p4RIxakAx4sfvGN8uGV2NzgRhyOK0KUOdKFuotB-d8DJexa1bTc94WRIICE5fcM-SfcKoSzLY-yCeFKqFHrVa2V0ITTFpfDZg6qhrVrT_D1CZ_ZXL7op2f74_UfOsbhNcUTB0W0EBliA2FywYsIPySGvtgreKBYUc5zMpT_fBGWAzrdHCbUJGx4qrFQrsxKbtIlXJIrVC0FN-Ucf-wHjWNFl2IxGV0Nds1lm_xGy52SxU93ScSw0SNp8egk3nyJAIpeuhnxE9Z1W_yVNDsuvdSvctQYUK0uqFhczbrkbaF12Foc7nepE");
                        return headers;
                    }


                };

                AlphaApplication.getInstance().addToRequestQueue(stringRequest);
            } catch (Exception e) {
                e.printStackTrace();
//                progressDialog.dismiss();
            }


        } else {
            circularProgressBar.setVisibility(View.GONE);
            circularProgressBar1.setVisibility(View.GONE);


            alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
            alertMessage.show(UserProfile.this.getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
        }


    }

  public void getProfile() {

      if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
          try {
              getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                      WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
              circularProgressBar.setVisibility(View.VISIBLE);
              JSONObject jsonParam = new JSONObject();

              Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
              JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constant.WEB_URL + Constant.GETPROFILE, null,
                      new Response.Listener<JSONObject>() {
                          @Override
                          public void onResponse(JSONObject response) {
                              try {
                                  Log.i("Alpha response", response.toString());
                                  JSONObject jsonParam = new JSONObject(response.toString());
                                  String status = jsonParam.getString("status");
                                  String code = jsonParam.getString("code");
                                  String message = jsonParam.getString("message");

                                  if (code.equals("10")) {
                                      JSONArray result = jsonParam.getJSONArray("result");
                                      if (result.length() > 0) {


                                          for (int i = 0; i < result.length(); i++) {
                                              profile = new Profile();
                                              JSONObject jsonObject = result.getJSONObject(i);

                                              JSONObject jsonObject1 = jsonObject.getJSONObject("profile");


                                              try {
                                                  if (jsonObject1.getString("profile_pic").equalsIgnoreCase("")) {
                                                      circularProgressBar.setVisibility(View.GONE);
                                                      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                      iv_cameraImage.setEnabled(true);
                                                      iv_userImage.setEnabled(true);

                                                  } else {
                                                      appUtil.setPrefrence("profile_pic",jsonObject1.getString("profile_pic"));
                                                      Picasso.with(UserProfile.this).load(jsonObject1.getString("profile_pic"))
                                                              .placeholder(R.drawable.user_pic).into(iv_userImage, new Callback() {
                                                          @Override
                                                          public void onSuccess() {

                                                              circularProgressBar.setVisibility(View.GONE);
                                                              getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                                                              iv_cameraImage.setEnabled(true);
                                                              iv_userImage.setEnabled(true);
                                                          }


                                                          @Override
                                                          public void onError() {
                                                              getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                              // Toast.makeText(activity, "No Image Found", Toast.LENGTH_SHORT).show();
                                                              alertMessage = AlertMessage.newInstance(
                                                                      "No Image Found", getString(R.string.ok), getString(R.string.alert));
                                                              alertMessage.show(UserProfile.this.getFragmentManager(), "");

                                                              circularProgressBar.setVisibility(View.GONE);
                                                              iv_cameraImage.setEnabled(true);
                                                              iv_userImage.setEnabled(true);
                                                          }
                                                      });
                                                  }

                                                  //  iv_userImage.setImageBitmap(BitmapFactory.decodeFile(appUtil.getPrefrence("userProfile")));
                                                  profile.setId(jsonObject1.getString("id"));
                                                  if (jsonObject1.getString("username").equalsIgnoreCase("null")) {
                                                      profile.setUsername("");

                                                  } else {
                                                      profile.setUsername(jsonObject1.getString("username"));

                                                  }
                                                  if (jsonObject1.getString("first_name").equalsIgnoreCase("null")) {
                                                      et_firstName.setText("");

                                                  } else {
                                                      appUtil.setPrefrence("first_name", jsonObject1.getString("first_name"));

                                                  }

                                                  if (jsonObject1.getString("last_name").equalsIgnoreCase("null")) {
                                                      et_lastname.setText("");

                                                  } else {
                                                      appUtil.setPrefrence("last_name", jsonObject1.getString("last_name"));

                                                  }

                                                  if (jsonObject1.getString("email").equalsIgnoreCase("null")) {
                                                      et_email.setText("");

                                                  } else {
                                                      appUtil.setPrefrence("email", jsonObject1.getString("email"));

                                                  }
                                                  profile.setProfile_pic(jsonObject1.getString("profile_pic"));
                                                  profile.setExp_month(jsonObject1.getString("exp_month"));
                                                  profile.setExp_year(jsonObject1.getString("exp_year"));
                                                  profile.setLast4(jsonObject1.getString("last4"));
                                                  profile.setLast4(jsonObject1.getString("last4"));
                                                  profile.setCreated_at(jsonObject1.getString("created_at"));

                                                  try {
                                                      String date = profile.getCreated_at();
                                                      SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                                      Date newDate = spf.parse(date);
                                                      spf = new SimpleDateFormat("MMMM d, yyyy");
                                                      date = spf.format(newDate);
                                                      System.out.println(date);
                                                      tv_memberdate.setText(getString(R.string.member_date)+" "+date);
                                                  } catch (ParseException e) {
                                                      e.printStackTrace();
                                                  }
                                                  if(jsonObject1.getString("last4").equalsIgnoreCase("null"))
                                                  {
                                                      tv_accountNumber.setText(" ");
                                                  }
                                                  else {
                                                      tv_accountNumber.setText("**** **** **** " + jsonObject1.getString("last4"));
//                                                      tv_accountNumber.setText("Card Detail : "+"**** **** **** " + jsonObject1.getString("last4"));
                                                  }
                                                 /* if(jsonObject1.getString("exp_month").equalsIgnoreCase("null")&&jsonObject1.getString("exp_year").equalsIgnoreCase("null"))
                                                  {
                                                      tv_expiryDate.setText(" ");
                                                  }
                                                  else {
                                                      try {
                                                          tv_expiryDate.setText(prefixZero(Integer.parseInt(jsonObject1.getString("exp_month"))) + "/" + getLastnCharacters(jsonObject1.getString("exp_year"), 2));
//                                                          tv_expiryDate.setText("valid till: "+prefixZero(Integer.parseInt(jsonObject1.getString("exp_month"))) + "/" + getLastnCharacters(jsonObject1.getString("exp_year"), 2));
                                                      } catch (Exception e) {
                                                      }
                                                  }*/
                                                  et_firstName.setText(appUtil.getPrefrence("first_name"));
                                                  et_lastname.setText(appUtil.getPrefrence("last_name"));
                                                  et_email.setText(appUtil.getPrefrence("email"));
                                                  et_alias.setText(profile.getUsername());


                                              } catch (Exception e) {
                                                  e.printStackTrace();
                                              }


                                          }

                                      } else {
                                          et_firstName.setText(appUtil.getPrefrence("first_name"));
                                          et_lastname.setText(appUtil.getPrefrence("last_name"));
                                          et_email.setText(appUtil.getPrefrence("email"));

                                          //Toast.makeText(UserProfile.this, getString(R.string.noData), Toast.LENGTH_LONG).show();
                                      }
                                      //    progressDialog.dismiss();

                                  } else {
                                      getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                              WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                      alertMessage = AlertMessage.newInstance(
                                              message, getString(R.string.ok), getString(R.string.alert));
                                      alertMessage.show(UserProfile.this.getFragmentManager(), "");
                                      //  Toast.makeText(UserProfile.this, message, Toast.LENGTH_LONG).show();
                                      //   progressDialog.dismiss();
                                  }
                              } catch (Throwable e) {
                                  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                  //   progressDialog.dismiss();
                                  Log.i("Excep", "error----" + e.getMessage());
                                  e.printStackTrace();


                              }


                          }
                      }, new Response.ErrorListener() {
                  @Override
                  public void onErrorResponse(VolleyError error) {
                      Log.i("Volley error resp", "error----" + error.getMessage());
                      // progressDialog.dismiss();
                      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                      if (error.networkResponse == null) {
                          if (error.getClass().equals(TimeoutError.class)) {
                              Toast.makeText(UserProfile.this, "Time out error", Toast.LENGTH_LONG).show();
                          }
                      } else {
                          if (error.networkResponse.statusCode == 401) {
                              if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {

                                  ApiController apiController = new ApiController(UserProfile.this);
                                  apiController.userLogin(UserProfile.this);
                              } else {
                                  alertMessage = AlertMessage.newInstance(
                                          getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                                  alertMessage.show(UserProfile.this.getFragmentManager(), "");
                              }

                          }
                      }

                  }
              })


              {    //this is the part, that adds the header to the request
                  @Override
                  public Map<String, String> getHeaders() {
                      Map<String, String> params = new HashMap<String, String>();
                      params.put("Content-Type", "application/json ;charset=utf-8");
                      params.put("Authorization", "Bearer " + appUtil.getPrefrence("accessToken"));
                      //  params.put("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImU4MGJhOTM5YzE1N2VmYzdmNGJjMjczZTc3NTI1NDZjYjk3MzkyNDMzNGIxM2RjNmVjZDg1ZTFjNTBjNDNlYTI5MTMwNmIxMGVmYjBjMmM2In0.eyJhdWQiOiIxIiwianRpIjoiZTgwYmE5MzljMTU3ZWZjN2Y0YmMyNzNlNzc1MjU0NmNiOTczOTI0MzM0YjEzZGM2ZWNkODVlMWM1MGM0M2VhMjkxMzA2YjEwZWZiMGMyYzYiLCJpYXQiOjE1MjgxNzQ1NzksIm5iZiI6MTUyODE3NDU3OSwiZXhwIjoxNTU5NzEwNTc5LCJzdWIiOiIxODciLCJzY29wZXMiOltdfQ.mOQM36HKxHFcq4L27YylIYSQcsEz8T1wit4RnacVBB4O3C4ZM2GcXCK5uGE-J--OwH45EB6m5y8e5GtmHUgtvmt0-NmJDM48p_wTScplsMRQ4AUr4xSkE8nRwV73b8G9fjNJy78tnxJbtUUfDZMbj6SZ2E0aK1ftU1n_ycW0FY3XqRSLcSOHf8Zz7zH98CGQu5x01DLGLHRq8oJ4U_gqzSKM71U0Qv0tR0Pz1yxGNOXYcrfDmVSVoVE5etpvf6Onxp4jAh_mr-XnnezcLmEDq7bUv6J3S7wpDpMIJZerKA00HHgWXgGfGyvgeNz3Cq6cjpVTTw8qCeaEj-QFXmMFNQxvbZo7ID93bNBFYLz6BXpje8YGF51CYFVF9jpARCfoKsBs5_VIpRxlmMgkobrK0ccxUO86DyaUCqlyaFpp5m1IjvNBlP7N7ZTbeHI3SRdFxvRqTNWVcuYGkwHwTQ6aZ2gjc0FWOSBg0E4K_32kEKDjg1QW-t2T48xoCHE0eOg0iveaNimSvKIyxUaTFNjjjkIGHmSbJR5MFF5F5ZvrjB1J_caVODvCNNHqjWoUn5DI2Cxdhea_KXs6YKkKPXjpvzijZiy64q6q1kTrbyubi8Zd3AvTdVHYed7szu_odZPzyHfeBrhXZOpzeGqcISMlehCM-sWmKdhGxnZl5jg_QUA");

                      return params;
                  }
              };

              RetryPolicy policy = new DefaultRetryPolicy
                      (50000,
                              DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
              jsonObjectRequest.setRetryPolicy(policy);


              AlphaApplication.getInstance().addToRequestQueue(jsonObjectRequest);

          } catch (Exception e) {
              e.printStackTrace();
//                progressDialog.dismiss();
              getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

          }

      } else {
          getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

          alertMessage = AlertMessage.newInstance(
                  getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
          alertMessage.show(UserProfile.this.getFragmentManager(), "");
//            Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show();
      }


  }

    @Override
    public void onSuccessResponse(String code, String message, String status) {
        if (code.equals("10")) {
            if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                if(type.equals("cancel"))
                {
                    cancelMembership();
                }
                else{
                getProfile();
                }
            } else {
                AlertMessage alertMessage = AlertMessage.newInstance(
                        getString(R.string.noInternet), getString(R.string.ok), getString(R.string.alert));
                alertMessage.show(UserProfile.this.getFragmentManager(), "");
            }
        } else {
            isFinish = true;
            alertMessage = AlertMessage.newInstance(
                    message, getString(R.string.ok), status);
            alertMessage.show(UserProfile.this.getFragmentManager(), "");

        }
    }

    @Override
    public void onDialogPositiveWithoutViewClick(DialogFragment dialog) {
        if(isFinish)
        {
            isFinish=false;
            dialog.dismiss();
            finish();
        }
        else {
            dialog.dismiss();
        }

    }
    public  String prefixZero(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public String getLastnCharacters(String inputString,
                                     int subStringLength){
        int length = inputString.length();
        if(length <= subStringLength){
            return inputString;
        }
        int startIndex = length-subStringLength;
        return inputString.substring(startIndex);
    }

    private void cancelMembership() {
        if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
             progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Processing");
            progressDialog.show();
//            progressDialog.setVisibility(View.VISIBLE);
            try {
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("subscription_id", appUtil.getPrefrence("subscription_id"));
                jsonParam.accumulate("customer_id", appUtil.getPrefrence("customer_id"));
                Log.i("Alpha", "jsonparam -----" + jsonParam.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.WEB_URL + Constant.CANCEL_SUBSCRIPTION, jsonParam,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("Alpha response", response.toString());
                                    JSONObject jsonParam = new JSONObject(response.toString());
                                    String status = jsonParam.getString("status");
                                    String code = jsonParam.getString("code");
                                    String message = jsonParam.getString("message");
                                    if (code.equals("10")) {

                                        progressDialog.dismiss();
                                        /*alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        alertMessage.show(getFragmentManager(), "");*/
                                        appUtil.setPrefrence("isLogin","no");
                                        appUtil.setPrefrence("isPayment","no");
                                        Intent i=new Intent(activity, Login.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                    else  {

                                        progressDialog.dismiss();
                                        alertMessage = AlertMessage.newInstance(
                                                message, getString(R.string.ok),status);
                                        alertMessage.show(getFragmentManager(), "");
                                    }

                                } catch (Throwable e) {
                                    progressDialog.dismiss();
                                    Log.i("Excep", "error----" + e.getMessage());
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley error resp", "error----" + error.getMessage());
                        progressDialog.dismiss();
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {

                                Toast.makeText(activity, "Time out error", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            if(error.networkResponse.statusCode==401)
                            {
                                if (connectStatus != NetworkUtils.TYPE_NOT_CONNECTED) {
                                     type="cancel";
                                    ApiController apiController =new ApiController(UserProfile.this);
                                    apiController.userLogin(UserProfile.this);
                                }
                                else{
                                    alertMessage = AlertMessage.newInstance(
                                            getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
                                    alertMessage.show(getFragmentManager(), "");
                                }


                            }
                        }
                    }
                }){    //this is the part, that adds the header to the request
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json ;charset=utf-8");
                        params.put("Authorization", "Bearer "+appUtil.getPrefrence("accessToken"));
                        return params;
                    }
                };

                RetryPolicy policy = new DefaultRetryPolicy
                        (50000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsonObjectRequest.setRetryPolicy(policy);
                AlphaApplication.getInstance().addToRequestQueue(jsonObjectRequest);

            } catch (Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }

        } else {

            alertMessage = AlertMessage.newInstance(
                    getString(R.string.noInternet), getString(R.string.ok),getString(R.string.alert));
            alertMessage.show(getFragmentManager(), "");
        }
    }

}
